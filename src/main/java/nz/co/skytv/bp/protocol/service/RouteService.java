package nz.co.skytv.bp.protocol.service;

import java.util.List;
import nz.co.skytv.bp.protocol.dto.RouteDTO;


public interface RouteService {

  public List<RouteDTO> processRoutes(String source, String channel);

  public List<String> resetLocation(String channelName);
}
