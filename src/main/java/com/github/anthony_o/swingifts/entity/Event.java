package com.github.anthony_o.swingifts.entity;

public class Event {
    private Long id;
    private String name;
    private String key;
    private Boolean isOpened;
    private Boolean isCircleGiftLaunchedOnce;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Boolean getIsOpened() {
        return isOpened;
    }

    public void setIsOpened(Boolean isOpened) {
        this.isOpened = isOpened;
    }

    public Boolean getIsCircleGiftLaunchedOnce() {
        return isCircleGiftLaunchedOnce;
    }

    public void setIsCircleGiftLaunchedOnce(Boolean isCircleGiftLaunchedOnce) {
        this.isCircleGiftLaunchedOnce = isCircleGiftLaunchedOnce;
    }
}
