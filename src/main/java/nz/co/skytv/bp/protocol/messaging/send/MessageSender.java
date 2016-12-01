package nz.co.skytv.bp.protocol.messaging.send;


public interface MessageSender {


  /**
   * MessageSender uses the ServerSocket ID to connect with the correct NDS Streamserver
   * port<br>
   * 
   * @param message
   */
  void sendMessage(byte[] message);


}
