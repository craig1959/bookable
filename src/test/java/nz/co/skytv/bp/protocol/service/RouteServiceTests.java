package nz.co.skytv.bp.protocol.service;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import junit.framework.Assert;
import nz.co.skytv.bp.protocol.dao.RouteDAO;
import nz.co.skytv.bp.protocol.dto.RouteDTO;
import nz.co.skytv.bp.protocol.model.Location;
import nz.co.skytv.bp.protocol.model.Route;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class RouteServiceTests {

  RouteServiceImpl routeService;

  @Mock
  RouteDAO routeDAO;

  @Before
  public void init() {
    routeService = new RouteServiceImpl();
    routeService.routeDAO = routeDAO;
    when(routeDAO.getRoutes(anyString(), anyString())).thenReturn(routeFixture());
  }

  @Test
  public void processRoutes() {
    List<RouteDTO> routes = routeService.processRoutes("source", "channel");
    Assert.assertEquals(2, routes.size());
  }

  private List<Route> routeFixture() {
    List<Route> entities = new ArrayList<Route>();
    entities.add(entityFixture("NDS", "NDS1", "SOHO SD"));
    entities.add(entityFixture("TCM", "dvb://169.3.1179", "SOHO HD"));
    return entities;
  }

  private Route entityFixture(String type, String location, String channel) {
    Route route = new Route();
    route.setChannel_name(channel);
    route.setRoute_location(location);
    route.setRoute_type(type);
    route.setLocation(new Location());
    return route;
  }


}
