package nz.co.skytv.bp.protocol.socket.client;

import static nz.co.skytv.bp.protocol.constants.ConverterConstants.CRLF;
import static nz.co.skytv.bp.protocol.constants.ConverterConstants.HEARTBEAT;
import static nz.co.skytv.bp.protocol.constants.ConverterConstants.RESPONSE;
import java.util.Timer;
import java.util.TimerTask;
import nz.co.skytv.bp.protocol.messaging.MessageHandler;
import nz.co.skytv.bp.protocol.messaging.MessageResponder;
import nz.co.skytv.bp.protocol.model.EndpointType;
import nz.co.skytv.bp.protocol.socket.common.SocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


/**
 * ProbelSocketConnector handles all concerns unique to the Probel automation protocol.<br>
 */
public class ProbelSocketConnector implements SocketConnector, MessageHandler {

  private static final Logger log = LoggerFactory.getLogger(ProbelSocketConnector.class);

  ClientSocketConnectorDelegate socketConnectorDelegate;
  boolean isHandlingHeartbeats;
  private boolean restartOnFailedHeartbeat;
  String formatType;
  private String messageSource;
  private int port;
  int heartbeatTimeout;
  volatile long previousHeartbeat = Long.MIN_VALUE;//not the same on startup
  volatile long currentHeartbeat = 0L;
  Timer timer;
  private HeartBeatScheduler heartBeatScheduler;

  @Autowired
  MessageResponder messageResponder;
  @Value("${client.socket.autoconnect}")
  boolean autoconnect;
  @Value("${socket.reconnect.delayseconds}")
  int reinitialiseDelay;

  public ProbelSocketConnector() {
  }

  public ProbelSocketConnector(EndpointType endpoint) {
    this.timer = new Timer(true);
    this.heartBeatScheduler = new HeartBeatScheduler();
    this.messageSource = endpoint.getId();
    this.formatType = endpoint.getType();
    this.port = endpoint.getPort();
    this.socketConnectorDelegate = new ClientSocketConnectorDelegate(endpoint, this);
    this.isHandlingHeartbeats = endpoint.isCheckHeartbeat();
    this.restartOnFailedHeartbeat = endpoint.isRestartOnFailedHeartbeat();
    this.heartbeatTimeout = endpoint.getHeartbeatTimeout();
  }

  @Override
  public void start() throws InterruptedException {
    if (restartOnFailedHeartbeat) {
      timer.schedule(heartBeatScheduler, heartbeatTimeout * 1000, heartbeatTimeout * 1000);
      heartBeatScheduler.run();
    }
    socketConnectorDelegate.setAutoconnect(autoconnect);
    socketConnectorDelegate.setReconectDelay(reinitialiseDelay);
    socketConnectorDelegate.start();
  }

  @Override
  public void stop() {
    heartBeatScheduler.cancel();
    socketConnectorDelegate.stop();
  }


  @Override
  public void handleMessage(byte[] response) {
    String input = new String(response);
    String output = RESPONSE + input + CRLF;
    if (input.startsWith(HEARTBEAT)) {
      if (isHandlingHeartbeats) {
        handleHeartBeat(input);
      }
      socketConnectorDelegate.sendMessage(output.getBytes());//send response to automation
      return;// we don't want to pass heart beat messages  to ESB
    }
    log.info("Recieved from automation '{}' , '{}'", messageSource, input);
    socketConnectorDelegate.sendMessage(output.getBytes());//send response to automation
    messageResponder.handleMessage(response, messageSource);//send message to various routes
  }

  protected void handleHeartBeat(String message) {
    String heartBeat = message.replace(HEARTBEAT, "");
    try {
      this.currentHeartbeat = Long.decode("0x" + heartBeat);
    } catch (NumberFormatException e) {
      log.error("failed to decode heartbeat, not a valid hexadecimal string");
    }
  }

  private class HeartBeatScheduler extends TimerTask {

    @Override
    public void run() {
      log.trace("previousHeartbeat={}  currentHeartbeat={}", previousHeartbeat, currentHeartbeat);
      if (currentHeartbeat == previousHeartbeat) {

        log.trace("heartbeat hasn't updated, reinitialise socket on port '{}'", port);
        socketConnectorDelegate.reinitialiseChannel();
      }
      previousHeartbeat = currentHeartbeat;//make them the same
      //after timeout (2mins) if current heart beat is not greater than previous heart beat, reconnect.
    }
  }
}
