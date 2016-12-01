package nz.co.skytv.bp.protocol.messaging;

import nz.co.skytv.bp.protocol.rest.controller.GreenButtonResetController;


/**
 * MessageResponder is for converting byte[] messages from a ClientSocket<br>
 * using {@link MessageFactory} to convert to Message type needed for target system
 * 
 */
public interface MessageResponder {


  /**
   * handleMessage gets from database the routing details. <br>
   * Then uses the {@link MessageFactory} to convert to Message type needed for target
   * system
   * 
   * @param byteMessage the incoming message from automation<br>
   *        eg S|SKY MOVIES 1 HD |PPMB0001 |12:33:45:00
   * @param messageSource the automation system source eg PROBEL1, HARRIS2
   */
  void handleMessage(byte[] byteMessage, String messageSource);

  /**
   * If a green button is stuck on screen. The rest webservice<br>
   * {@link GreenButtonResetController} passes in the channelName.<br>
   * The location of this channel name is extracted from the Database and a clear message is<br>
   * sent to the route locations for that channel name.
   * @param channelName
   */
  void resetChannel(String channelName);

}
