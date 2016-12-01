/**
 * Copyright (c) 2014 Sky Network Television Ltd. All rights reserved.
 * @author Prj.M@x <pablo.rendon@skytv.co.nz>
 */
package nz.co.skytv.bp.protocol.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


@Entity
@Table(name = "BOOKABLE_PROMO_ROUTE")
public class Route extends Audit implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "ROUTE_ID", nullable = false)
  @SequenceGenerator(name = "route_id_seq", sequenceName = "route_id_seq", allocationSize = 1, initialValue = 100)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "route_id_seq")
  private Long id;

  @Column(name = "LOCATION_ID")
  private Long location_id;

  @Column(name = "ROUTE_TYPE")
  private String route_type;

  @Column(name = "ROUTE_LOCATION")
  private String route_location;

  @Column(name = "CHANNEL_NAME")
  private String channel_name;

  @Column(name = "TIME_SHIFT")
  private Long time_shift;

  @Column(name = "IS_ACTIVE")
  private int is_active;

  @ManyToOne
  @JoinColumn(name = "LOCATION_ID", referencedColumnName = "LOCATION_ID", insertable = false, updatable = false)
  private Location location;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.location = location;
  }


  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Route other = (Route) obj;
    if (!Objects.equals(this.id, other.id)) {
      return false;
    }
    if (!Objects.equals(this.location_id, other.location_id)) {
      return false;
    }
    if (!Objects.equals(this.route_type, other.route_type)) {
      return false;
    }
    if (!Objects.equals(this.route_location, other.route_location)) {
      return false;
    }
    if (!Objects.equals(this.channel_name, other.channel_name)) {
      return false;
    }
    if (!Objects.equals(this.time_shift, other.time_shift)) {
      return false;
    }
    if (!Objects.equals(this.is_active, other.is_active)) {
      return false;
    }
    return true;
  }


}
