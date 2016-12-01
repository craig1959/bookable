package nz.co.skytv.bp.protocol.messaging;

import java.util.List;


/**
 * MessageFilter is a collection of methods<br>
 * for extracting the various parameters from a NDS GAS protocol version B<br>
 * message.
 */
public interface MessageFilter {

  /**
   * @param message eg S|SKY MOVIES1 HD|PPMB0001|12:33:45:00
   * @return the message type, {S,SKY MOVIES1 HD,PPMB0001,12:33:45:00}
   */
  public List<String> extractAll(byte[] message);

  /**
   * @param message eg S|SKY MOVIES1 HD|PPMB0001|12:33:45:00
   * @return the message type, S
   */
  public String extractType(byte[] message);

  /**
   * @param message eg S|SKY MOVIES1 HD|PPMB0001|12:33:45:00
   * @return the channel, SKY MOVIES1 HD
   */
  public String extractChannel(byte[] message);

  /**
   * @param message eg S|SKY MOVIES1 HD|PPMB0001|12:33:45:00
   * @return the promo key, PPMB0001
   */
  public String extractPromoKey(byte[] message);

  /**
   * @param message eg S|SKY MOVIES1 HD|PPMB0001|12:33:45:00
   * @return the time , 12:33:45:00
   */
  public String extractTime(byte[] message);

  /**
   * Cisco ITVTS either plays the promo key or plays the null.<br>
   * 
   * @param message eg S|SKY MOVIES1 HD|PPMB0001|12:33:45:00<br>
   *        or S|SKY MOVIES1 HD|#NONE#|12:33:45:00<br>
   * @return the channel, PPMB0001 or %23NULLEPRM3%23
   */
  public String extractITVTSType(byte[] message);


}
