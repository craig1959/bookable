package nz.co.skytv.bp.protocol.endpoint.lifecycle;

import java.util.HashMap;
import java.util.Map;
import nz.co.skytv.bp.protocol.model.EndpointType;
import nz.co.skytv.bp.protocol.socket.common.SocketConnector;
import nz.co.skytv.bp.protocol.socket.factory.SocketConnectorFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class EndpointLifecycleServiceImpl implements EndpointLifecycleService {

  private static final Logger log = LoggerFactory.getLogger(EndpointLifecycleServiceImpl.class);

  @Autowired
  SocketConnectorFactoryImpl socketConnectorFactory;

  private Map<String, nz.co.skytv.bp.protocol.socket.common.SocketConnector> socketMap = new HashMap<String, SocketConnector>();

  @Override
  public void initiateEndpoint(EndpointType endpoint) throws InterruptedException {
    SocketConnector socketConnector = socketConnectorFactory.createSocketConnector(endpoint);
    if (socketConnector != null) {
      socketMap.put(endpoint.getId(), socketConnector);
      socketConnector.start();
    } else {
      log.error("Initiate endpoint of type '{}' failed", endpoint.getType());
    }
  }

  @Override
  public void destroyEndpoint(EndpointType endpoint) throws EndpointLifeCycleException {
    SocketConnector socketConnector = socketMap.get(endpoint.getId());
    if (null != socketConnector) {
      log.info("destroying endpoint '{}'", endpoint.getId());
      socketConnector.stop();
      socketMap.remove(endpoint.getId());
    } else {
      throw new EndpointLifeCycleException(String.format("Destroy Endpoint id=%s failed ", endpoint.getId()));
    }
  }

  @Override
  public void reconfigureEndpoint(EndpointType newEndpoint) throws EndpointLifeCycleException, InterruptedException {

    SocketConnector existingSocketConnector = socketMap.get(newEndpoint.getId());
    if (null != existingSocketConnector) {
      existingSocketConnector.stop();
      SocketConnector newSocketConnector = socketConnectorFactory.createSocketConnector(newEndpoint);
      if (newSocketConnector != null) {
        socketMap.put(newEndpoint.getId(), newSocketConnector);
        newSocketConnector.start();
      } else {
        throw new EndpointLifeCycleException(String.format("Reconfigure endpoint of type %s, failed to create socket connector",
            newEndpoint.getId()));
      }
    } else {
      throw new EndpointLifeCycleException(String.format("Reconfigure Endpoint of type %s, failed to find in socket map ",
          newEndpoint.getId()));
    }
  }


}
