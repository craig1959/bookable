package nz.co.skytv.bp.protocol.messaging;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nz.co.skytv.bp.protocol.spring.BaseConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BaseConfiguration.class })
@WebAppConfiguration
public class MessageFactoryTests {

  private static final Logger log = LoggerFactory.getLogger(MessageFactoryTests.class);

  @Autowired
  MessageFactoryImpl factory;
  @Autowired
  MessageFilterImpl filter;

  String channelName = "SKY SPORT HD";
  String typeB = "S|Sport 1             |PPMB0001            |12:33:45:00";
  String typeC = "S|SKY SPORT HD        |PPMB0001            |12:33:45:00|0001000000000000|PPMB0001";
  String NULLtypeC = "S|null                |PPMB0001            |12:33:45:00|0001000000000000|PPMB0001";
  String OFFtypeC = "S|SKY SPORT HD        |#NONE#              |12:33:45:00|0001000000000000|#NONE#";
  String Harris = "S|0007|PPMB0001";
  String partHarris = "S|SKY SPORT HD        |PPMB0001            |";//to ignore time string for Harris test

  @Test
  public void createITVTSMessageFromHarris() {
    String response = factory.createITVTSMessage(Harris.getBytes(), channelName);
    assertTrue(response.contains("<trg:UriExt>PPMB0001</trg:UriExt>"));

  }

  @Test
  public void createHarrisNDSTypeCMessage() {
    String response = factory.createHarrisNDSMessage(Harris.getBytes(), channelName);
    assertTrue(response.contains(partHarris));
  }

  @Test
  public void shouldReturnNullHarrisNDSTypeCMessage() {
    String response = factory.createHarrisNDSMessage(null, channelName);
    assertEquals(null, response);
  }

  @Test
  public void createNDSTypeCMessage() {
    String response = factory.createNDSMessage(typeB.getBytes(), channelName);
    assertEquals(typeC, response);
  }

  @Test
  public void shouldReturnNullNDSTypeCMessage() {
    String response = factory.createNDSMessage(typeB.getBytes(), null);
    assertEquals(NULLtypeC, response);
  }

  @Test
  public void shouldReturnNull() {
    String response = factory.createNDSMessage(null, channelName);
    assertEquals(null, response);
  }

  @Test
  public void shouldReturnNullWithOutOfBoundsError() {
    String typeB = "S|Sport 1             |PPMB0001";

    String response = factory.createNDSMessage(typeB.getBytes(), channelName);
    assertEquals(null, response);
  }

  @Test
  public void generateNDSOff() {
    String response = factory.createNDSOffMessage("SKY SPORT HD");
    assertTrue(response.contains("#NONE#"));
  }

  @Test
  public void generateTVFormatTimeString() {
    String time = factory.tvTimeString();
    Pattern p = Pattern.compile("[0-9][0-9]:[0-9][0-9]:[0-9][0-9]:[0-9][0-9]");
    Matcher m = p.matcher(time);
    assertTrue(m.matches());
  }
}
