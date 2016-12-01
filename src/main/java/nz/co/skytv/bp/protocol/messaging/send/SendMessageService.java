package nz.co.skytv.bp.protocol.messaging.send;


/**
 * SendMessageService takes a bridge message and passes it to the correct NDS GAS stream
 * server socket.
 */
public interface SendMessageService {

  /**
   * messages for legacy NDS GAS are low level socket messages
   * @param endpointId
   * @param messageText
   */
  void sendMessage(String endpointId, String messageText) throws EndpointLocationNotConfiguredException;
}
