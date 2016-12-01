package nz.co.skytv.bp.protocol.rest.controller;

import javax.ws.rs.core.MediaType;
import nz.co.skytv.bp.protocol.messaging.MessageResponder;
import nz.co.skytv.bp.protocol.messaging.send.EndpointLocationNotConfiguredException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * The green button reset endpoint will look something like this<br>
 * http://localhost:8080/bookablepromo-protocolconverter-1.0.1-SNAPSHOT/greenbuttonreset<br>
 * if deployed to tomcat with no context configured.
 */
@Controller
@RequestMapping("/greenbuttonreset")
public class GreenButtonResetController {

  private static final Logger log = LoggerFactory.getLogger(GreenButtonResetController.class);

  @Autowired
  MessageResponder responder;


  @RequestMapping(method = { RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
  public @ResponseBody
  ResponseEntity<String> reset(@RequestBody String json) {

    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.set("Green button reset", "actioned");

    ObjectMapper mapper = new ObjectMapper();
    Channel channel = null;
    try {
      channel = mapper.readValue(json, Channel.class);
      responder.resetChannel(channel.Channel);
    } catch (EndpointLocationNotConfiguredException e) {
      log.error("Reset Failed with invalid endpoint ", e.getMessage());
      return new ResponseEntity<String>("Green button reset invalid endpoint", responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
    } catch (Exception e) {
      log.error("Failed to map Channel name {}", json);
      return new ResponseEntity<String>("Green button reset failed to map", responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    log.debug("Got a reset request for channel " + channel.Channel);

    return new ResponseEntity<String>("Green button reset OK", responseHeaders, HttpStatus.OK);
  }

  /**
   * Channel is only used here to give ObjectMapper something to map to.
   */
  public static class Channel {

    public String Channel;
  }

}
