package nz.co.skytv.bp.protocol.config;

import static org.fest.assertions.Assertions.assertThat;
import javax.xml.soap.MessageFactory;
import nz.co.skytv.bp.protocol.endpoint.lifecycle.EndpointLifecycleServiceImpl;
import nz.co.skytv.bp.protocol.messaging.RoutableMessageResponder;
import nz.co.skytv.bp.protocol.messaging.send.SendMessageServiceImpl;
import nz.co.skytv.bp.protocol.model.EndpointType;
import nz.co.skytv.bp.protocol.spring.BaseConfiguration;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BaseConfiguration.class, ConfigService.class,
    EndpointLifecycleServiceImpl.class, ConfigXmlFileClassLoader.class, RoutableMessageResponder.class,
    SendMessageServiceImpl.class, MessageFactory.class })
public class ConfigFileIntegrationTest {

  private static final Logger log = LoggerFactory.getLogger(ConfigFileIntegrationTest.class);

  //these are integration tests, actually reading the config.xml file.

  @Autowired
  ConfigService configService;


  @Ignore
  @Test
  public void fileLoadTest() throws ConfigFileLoaderException, InterruptedException {

    configService.loadConfig();

    for (EndpointType e : configService.currentEndpoints.values()) {
      log.info("host = {} port= {}", e.getHost(), e.getPort());
      assertThat(e.getHost()).isNotNull();
    }
    Thread.sleep(240000);
  }

  @Ignore
  @Test
  public void watcherTest() throws Exception {

    log.info("ConfigWatcher is starting");

    configService.startConfigFileWatcher();

    //run test while thread is sleeping edit and save config.xml
    //check debug logging from ConfigWatcher
    Thread.sleep(30000);

    log.info("ConfigWatcher is ending");
  }


}
