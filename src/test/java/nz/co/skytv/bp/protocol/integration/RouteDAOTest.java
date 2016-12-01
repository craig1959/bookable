package nz.co.skytv.bp.protocol.integration;

import java.util.List;
import nz.co.skytv.bp.protocol.dao.RouteDAO;
import nz.co.skytv.bp.protocol.model.Route;
import nz.co.skytv.bp.protocol.spring.BaseConfiguration;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BaseConfiguration.class })
public class RouteDAOTest {

  private static final Logger log = LoggerFactory.getLogger(RouteDAOTest.class);

  @Autowired
  RouteDAO routeDAO;

  @Ignore
  @Test
  public void getRoutesIntegrationTest() {

    List<Route> routes = routeDAO.getRoutes("PROBEL2", "SOHO");
    log.info("channel Name = {}", routes.get(0).getChannel_name());
    log.info("route location = {}", routes.get(0).getRoute_location());
    log.info("route type = {}", routes.get(0).getRoute_type());

  }

  @Ignore
  @Test
  public void getResetLocationIntegrationTest() {

    List<String> routes = routeDAO.getResetLocation("SOHO HD");

    for (String route : routes) {
      log.info("route location = {}", route);
    }

  }


}
