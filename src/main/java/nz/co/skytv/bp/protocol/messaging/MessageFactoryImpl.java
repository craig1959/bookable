package nz.co.skytv.bp.protocol.messaging;

import static nz.co.skytv.bp.protocol.constants.ConverterConstants.CISCO_KEY;
import static nz.co.skytv.bp.protocol.constants.ConverterConstants.CISCO_NULL;
import static nz.co.skytv.bp.protocol.constants.ConverterConstants.CISCO_PLAY;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import nz.co.skytv.bp.protocol.constants.ConverterConstants;
import nz.co.skytv.bp.protocol.model.UnscheduledTrigger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;


@Component
public class MessageFactoryImpl implements MessageFactory {

  private static final Logger log = LoggerFactory.getLogger(MessageFactoryImpl.class);

  private DatatypeFactory datatypeFactory;
  private XMLGregorianCalendar xgc = null;
  private DateTimeZone dtz = null;
  private DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm:ss");
  private Configuration cfg = null;
  private Template template = null;
  private File file;

  @Autowired
  MessageFilter messageFilter;

  @Value("${freemarker.template}")
  String templateDir;


  @PostConstruct
  public void init() {

    TimeZone tz = TimeZone.getTimeZone("GMT");// Cisco headend runs on GMT (UTC) time
    dtz = DateTimeZone.forTimeZone(tz);
    // Create and adjust the free marker configuration. Needed once for the application.
    cfg = new Configuration(new Version(2, 3, 20));
    cfg.setDefaultEncoding("UTF-8");
    try {
      file = new File(templateDir);
      datatypeFactory = DatatypeFactory.newInstance();
      cfg.setDirectoryForTemplateLoading(file);
      template = cfg.getTemplate("trigger.ftl");
    } catch (IOException e) {
      log.error("failed to load freemarker template");
      throw new RuntimeException(e);
    } catch (DatatypeConfigurationException e) {
      log.error("Failed to construct DatatypeFactory for XMLGregorianCalendar");
      throw new RuntimeException(e);
    }
  }


  @Override
  public synchronized String createITVTSMessage(byte[] byteMessage, String target)
  {
    UnscheduledTrigger trigger = new UnscheduledTrigger();
    StringWriter writer = new StringWriter();

    try {
      //the GMT time now in XMLGregorian is passed into the trigger
      xgc = datatypeFactory.newXMLGregorianCalendar(new DateTime(dtz).toGregorianCalendar());

      trigger.setActualTime(xgc);
      trigger.setTarget(target);//dvb://169.3.1002
      trigger.setKey(CISCO_KEY);
      trigger.getType().add(CISCO_PLAY);
      trigger.setUriExt(messageFilter.extractITVTSType(byteMessage));//either promoId or CISCO_NULL

      template.process(trigger, writer);

    } catch (TemplateException | IOException e) {
      log.error("Freemarker failed to process template", e);
    }
    return writer.toString();
  }

  @Override
  public synchronized String createHarrisNDSMessage(byte[] byteMessage, String channelName) {

    //Harris  S|0007|PPMB10001
    String promoId = messageFilter.extractPromoKey(byteMessage);
    if (promoId == null) {
      return null;
    }

    StringBuilder sb = new StringBuilder()
        .append('S').append('|')
        .append(padString(channelName)).append('|')
        .append(padString(promoId)).append('|')
        .append(tvTimeString()).append('|')
        .append("0001000000000000").append('|')
        .append(promoId.trim());

    return sb.toString();
  }

  @Override
  public synchronized String createNDSMessage(byte[] byteMessage, String channelName) {

    //type B  S|Sport 1             |PPMB0001            |12:33:45:00
    //type C  S|Sport 1             |PPMB0001            |10:42:08:00|0001000000000000|PPMB0001

    List<String> all = messageFilter.extractAll(byteMessage);
    if (all == null || all.size() <= 3) {
      return null;
    }
    StringBuilder sb = new StringBuilder()
        .append(all.get(0)).append('|')
        .append(padString(channelName)).append('|')
        .append(all.get(2)).append('|')
        .append(all.get(3)).append('|')
        .append("0001000000000000").append('|')
        .append(all.get(2).trim());

    return sb.toString();
  }


  /**
   * NDS GAS protocol requires exactly 20 characters between pipe delimiters(|).
   * @param channelName
   * @return channel name padded out to 20 chars.
   */
  private String padString(String channelName) {
    if (null == channelName) {
      channelName = "null";
    }
    int padding = ConverterConstants.NDS_PADDED_SIZE - channelName.length();
    for (int i = 1; i <= padding; i++) {
      channelName = channelName.concat(" ");
    }
    return channelName;
  }


  @Override
  public String createITVTSOffMessage(String location) {
    UnscheduledTrigger trigger = new UnscheduledTrigger();
    StringWriter writer = new StringWriter();

    try {
      //the GMT time now in XMLGregorian is passed into the trigger
      xgc = datatypeFactory.newXMLGregorianCalendar(new DateTime(dtz).toGregorianCalendar());

      trigger.setActualTime(xgc);
      trigger.setTarget(location);//dvb://169.3.1002
      trigger.setKey(CISCO_KEY);
      trigger.getType().add(CISCO_PLAY);
      trigger.setUriExt(CISCO_NULL);

      template.process(trigger, writer);

    } catch (TemplateException | IOException e) {
      log.error("Freemarker failed to process template", e);
    }
    return writer.toString();
  }


  @Override
  public String createNDSOffMessage(String channelName) {
    //type B  S|Sport 1             |#NONE#              |12:33:45:00
    //type C  S|Sport 1             |#NONE#              |10:42:08:00|0001000000000000|#NONE#

    if (channelName == null) {
      return null;
    }
    StringBuilder sb = new StringBuilder()
        .append("S").append('|')
        .append(padString(channelName)).append('|')
        .append(padString("#NONE#")).append('|')
        .append(tvTimeString()).append('|')
        .append("0001000000000000").append('|')
        .append("#NONE#");

    return sb.toString();
  }


  /**
   * Television uses a time-code format that is hours minutes seconds and frames.<br>
   * example 12:33:45:00.<br>
   * Here the frame count is always zero, but is essential for the target Cisco headend
   * system.
   */
  protected String tvTimeString() {
    DateTime now = new DateTime(dtz);
    String tvNowTime = now.toString(dtf);
    return tvNowTime.concat(":00");
  }


}
