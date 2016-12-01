package nz.co.skytv.bp.protocol.socket.client;

import static nz.co.skytv.bp.protocol.constants.ConverterConstants.HEARTBEAT;
import nz.co.skytv.bp.protocol.messaging.MessageHandler;
import nz.co.skytv.bp.protocol.messaging.MessageResponder;
import nz.co.skytv.bp.protocol.model.EndpointType;
import nz.co.skytv.bp.protocol.socket.common.SocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


/**
 * HarrisSocketConnector handles all concerns unique to the Harris automation protocol.<br>
 * The HarrisSocketConnector does not support heartbeats.<br>
 * Sky TV Albany Harris automation systems do not send heartbeats.<br>
 * 
 */
public class HarrisSocketConnector implements SocketConnector, MessageHandler {

  private static final Logger log = LoggerFactory.getLogger(HarrisSocketConnector.class);

  ClientSocketConnectorDelegate socketConnectorDelegate;
  boolean isHandlingHeartbeats;
  private String formatType; //HARRIS or PROBEL
  private String messageSource;

  @Autowired
  MessageResponder messageResponder;
  @Value("${client.socket.autoconnect}")
  boolean autoconnect;
  @Value("${socket.reconnect.delayseconds}")
  int reinitialiseDelay;

  public HarrisSocketConnector() {
  }

  public HarrisSocketConnector(EndpointType endpoint) {
    this.messageSource = endpoint.getId();
    this.formatType = endpoint.getType();
    this.socketConnectorDelegate = new ClientSocketConnectorDelegate(endpoint, this);
    this.isHandlingHeartbeats = endpoint.isCheckHeartbeat();
  }

  @Override
  public void start() throws InterruptedException {
    socketConnectorDelegate.setAutoconnect(autoconnect);
    socketConnectorDelegate.setReconectDelay(reinitialiseDelay);
    socketConnectorDelegate.start();
  }

  @Override
  public void stop() {
    socketConnectorDelegate.stop();
  }

  @Override
  public void handleMessage(byte[] response) {

    String input = new String(response);
    //Don't pass Heart beat message to ESB
    if (input.startsWith(HEARTBEAT)) {
      return;
    }
    log.info("Received from automation '{}'  '{}'", messageSource, input);
    messageResponder.handleMessage(response, messageSource);
  }

}
