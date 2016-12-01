package nz.co.skytv.bp.protocol.integration;

import static org.junit.Assert.assertNotNull;
import nz.co.skytv.bp.protocol.spring.BaseConfiguration;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


/**
 * Sanity check that the hibernate configuration is working
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BaseConfiguration.class })
public class HibernateConfigurationTest {


  @Autowired
  private SessionFactory sessionFactory;

  @Test
  public void testHibernateConfiguration() {
    // Spring IOC container instantiated and prepared sessionFactory
    assertNotNull(sessionFactory);
  }

}
