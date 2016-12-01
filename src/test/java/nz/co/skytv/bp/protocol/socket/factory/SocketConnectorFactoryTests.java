package nz.co.skytv.bp.protocol.socket.factory;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;
import nz.co.skytv.bp.protocol.model.EndpointType;
import nz.co.skytv.bp.protocol.socket.client.HarrisSocketConnector;
import nz.co.skytv.bp.protocol.socket.client.ProbelSocketConnector;
import nz.co.skytv.bp.protocol.socket.common.SocketConnector;
import nz.co.skytv.bp.protocol.socket.server.ServerSocketConnector;
import nz.co.skytv.spring.util.FactoryHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;


@RunWith(MockitoJUnitRunner.class)
public class SocketConnectorFactoryTests {

  SocketConnectorFactoryImpl factory;

  FactoryHelper helper;

  @Mock
  ApplicationContext context;
  @Mock
  AutowireCapableBeanFactory beanFactory;

  @Before
  public void init() {

    factory = new SocketConnectorFactoryImpl();
    helper = new FactoryHelper();
    factory.helper = helper;
    helper.setApplicationContext(context);
    when(context.getAutowireCapableBeanFactory()).thenReturn(beanFactory);

  }

  @Test
  public void shouldCreateProbelSocketConnector() {
    EndpointType endpoint = endpointFixture(4444, "PROBEL1", "PROBEL");
    SocketConnector socketConnector = factory.createSocketConnector(endpoint);
    assertThat(socketConnector).isInstanceOf(ProbelSocketConnector.class);
  }

  @Test
  public void shouldCreateHarrisSocketConnector() {
    EndpointType endpoint = endpointFixture(4444, "HARRIS1", "HARRIS");
    SocketConnector socketConnector = factory.createSocketConnector(endpoint);
    assertThat(socketConnector).isInstanceOf(HarrisSocketConnector.class);
  }

  @Test
  public void shouldCreateNDSSocketConnector() {
    EndpointType endpoint = endpointFixture(4444, "NDS1", "NDS");
    SocketConnector socketConnector = factory.createSocketConnector(endpoint);
    assertThat(socketConnector).isInstanceOf(ServerSocketConnector.class);
  }

  @Test
  public void shouldCreateNoSocketConnector() {
    EndpointType endpoint = endpointFixture(4444, "NDS1", "RandomText");
    SocketConnector socketConnector = factory.createSocketConnector(endpoint);
    assertThat(socketConnector).isNull();
  }

  private static EndpointType endpointFixture(int port, String id, String type) {
    EndpointType e = new EndpointType();
    e.setHost("localhost");
    e.setPort(port);
    e.setId(id);
    e.setType(type);
    return e;
  }
}
