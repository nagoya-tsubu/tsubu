package com.androidtsubu.ramentimer.server.model;

import java.io.Serializable;
import java.util.Date;

import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONHint;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.CreationDate;
import org.slim3.datastore.Model;
import org.slim3.datastore.ModificationDate;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@Model(schemaVersion = 1)
public class Ramen implements Serializable {

    private static final long serialVersionUID = 1L;

    @Attribute(primaryKey = true)
    @JSONHint(ignore = true)
    private Key key;

    @Attribute(version = true)
    @JSONHint(ignore = true)
    private Long version;
    
    @Attribute(listener = CreationDate.class)
    private Date createdAt;

    @Attribute(listener = ModificationDate.class)
    private Date updatedAt;
    
    private String name;
    
    private String jan;
    
    private int boilTime;
    
    @Attribute(lob = true)
    private byte[] imageData;
    
    @Attribute(persistent = false)
    private String imageUrl;
    
    /**
     * Returns the key.
     *
     * @return the key
     */
    @JSONHint(ignore = true)
    public Key getKey() {
        return key;
    }

    /**
     * Sets the key.
     *
     * @param key
     *            the key
     */
    public void setKey(Key key) {
        this.key = key;
    }

    /**
     * Returns the version.
     *
     * @return the version
     */
    @JSONHint(ignore = true)
    public Long getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version
     *            the version
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJan() {
        return jan;
    }

    public void setJan(String jan) {
        this.jan = jan;
    }

    public int getBoilTime() {
        return boilTime;
    }

    public void setBoilTime(int boilTime) {
        this.boilTime = boilTime;
    }

    @JSONHint(ignore = true)
    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] image) {
        this.imageData = image;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        if (this.key == null) {
            return null;
        } else {
            return "/api/ramens/image?key=" + KeyFactory.keyToString(this.key);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Ramen other = (Ramen) obj;
        if (key == null) {
            if (other.key != null) {
                return false;
            }
        } else if (!key.equals(other.key)) {
            return false;
        }
        return true;
    }
    
    public String toJson() {
        return JSON.encode(this);
    }
}
