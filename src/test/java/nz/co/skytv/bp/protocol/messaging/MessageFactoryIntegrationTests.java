package nz.co.skytv.bp.protocol.messaging;

import javax.xml.bind.JAXBException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import nz.co.skytv.bp.protocol.spring.BaseConfiguration;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Integration test only, keep test @Ignore in commit
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BaseConfiguration.class })
public class MessageFactoryIntegrationTests {

  private static final Logger log = LoggerFactory.getLogger(MessageFactoryIntegrationTests.class);


  @Autowired
  MessageFactory factory;


  @Ignore
  @Test
  public void createMessage() throws JAXBException, XMLStreamException, FactoryConfigurationError {
    String result = factory.createITVTSMessage(null, null);

    log.info("result = {}", result);

  }

}
