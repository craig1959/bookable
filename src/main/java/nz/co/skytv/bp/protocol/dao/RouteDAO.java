package nz.co.skytv.bp.protocol.dao;

import java.util.List;
import nz.co.skytv.bp.protocol.model.Route;


/**
 * retrieves a list of active routes from Database tables location and route
 */
public interface RouteDAO {

  /**
   * Broadcast automation provides a trigger containing message source and channel.<br>
   * eg PROBEL2 is an automation source and SOHO is an automation channel.<br>
   * 
   * @param source
   * @param channel
   * @return list of active routes.<br>
   *         eg valid routes could be to SOHO SD, SOHO HD, and NDS GAS
   */
  public List<Route> getRoutes(String source, String channel);

  /**
   * To clear a stuck green button.<br>
   * This is called to get unique Route Locations,<br>
   * for a destination channel.
   * 
   * @param channelName
   * @return list of unique route locations that use this channel name
   */
  public List<String> getResetLocation(String channelName);

}
