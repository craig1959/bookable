package nz.co.skytv.bp.protocol.messaging.send;

import static nz.co.skytv.bp.protocol.constants.ConverterConstants.CRLF;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class SendMessageServiceImpl implements MessageSenderLifecycleAware, SendMessageService {

  private static final Logger log = LoggerFactory.getLogger(SendMessageServiceImpl.class);

  protected Map<String, MessageSender> messageSenders;

  public SendMessageServiceImpl() {
    this.messageSenders = new HashMap<String, MessageSender>();
  }

  @Override
  public void registerMessageSender(String endpointId, MessageSender messageSender) {
    messageSenders.put(endpointId, messageSender);
  }

  @Override
  public void deregisterMessageSender(String endpointId) {
    messageSenders.remove(endpointId);
  }

  @Override
  public void sendMessage(String endpointId, String messageText) {


    MessageSender messageSender = messageSenders.get(endpointId);
    if (messageSender == null) {
      throw new EndpointLocationNotConfiguredException("Illegal value of endpoint for bridge'" + endpointId + "'");
    }
    messageSender.sendMessage(convertMessageText(messageText));

  }

  public byte[] convertMessageText(String messageText) {
    messageText = messageText.concat(CRLF);
    return messageText.getBytes();
  }
}
