package nz.co.skytv.bp.protocol.messaging;

import static nz.co.skytv.bp.protocol.constants.ConverterConstants.CISCO_NULL;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class MessageFilterImpl implements MessageFilter {

  private static final Logger log = LoggerFactory.getLogger(MessageFilterImpl.class);


  //example byteMessages   S|PREVIEW             |PPMB0001            |12:33:45:00
  //S|Sport 1             |#NONE#              |10:42:08:00
  @Override
  public String extractType(byte[] byteMessage) {
    String s = null;
    if (byteMessage != null) {
      s = filter(byteMessage, 0).trim();
    }
    return s;
  }

  @Override
  public String extractChannel(byte[] byteMessage) {
    String s = null;
    if (byteMessage != null) {
      s = filter(byteMessage, 1).trim();
    }
    return s;
  }

  @Override
  public String extractPromoKey(byte[] byteMessage) {
    String s = null;
    if (byteMessage != null) {
      s = filter(byteMessage, 2).trim();
    }
    return s;
  }

  @Override
  public String extractTime(byte[] byteMessage) {
    String s = null;
    if (byteMessage != null) {
      s = filter(byteMessage, 3).trim();
    }
    return s;
  }

  @Override
  public List<String> extractAll(byte[] byteMessage) {
    List<String> all = new ArrayList<String>();
    if (byteMessage != null) {
      try {
        for (int i = 0; i <= 3; i++) {
          String s = filter(byteMessage, i);
          all.add(s);
        }
      } catch (IndexOutOfBoundsException e) {
        log.error("IndexOutOfBoundsException for byteMessage = {}", new String(byteMessage));
        return null;
      }
    }
    return all;
  }

  @Override
  public String extractITVTSType(byte[] byteMessage) {
    String s = null;
    if (byteMessage != null) {
      s = filter(byteMessage, 2).trim();

      if (s.contains("NONE")) {
        s = CISCO_NULL;
      }
    }
    return s;
  }

  private String filter(byte[] byteMessage, int position) throws IndexOutOfBoundsException {
    String message = new String(byteMessage);
    String[] array = message.split("\\|");
    return array[position];
  }


}
