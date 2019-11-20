package com.github.anthonyo.swingifts.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.anthonyo.swingifts.web.rest.vm.JsonViews;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Event.
 */
@Entity
@Table(name = "event")
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonView(JsonViews.EventGet.class)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    @JsonView(JsonViews.EventGet.class)
    private String name;

    @Size(max = 8192)
    @Column(name = "description", length = 8192)
    private String description;

    @Size(max = 32)
    @Column(name = "public_key", length = 32)
    private String publicKey;

    @OneToMany(mappedBy = "event")
    private Set<Participation> participations = new HashSet<>();

    @OneToMany(mappedBy = "event")
    @JsonIgnore
    private Set<GiftDrawing> giftDrawings = new HashSet<>();

    @OneToMany(mappedBy = "event")
    @JsonIgnoreProperties("event")
    @JsonView(JsonViews.EventGet.class)
    private Set<DrawingExclusionGroup> drawingExclusionGroups = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonView(JsonViews.EventGet.class)
    private User admin;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    @Transient
    private Set<Participation> myGiftDrawingRecipients = new HashSet<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Event name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Event description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public Event publicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public Set<Participation> getParticipations() {
        return participations;
    }

    public Event participations(Set<Participation> participations) {
        this.participations = participations;
        return this;
    }

    public Event addParticipations(Participation participation) {
        this.participations.add(participation);
        participation.setEvent(this);
        return this;
    }

    public Event removeParticipations(Participation participation) {
        this.participations.remove(participation);
        participation.setEvent(null);
        return this;
    }

    public void setParticipations(Set<Participation> participations) {
        this.participations = participations;
    }

    public Set<GiftDrawing> getGiftDrawings() {
        return giftDrawings;
    }

    public Event giftDrawings(Set<GiftDrawing> giftDrawings) {
        this.giftDrawings = giftDrawings;
        return this;
    }

    public Event addGiftDrawing(GiftDrawing giftDrawing) {
        this.giftDrawings.add(giftDrawing);
        giftDrawing.setEvent(this);
        return this;
    }

    public Event removeGiftDrawing(GiftDrawing giftDrawing) {
        this.giftDrawings.remove(giftDrawing);
        giftDrawing.setEvent(null);
        return this;
    }

    public void setGiftDrawings(Set<GiftDrawing> giftDrawings) {
        this.giftDrawings = giftDrawings;
    }

    public Set<DrawingExclusionGroup> getDrawingExclusionGroups() {
        return drawingExclusionGroups;
    }

    public Event drawingExclusionGroups(Set<DrawingExclusionGroup> drawingExclusionGroups) {
        this.drawingExclusionGroups = drawingExclusionGroups;
        return this;
    }

    public Event addDrawingExclusionGroup(DrawingExclusionGroup drawingExclusionGroup) {
        this.drawingExclusionGroups.add(drawingExclusionGroup);
        drawingExclusionGroup.setEvent(this);
        return this;
    }

    public Event removeDrawingExclusionGroup(DrawingExclusionGroup drawingExclusionGroup) {
        this.drawingExclusionGroups.remove(drawingExclusionGroup);
        drawingExclusionGroup.setEvent(null);
        return this;
    }

    public void setDrawingExclusionGroups(Set<DrawingExclusionGroup> drawingExclusionGroups) {
        this.drawingExclusionGroups = drawingExclusionGroups;
    }

    public User getAdmin() {
        return admin;
    }

    public Event admin(User user) {
        this.admin = user;
        return this;
    }

    public void setAdmin(User user) {
        this.admin = user;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @JsonView(JsonViews.EventGet.class)
    public Set<Participation> getMyGiftDrawingRecipients() {
        return myGiftDrawingRecipients;
    }

    public void setMyGiftDrawingRecipients(Set<Participation> myGiftDrawingRecipients) {
        this.myGiftDrawingRecipients = myGiftDrawingRecipients;
    }

    public Event myGiftDrawingRecipients(Set<Participation> myGiftDrawingRecipients) {
        this.myGiftDrawingRecipients = myGiftDrawingRecipients;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        return id != null && id.equals(((Event) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Event{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", publicKey='" + getPublicKey() + "'" +
            "}";
    }
}
