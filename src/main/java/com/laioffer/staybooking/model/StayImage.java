package com.laioffer.staybooking.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;

/*the StayImage class represents images associated with a stay. Each StayImage object has a URL,
and it is associated with a specific Stay entity. This class is used to establish a relationship
between stays and their images in a database table.*/
@Entity
@Table(name = "stay_image")
public class StayImage {
    @Id
    private String url;

    @ManyToOne
    @JoinColumn(name = "stay_id")
    @JsonIgnore
    private Stay stay;

    public StayImage() {
    }

    public StayImage(String url, Stay stay) {
        this.url = url;
        this.stay = stay;
    }

    public String getUrl() {
        return url;
    }

    public StayImage setUrl(String url) {
        this.url = url;
        return this;
    }

    public Stay getStay() {
        return stay;
    }

    public StayImage setStay(Stay stay) {
        this.stay = stay;
        return this;
    }
}

