package nz.co.skytv.bp.protocol.config;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import nz.co.skytv.bp.protocol.endpoint.lifecycle.EndpointLifeCycleException;
import nz.co.skytv.bp.protocol.endpoint.lifecycle.EndpointLifecycleServiceImpl;
import nz.co.skytv.bp.protocol.model.ConfigType;
import nz.co.skytv.bp.protocol.model.EndpointType;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;


/**
 * Config service is the heart of managing the bookablepromo-agent endpoints.<br>
 * Control of the endpoint lifecycle, initialise, reconfigure, destroy.<br>
 * Here we have the Config file watcher, this runs on its own thread<br>
 * and allows the endpoints to be reconfigured when the config.xml file is edited<br>
 * without restarting the application.
 */
@Service
public class ConfigService implements ApplicationListener<ApplicationContextEvent> {

  private static final Logger log = LoggerFactory.getLogger(ConfigService.class);

  @Value("${endpointconfiguration.configFile}")
  String configFileName;

  @Autowired
  ConfigXmlFileClassLoader configLoader;
  @Autowired
  EndpointLifecycleServiceImpl endpointLifecycleService;

  Map<String, EndpointType> currentEndpoints = new HashMap<String, EndpointType>();

  File xmlFile;
  ConfigWatcher configWatcher = new ConfigWatcher();

  private boolean isrunning;

  @Override
  public void onApplicationEvent(ApplicationContextEvent event) {
    if (event instanceof ContextRefreshedEvent && !isrunning) {
      log.debug("ApplicationContextEvent is ContextRefreshedEvent");
      xmlFile = new File(configFileName);
      try {

        loadConfig();
        initiateEndpoints();
      } catch (ConfigFileLoaderException e) {
        log.error("Error while loading config file", e);
      }
      //startup file watcher on a thread
      startConfigFileWatcher();
      isrunning = true;
    }
    else if (event instanceof ContextClosedEvent && isrunning) {
      log.debug("ApplicationContextEvent is ContextClosedEvent.");

      destroyAllEndpoints();
      //kill file watcher thread
      log.debug("closing file watcher thread");
      configWatcher.shutdown = true;
      isrunning = false;
    }
  }


  public void loadConfig() throws ConfigFileLoaderException {
    nz.co.skytv.bp.protocol.model.ConfigType config = configLoader.parseFile(xmlFile);

    for (nz.co.skytv.bp.protocol.model.EndpointType e : config.getEndpoint()) {
      currentEndpoints.put(e.getId(), e);
    }
  }

  public void updateConfig() throws ConfigFileLoaderException {
    Map<String, EndpointType> newEndpoints = new HashMap<String, EndpointType>();

    ConfigType newConfig = configLoader.parseFile(xmlFile);

    for (EndpointType e : newConfig.getEndpoint()) {
      newEndpoints.put(e.getId(), e);
    }
    processEndpoints(newEndpoints);
    //update current map
    currentEndpoints = newEndpoints;

  }


  /**
   * compare new endpoints to old, destroy unused ones,<br>
   * reconfigure changed ones,<br>
   * initiate new ones
   * @param newEndpoints
   */
  protected void processEndpoints(Map<String, EndpointType> newEndpoints) {

    try {
      for (Map.Entry<String, EndpointType> entry : currentEndpoints.entrySet()) {
        EndpointType currentEndpoint = entry.getValue();
        EndpointType newEndpoint = newEndpoints.get(entry.getKey());

        if (newEndpoint == null) {
          endpointLifecycleService.destroyEndpoint(currentEndpoint);
        } else {
          //has end point changed? compare and reconfigure
          if (!compareEndpoints(currentEndpoint, newEndpoint)) {
            endpointLifecycleService.reconfigureEndpoint(newEndpoint);
          }
        }
      }

    } catch (EndpointLifeCycleException | InterruptedException e) {
      log.error("error while processing endpoints", e);
    }
    initiateNewEndpoints(newEndpoints);
  }


  private boolean compareEndpoints(EndpointType currentEndpoint, EndpointType newEndpoint) {
    return EqualsBuilder.reflectionEquals(currentEndpoint, newEndpoint);
  }


  private void initiateEndpoints() {
    try {
      for (Entry<String, EndpointType> entry : currentEndpoints.entrySet()) {
        endpointLifecycleService.initiateEndpoint(entry.getValue());
      }
    } catch (InterruptedException e) {
      log.error("error while initiating current endpoints", e);
    }
  }

  private void initiateNewEndpoints(Map<String, EndpointType> newEndpoints) {
    Set<String> addedEndpointKeys = new HashSet<String>(newEndpoints.keySet());
    addedEndpointKeys.removeAll(currentEndpoints.keySet());
    try {
      for (String key : addedEndpointKeys) {
        endpointLifecycleService.initiateEndpoint(newEndpoints.get(key));
      }
    } catch (InterruptedException e) {
      log.error("error while initiating new endpoints", e);
    }
  }

  private void destroyAllEndpoints() {
    for (Map.Entry<String, EndpointType> endpointEntry : currentEndpoints.entrySet()) {
      try {
        endpointLifecycleService.destroyEndpoint(endpointEntry.getValue());
      } catch (EndpointLifeCycleException e) {
        log.error("error while destroying current endpoints", e);
      }
    }
  }

  public void startConfigFileWatcher() {
    Thread thread = new Thread(configWatcher);
    thread.setDaemon(true);
    thread.start();
  }

  /**
   * Given the need for High Availability,<br>
   * restarting the application could cause an outage.<br>
   * if there is a need for a change in configuration.<br>
   * the application will reconfigure itself when the config file changes.
   * 
   */
  private class ConfigWatcher implements Runnable {

    private boolean shutdown = false;

    public void watchConfig() {

      Path filePath = Paths.get(xmlFile.getAbsolutePath());
      Path fileDirPath = filePath.getParent();

      try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
        fileDirPath.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

        while (!shutdown && !Thread.currentThread().isInterrupted()) {
          WatchKey watchKey;
          try {
            watchKey = watcher.take();
            if (!watchKey.isValid()) {
              continue;
            }
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
          }

          for (WatchEvent<?> watchEvent : watchKey.pollEvents()) {
            log.debug(String.format("Watch Event: context = %s, count = %d%n", watchEvent.context(), watchEvent.count()));
            processWatchEvent(filePath, fileDirPath, watchEvent);
          }
          watchKey.reset();
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    private void processWatchEvent(Path filePath, Path fileDirPath, WatchEvent<?> watchEvent) throws ConfigFileLoaderException {
      if (watchEvent.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {

        Path context = (Path) watchEvent.context();
        Path absolueContext = fileDirPath.resolve(context);
        updateConfigIfSameFileContext(filePath, absolueContext);
      }
    }

    private void updateConfigIfSameFileContext(Path filePath, Path absolueContext) throws ConfigFileLoaderException {
      if (absolueContext.equals(filePath)) {
        log.debug(String.format("File %s has been updated.  Loading new config %n", xmlFile));
        // get updated config
        ConfigType config = configLoader.parseFile(xmlFile);
        log.debug(String.format("Config is now = %s%n", config));
        updateConfig();
      }
    }

    @Override
    public void run() {
      watchConfig();
    }

  }
}
