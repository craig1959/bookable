package nz.co.skytv.bp.protocol.cisco;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(MockitoJUnitRunner.class)
public class ITVTSClientIntegrationTest {

  private static final Logger log = LoggerFactory.getLogger(ITVTSClientIntegrationTest.class);

  ITVTSHttpClient client;
  //this url for Cisco head-end PT is only directly accessible from the sky developer sub-net 181
  String url = "http://172.26.123.47:5491"; //PT headend

  //String url = "http://localhost:8888";


  @Before
  public void init() {
    client = new ITVTSHttpClient(url);

  }

  @Ignore
  @Test
  public void postPayloadShouldReturnValidXML() throws ClientProtocolException, IOException {

    HttpResponse response = client.post(payload);
    log.debug(" HttpResponse Status {}", response.getStatusLine());

    InputStream in = response.getEntity().getContent();

    String myString = IOUtils.toString(in, "UTF-8");

    log.debug("response = {}", myString);
  }


  private static String payload = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
      + " <trg:Trigger  xmlns:trg=\"urn:nnds:trigger:01:01\""
      + "  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
      + " xsi:schemaLocation=\"urn:nnds:trigger:01:01 triggering.xsd\""
      + " xsi:type=\"trg:UnscheduledTrigger\">"
      + "  <trg:Target>dvb://169.3.1170</trg:Target>"
      + "  <trg:ActualTime>2015-06-22T01:23:07.204Z</trg:ActualTime>"
      + "  <trg:Type>urn:nnds:trigger:itvts:dataset:play</trg:Type>"
      + "  <trg:Key>PROMO03</trg:Key>"
      + " <trg:UriExt>PROMO04</trg:UriExt>"
      + " </trg:Trigger>";
}
