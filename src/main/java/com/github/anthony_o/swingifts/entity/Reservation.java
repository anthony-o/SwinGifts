package com.github.anthony_o.swingifts.entity;

public class Reservation {
    private Long id;
    private Long personId;
    private Long wishItemId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Long getWishItemId() {
        return wishItemId;
    }

    public void setWishItemId(Long wishItemId) {
        this.wishItemId = wishItemId;
    }

}
