package nz.co.skytv.bp.protocol.socket.client;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import io.netty.channel.Channel;
import nz.co.skytv.bp.protocol.messaging.MessageResponder;
import nz.co.skytv.bp.protocol.model.EndpointType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ProbelSocketConnectorTests {

  ProbelSocketConnector probelSocketConnector;

  String startMessage = "S|PREVIEW             |PPMB0001            |12:33:45:00";
  byte[] startByteMessage;
  String responseMessage = "R|S|PREVIEW             |PPMB0001            |12:33:45:00\r\n";
  byte[] responseByteMessage;
  String heartbeatMessage = "H|3bdf28";
  byte[] heartbeatByteMessage;
  String heartbeatResponseMessage = "R|H|3bdf28\r\n";
  byte[] heartbeatResponseByteMessage;
  String heartbeat = "3bdf28";

  @Mock
  ClientSocketConnectorDelegate delegate;
  @Mock
  MessageResponder responder;

  @Mock
  Channel channel;

  @Before
  public void init() {
    probelSocketConnector = new ProbelSocketConnector(endpointFixture(5555, "PROBEL"));
    probelSocketConnector.socketConnectorDelegate = delegate;
    probelSocketConnector.messageResponder = responder;
    probelSocketConnector.socketConnectorDelegate.channel = channel;
    startByteMessage = startMessage.getBytes();
    responseByteMessage = responseMessage.getBytes();
    heartbeatByteMessage = heartbeatMessage.getBytes();
    heartbeatResponseByteMessage = heartbeatResponseMessage.getBytes();
  }

  @Test
  public void shouldSetHeartbeat() {
    probelSocketConnector.handleHeartBeat(heartbeatMessage);
    assertThat(probelSocketConnector.currentHeartbeat).isEqualTo(Long.decode("0x" + heartbeat));
  }

  @Test
  public void shouldOnlySendMessageOnce() {
    probelSocketConnector.isHandlingHeartbeats = true;
    probelSocketConnector.handleMessage(heartbeatByteMessage);
    verify(delegate, times(1)).sendMessage(Mockito.any(byte[].class));
  }

  @Test
  public void shouldSendMessageOnceAndHandleMessageOnce() {
    probelSocketConnector.isHandlingHeartbeats = false;
    probelSocketConnector.handleMessage(startByteMessage);
    verify(delegate, times(1)).sendMessage(Mockito.any(byte[].class));
    verify(responder, times(1)).handleMessage(Mockito.any(byte[].class), Mockito.anyString());
  }

  //integration test
  @Ignore
  @Test
  public void runTimer() throws InterruptedException {

    probelSocketConnector = new ProbelSocketConnector(endpointFixture(5555, "PROBEL"));
    probelSocketConnector.heartbeatTimeout = 2;
    probelSocketConnector.start();
    Thread.sleep(20000);

  }

  private static EndpointType endpointFixture(int port, String id) {
    EndpointType e = new EndpointType();
    e.setHost("localhost");
    e.setPort(port);
    e.setId(id);
    e.setType("PROBEL");
    return e;
  }
}
