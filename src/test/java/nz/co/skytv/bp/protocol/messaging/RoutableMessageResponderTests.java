package nz.co.skytv.bp.protocol.messaging;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class RoutableMessageResponderTests {

  RoutableMessageResponder responder;
  MessageFilterImpl filter;

  String probelValid = "S|PREVIEW             |PP1B000198          |12:33:45:00";
  String probelInvalid = "S|PREVIEW             |DPMV00101           |12:33:45:00";
  String probelNone = "S|SKY SPORT HD        |#NONE#              |12:33:45:00";
  String harris = "S|0007|PPMB0001";
  String harrisInvalid = "S|0007|BPMV0001";
  String harrisNone = "S|0007|#NONE#";


  @Before
  public void init() {
    filter = new MessageFilterImpl();
    responder = new RoutableMessageResponder();
    responder.messageFilter = filter;

  }

  @Test
  public void returnTrueValidPromo() {
    assertTrue(responder.isBookablePromo(probelValid.getBytes()));
  }

  @Test
  public void returnFalseInvalidPromo() {
    assertFalse(responder.isBookablePromo(probelInvalid.getBytes()));
  }

  @Test
  public void returnTrueValidNonePromo() {
    assertTrue(responder.isBookablePromo(probelNone.getBytes()));
  }

  @Test
  public void returnTrueHarrisValidPromo() {
    assertTrue(responder.isBookablePromo(harris.getBytes()));
  }

  @Test
  public void returnFalseHarrisInvalidPromo() {
    assertFalse(responder.isBookablePromo(harrisInvalid.getBytes()));
  }

  @Test
  public void returnTrueHarrisNonePromo() {
    assertTrue(responder.isBookablePromo(harrisNone.getBytes()));
  }

}
