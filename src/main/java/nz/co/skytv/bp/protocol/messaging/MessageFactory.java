package nz.co.skytv.bp.protocol.messaging;


/**
 * message factory creates the different flavours of message.<br>
 * This depends on whether it is the Cisco Fusion XML type or the Cisco NDS legacy type.<br>
 * at launch protocol converter must support both types of trigger.
 */
public interface MessageFactory {

  /**
   * ITVTS uses a very specific xml message, requiring exact name space and prefixes.<br>
   * Initially, JAX-B was tried but could not get the exact format.<br>
   * So MessageFactory is now using Freemarker templating.
   * 
   * An example ITVTS trigger message<br>
   * {@code <?xml version="1.0" encoding="UTF-8"?><br>
   * <trg:Trigger  xmlns:trg="urn:nnds:trigger:01:01"<br>
   * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"<br>
   * xsi:schemaLocation="urn:nnds:trigger:01:01 triggering.xsd"<br>
   * xsi:type="trg:UnscheduledTrigger"><br>
   * <trg:Target> dvb://169.3.1002</trg:Target><br>
   * <trg:ActualTime> 2014-02-03T02:00:00+00:00 </trg:ActualTime><br>
   * <trg:Type>urn:nnds:trigger:itvts:dataset:play</trg:Type><br>
   * <trg:Key>PROMO03</trg:Key><br>
   * <trg:UriExt>PROMO04</trg:UriExt><br>
   * </trg:Trigger><br>
   * }
   * 
   * @param byteMessage
   * @param messageSource
   * @param messageType
   * @return the xml message
   */
  public String createITVTSMessage(byte[] byteMessage, String target);


  /**
   * This message type is for the Cisco legacy NDS GAS headend.<br>
   * byteMessage is NDS protocol version B.<br>
   * Translated message is NDS protocol version C.
   * 
   * @param byteMessage
   * @param channelName
   * @return protocol translated message
   */
  public String createNDSMessage(byte[] byteMessage, String channelName);

  /**
   * This message type is for the Cisco legacy NDS GAS headend.<br>
   * byteMessage is Harris protocol.<br>
   * Translated message is NDS protocol version C.
   * 
   * @param byteMessage
   * @param channel_name
   * @return
   */
  public String createHarrisNDSMessage(byte[] byteMessage, String channel_name);


  /**
   * When green button is stuck on screen an off message is sent to the channel.<br>
   * This is only used by the web UI admin tool.
   * 
   * @param location
   * @return the ITVTS XML message
   */
  public String createITVTSOffMessage(String location);

  /**
   * When green button is stuck on screen an off message is sent to the channel.<br>
   * This is only used by the web UI admin tool.
   * 
   * @param location
   * @return the NDS GAS format message
   */
  public String createNDSOffMessage(String location);
}
