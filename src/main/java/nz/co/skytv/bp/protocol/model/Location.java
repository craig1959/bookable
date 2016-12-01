/**
* Copyright (c) 2014 Sky Network Television Ltd. All rights reserved.
* @author Prj.M@x <pablo.rendon@skytv.co.nz>
*/
package nz.co.skytv.bp.protocol.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="BOOKABLE_PROMO_LOCATION")
public class Location extends Audit implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name="LOCATION_ID", nullable = false)
    @SequenceGenerator(name="location_id_seq", sequenceName="location_id_seq", allocationSize=1, initialValue = 100)  
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="location_id_seq")      
    private Long id;

    @Column(name="MESSAGE_SOURCE")
    private String source;
 
    @Column(name="MESSAGE_CHANNEL")
    private String channel;
    
    @OneToMany(mappedBy="location", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private Set<Route> routes;
    
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Set<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(Set<Route> routes) {
        this.routes = routes;
    }

   
        

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Location other = (Location) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.source, other.source)) {
            return false;
        }
        if (!Objects.equals(this.channel, other.channel)) {
            return false;
        }
        return true;
    }
    
}
