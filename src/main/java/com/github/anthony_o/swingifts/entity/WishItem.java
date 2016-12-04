package com.github.anthony_o.swingifts.entity;

import java.util.List;

public class WishItem {
    private Long id;
    private String name;
    private String url;

    private Long wishListId;
    private Long personId;
    private Long createdByPersonId;

    private List<Reservation> reservations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getWishListId() {
        return wishListId;
    }

    public void setWishListId(Long wishListId) {
        this.wishListId = wishListId;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public Long getCreatedByPersonId() {
        return createdByPersonId;
    }

    public void setCreatedByPersonId(Long createdByPersonId) {
        this.createdByPersonId = createdByPersonId;
    }
}
