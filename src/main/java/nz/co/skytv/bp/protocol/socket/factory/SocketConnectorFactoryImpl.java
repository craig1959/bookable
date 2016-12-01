package nz.co.skytv.bp.protocol.socket.factory;

import static nz.co.skytv.bp.protocol.constants.ConverterConstants.HARRIS;
import static nz.co.skytv.bp.protocol.constants.ConverterConstants.NDS;
import static nz.co.skytv.bp.protocol.constants.ConverterConstants.PROBEL;
import nz.co.skytv.bp.protocol.model.EndpointType;
import nz.co.skytv.bp.protocol.socket.client.HarrisSocketConnector;
import nz.co.skytv.bp.protocol.socket.client.ProbelSocketConnector;
import nz.co.skytv.bp.protocol.socket.common.SocketConnector;
import nz.co.skytv.bp.protocol.socket.server.ServerSocketConnector;
import nz.co.skytv.spring.util.FactoryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class SocketConnectorFactoryImpl implements SocketConnectorFactory {

  @Autowired
  FactoryHelper helper;

  @Override
  public SocketConnector createSocketConnector(EndpointType endpoint) {

    SocketConnector socketConnector = null;

    if (endpoint.getType().equalsIgnoreCase(NDS)) {
      socketConnector = new ServerSocketConnector(endpoint);
    } else if (endpoint.getType().equalsIgnoreCase(HARRIS)) {
      socketConnector = new HarrisSocketConnector(endpoint);
    } else if (endpoint.getType().equalsIgnoreCase(PROBEL)) {
      socketConnector = new ProbelSocketConnector(endpoint);
    }
    return helper.enhance(socketConnector);
  }
}
