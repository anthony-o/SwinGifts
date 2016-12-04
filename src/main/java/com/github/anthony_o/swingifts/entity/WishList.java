package com.github.anthony_o.swingifts.entity;

import java.util.List;

public class WishList {
    private Long id;
    private Long personId;
    private Long eventId;

    private Person person;
    private List<WishItem> wishItems;
    private Boolean isPersonEventAdmin;
    private Boolean isPersonParticipatesInCircleGift;
    private Long circleGiftTargetPersonId;
    private Boolean isCircleGiftTargetPersonIdRead;

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

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public List<WishItem> getWishItems() {
        return wishItems;
    }

    public void setWishItems(List<WishItem> wishItems) {
        this.wishItems = wishItems;
    }

    public void setIsPersonEventAdmin(Boolean isPersonEventAdmin) {
        this.isPersonEventAdmin = isPersonEventAdmin;
    }

    public Boolean getIsPersonEventAdmin() {
        return isPersonEventAdmin;
    }

    public void setIsPersonParticipatesInCircleGift(Boolean isPersonParticipatesInCircleGift) {
        this.isPersonParticipatesInCircleGift = isPersonParticipatesInCircleGift;
    }

    public Boolean getIsPersonParticipatesInCircleGift() {
        return isPersonParticipatesInCircleGift;
    }

    public void setCircleGiftTargetPersonId(Long circleGiftTargetPersonId) {
        this.circleGiftTargetPersonId = circleGiftTargetPersonId;
    }

    public Long getCircleGiftTargetPersonId() {
        return circleGiftTargetPersonId;
    }

    public Boolean getIsCircleGiftTargetPersonIdRead() {
        return isCircleGiftTargetPersonIdRead;
    }

    public void setIsCircleGiftTargetPersonIdRead(Boolean isCircleGiftTargetPersonIdRead) {
        this.isCircleGiftTargetPersonIdRead = isCircleGiftTargetPersonIdRead;
    }
}
