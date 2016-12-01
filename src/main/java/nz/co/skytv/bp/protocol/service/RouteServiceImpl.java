package nz.co.skytv.bp.protocol.service;

import java.util.ArrayList;
import java.util.List;
import nz.co.skytv.bp.protocol.dao.RouteDAO;
import nz.co.skytv.bp.protocol.dto.RouteDTO;
import nz.co.skytv.bp.protocol.model.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class RouteServiceImpl implements RouteService {


  @Autowired
  RouteDAO routeDAO;

  @Override
  @Transactional
  public List<RouteDTO> processRoutes(String source, String channel) {

    List<RouteDTO> routes = new ArrayList<RouteDTO>();

    List<Route> entities = routeDAO.getRoutes(source, channel);

    for (Route entity : entities) {
      routes.add(new RouteDTO(entity));
    }
    return routes;

  }

  @Override
  public List<String> resetLocation(String channelName) {
    return routeDAO.getResetLocation(channelName);
  }

}
