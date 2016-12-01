package nz.co.skytv.bp.protocol.messaging;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static nz.co.skytv.bp.protocol.constants.ConverterConstants.CISCO_NULL;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class MessageFilterTests {

  MessageFilterImpl filter;

  String message = "S|PREVIEW             |PPMB0001            |12:33:45:00";

  @Before
  public void init() {
    filter = new MessageFilterImpl();
  }

  @Test
  public void shouldExtractChannel() {
    String channel = filter.extractChannel(message.getBytes());
    assertEquals("PREVIEW", channel);
  }

  @Test
  public void shouldReturnChannelNull() {
    String channel = filter.extractChannel(null);
    assertEquals(null, channel);
  }

  @Test
  public void shouldExtractPromo() {
    String promoId = filter.extractPromoKey(message.getBytes());
    assertEquals("PPMB0001", promoId);
  }

  @Test
  public void shouldReturnPromoNull() {
    String promoId = filter.extractPromoKey(null);
    assertEquals(null, promoId);
  }

  @Test
  public void shouldExtractITVTSType() {
    String type = filter.extractITVTSType(message.getBytes());
    assertEquals("PPMB0001", type);
  }

  @Test
  public void shouldExtractITVTSNullType() {

    String message = "S|PREVIEW             |#NONE#              |12:33:45:00";
    String type = filter.extractITVTSType(message.getBytes());
    assertEquals(CISCO_NULL, type);
  }

  @Test
  public void shouldExtractJavaNull() {
    String type = filter.extractITVTSType(null);
    assertEquals(null, type);
  }

  @Test
  public void shouldExtractType() {
    String type = filter.extractType(message.getBytes());
    assertEquals("S", type);
  }

  @Test
  public void shouldTypeNull() {
    String type = filter.extractType(null);
    assertEquals(null, type);
  }

  @Test
  public void shouldExtractTime() {
    String time = filter.extractTime(message.getBytes());
    assertEquals("12:33:45:00", time);
  }

  @Test
  public void shouldTimeNull() {
    String time = filter.extractTime(null);
    assertEquals(null, time);
  }

  @Test
  public void shouldExtractAll() {
    List<String> all = filter.extractAll(message.getBytes());
    assertEquals(4, all.size());
  }

  @Test
  public void shouldAllNull() {
    List<String> all = filter.extractAll(null);
    assertTrue(all.isEmpty());
  }
}
