/**
 * Copyright (c) 2014 Sky Network Television Ltd. All rights reserved.
 * @author Prj.M@x <pablo.rendon@skytv.co.nz>
 */
package nz.co.skytv.bp.protocol.dto;

import java.io.Serializable;
import nz.co.skytv.bp.protocol.model.Route;


public class RouteDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long location_id;
  private String route_type;
  private String route_location;
  private String channel_name;
  private Long time_shift;
  private int is_active;
  private String location;

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder()
        .append("Route is ")
        .append(" Type = " + route_type)
        .append(" location = " + route_location)
        .append(" channel = " + channel_name)
        .append(" active = " + is_active);
    return sb.toString();
  }

  public RouteDTO(Route entity) {
    this.location_id = entity.getLocation_id();
    this.route_type = entity.getRoute_type();
    this.route_location = entity.getRoute_location();
    this.channel_name = entity.getChannel_name();
    this.time_shift = entity.getTime_shift();
    this.is_active = entity.getIs_active();
    this.location = entity.getLocation().getSource() + " - " + entity.getLocation().getChannel();
  }

  public Long getLocation_id() {
    return location_id;
  }

  public void setLocation_id(Long location_id) {
    this.location_id = location_id;
  }

  public String getRoute_type() {
    return route_type;
  }

  public void setRoute_type(String route_type) {
    this.route_type = route_type;
  }

  public String getRoute_location() {
    return route_location;
  }

  public void setRoute_location(String route_location) {
    this.route_location = route_location;
  }

  public String getChannel_name() {
    return channel_name;
  }

  public void setChannel_name(String channel_name) {
    this.channel_name = channel_name;
  }

  public Long getTime_shift() {
    return time_shift;
  }

  public void setTime_shift(Long time_shift) {
    this.time_shift = time_shift;
  }

  public int isIs_active() {
    return is_active;
  }

  public int getIs_active() {
    return is_active;
  }

  public void setIs_active(int is_active) {
    this.is_active = is_active;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }


}