/**
* Copyright (c) 2014 Sky Network Television Ltd. All rights reserved.
* @author Prj.M@x <pablo.rendon@skytv.co.nz>
*/
package nz.co.skytv.bp.protocol.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Audit implements Serializable {
    
    @Column(name="createdByUser")
    protected String createdByUser;
    
    @Column(name="createdDate")
    protected Long createdDate;
    
    @Column(name="modifiedByUser")
    protected String modifiedByUser;
    
    @Column(name="modifiedDate")
    protected Long modifiedDate;

    
    public String getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(String createdByUser) {
        this.createdByUser = createdByUser;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifiedByUser() {
        return modifiedByUser;
    }

    public void setModifiedByUser(String modifiedByUser) {
        this.modifiedByUser = modifiedByUser;
    }

    public Long getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Long modifiedDate) {
        this.modifiedDate = modifiedDate;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Audit other = (Audit) obj;
        if (!Objects.equals(this.createdByUser, other.createdByUser)) {
            return false;
        }
        if (!Objects.equals(this.createdDate, other.createdDate)) {
            return false;
        }
        if (!Objects.equals(this.modifiedByUser, other.modifiedByUser)) {
            return false;
        }
        if (!Objects.equals(this.modifiedDate, other.modifiedDate)) {
            return false;
        }
        return true;
    }
    
    
    
    
    
}