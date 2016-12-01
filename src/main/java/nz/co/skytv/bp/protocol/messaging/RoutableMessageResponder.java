package nz.co.skytv.bp.protocol.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import nz.co.skytv.bp.protocol.cisco.ITVTSClient;
import nz.co.skytv.bp.protocol.dto.RouteDTO;
import nz.co.skytv.bp.protocol.messaging.send.EndpointLocationNotConfiguredException;
import nz.co.skytv.bp.protocol.messaging.send.SendMessageService;
import nz.co.skytv.bp.protocol.service.RouteService;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class RoutableMessageResponder implements MessageResponder {

  private static final Logger log = LoggerFactory.getLogger(RoutableMessageResponder.class);

  @Autowired
  RouteService routeService;
  @Autowired
  ITVTSClient itvtsClient;
  @Autowired
  SendMessageService sendMessageService;
  @Autowired
  MessageFactory messageFactory;
  @Autowired
  MessageFilter messageFilter;

  @Override
  public void handleMessage(byte[] byteMessage, String messageSourceEndpointId) {

    if (isBookablePromo(byteMessage)) {
      String messageChannel = messageFilter.extractChannel(byteMessage);
      //messageSourceEndpointId eg. PROBEL2, HARRIS1. message channel eg. SOHO, 0004.
      List<RouteDTO> routes = routeService.processRoutes(messageSourceEndpointId, messageChannel);

      for (RouteDTO route : routes) {
        log.debug(route.toString());
        if (route.getRoute_type().equals("TCM")) {
          String xmlMessage = messageFactory.createITVTSMessage(byteMessage, route.getRoute_location());

          try {
            log.debug("ITVTS XML message = '{}'", xmlMessage);
            HttpResponse response = itvtsClient.post(xmlMessage);
            log.info("Routed ITVTS message for '{}, status = '{}'", route.getRoute_location(), response.getStatusLine()
                .getStatusCode());

            InputStream reply = response.getEntity().getContent();
            log.debug("reply from headend = '{}'", IOUtils.toString(reply, "UTF-8"));

          } catch (IOException e) {
            log.error("failed to POST XML message = {}", xmlMessage, e);
          }
        }
        else if (route.getRoute_type().equals("NDS")) {
          String translatedMessage = null;
          if (messageSourceEndpointId.contains("HARRIS")) {
            translatedMessage = messageFactory.createHarrisNDSMessage(byteMessage, route.getChannel_name());
          } else {
            translatedMessage = messageFactory.createNDSMessage(byteMessage, route.getChannel_name());
          }
          sendMessageService.sendMessage(route.getRoute_location(), translatedMessage);
        } else {
          log.error("Route from DB is empty. channel = '{}', type = '{}'", messageChannel, messageSourceEndpointId);
        }
      }
    } else {
      log.info("rejected promo, not bookable. message = '{}', type = '{}'", new String(byteMessage), messageSourceEndpointId);
    }

  }


  /**
   * What do we have here?<br>
   * This piece of embedded business logic<br>
   * is to cope with the fact that SkyTV Albany automation<br>
   * sends triggers for every promo whether it is bookable or not<br>
   * We need to filter these out so the headend is not overwhelmed with invalid triggers.
   */
  protected boolean isBookablePromo(byte[] byteMessage) {

    String promoKey = messageFilter.extractPromoKey(byteMessage);
    if (promoKey.contains("#NONE#")) {
      return true;
    }

    return promoKey.matches("^[P]{1}[a-zA-Z0-9]{1}[a-zA-Z0-9]{1}[B]{1}[0-9]+");
  }


  @Override
  public void resetChannel(String channelName) throws EndpointLocationNotConfiguredException {

    List<String> locations = routeService.resetLocation(channelName);

    for (String location : locations) {

      if (location.contains("dvb")) {
        String xmlMessage = messageFactory.createITVTSOffMessage(location);
        try {
          log.debug("Reset ITVTS message = '{}'", xmlMessage);
          HttpResponse response = itvtsClient.post(xmlMessage);
          log.info("Reset ITVTS message, response status = '{}'", response.getStatusLine().getStatusCode());

          InputStream reply = response.getEntity().getContent();
          log.debug("Reset reply from headend = '{}'", IOUtils.toString(reply, "UTF-8"));

        } catch (IOException e) {
          log.error("failed to POST Reset XML message = {}", xmlMessage, e);
        }
      } else if (location.contains("NDS")) {
        String offMessage = messageFactory.createNDSOffMessage(channelName);
        log.debug("Reset NDS GAS message = '{}'", offMessage);
        sendMessageService.sendMessage(location, offMessage);
      } else {
        log.error("Reset failed route locations are empty for channelName = '{}'", channelName);
      }
    }
  }


  //  CHANNEL_NAME  SOHO SD , SOHO HD   the output string name that gets sent to the target
  //  ROUTE_LOCATION  NDS1, NDS2 , dvb://169.7.1170   the target system
  //  ROUTE_TYPE  TCM or NDS  the target system type

}
