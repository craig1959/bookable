package nz.co.skytv.bp.protocol.messaging;

import nz.co.skytv.bp.protocol.model.EndpointType;


public interface MessageHandler {

  /**
   * If checkHeartbeat is enabled in configuration {@link EndpointType}<br>
   * A response is returned to the connected socket.<br>
   * The response format is the same message returned with an R| at the start.<br>
   * example R|S|SPORT1 |PSS07687 |1\0x0d\0x0a..<br>
   * For other messages such as start messages they are passed to the
   * {@link MessageResponder}.
   * 
   * @param message
   */
  void handleMessage(byte[] message);

}
