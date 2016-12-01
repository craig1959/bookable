package nz.co.skytv.bp.protocol.socket.factory;

import nz.co.skytv.bp.protocol.model.EndpointType;
import nz.co.skytv.bp.protocol.socket.common.SocketConnector;


/**
 * SocketConnectorFactory builds the appropriate socket connector.<br>
 * Both client and server, based on configuration end points.
 */
public interface SocketConnectorFactory {

  /**
   * construct a SocketConnector from supplied {@link EndpointType}.
   * 
   * @param endpointType
   * @return {@link SocketConnector}
   */
  SocketConnector createSocketConnector(EndpointType endpointType);

}
