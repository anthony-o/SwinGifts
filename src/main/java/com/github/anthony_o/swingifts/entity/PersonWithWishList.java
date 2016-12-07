package com.github.anthony_o.swingifts.entity;

public class PersonWithWishList {
    private Long id;
    private Long wishListId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWishListId() {
        return wishListId;
    }

    public void setWishListId(Long wishListId) {
        this.wishListId = wishListId;
    }
}
