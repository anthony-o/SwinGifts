package com.github.anthonyo.swingifts.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Participation.
 */
@Entity
@Table(name = "participation")
public class Participation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 0)
    @Column(name = "nb_of_gift_to_receive")
    private Integer nbOfGiftToReceive;

    @Min(value = 0)
    @Column(name = "nb_of_gift_to_donate")
    private Integer nbOfGiftToDonate;

    @NotNull
    @Column(name = "user_alias", nullable = false)
    private String userAlias;

    @OneToMany(mappedBy = "donor")
    private Set<GiftDrawing> giftDrawings = new HashSet<>();

    @OneToMany(mappedBy = "recipient")
    private Set<GiftIdea> giftIdeas = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("participations")
    private User user;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("participations")
    private Event event;

    @ManyToMany(mappedBy = "participations")
    @JsonIgnore
    private Set<DrawingExclusionGroup> drawingExclusionGroups = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNbOfGiftToReceive() {
        return nbOfGiftToReceive;
    }

    public Participation nbOfGiftToReceive(Integer nbOfGiftToReceive) {
        this.nbOfGiftToReceive = nbOfGiftToReceive;
        return this;
    }

    public void setNbOfGiftToReceive(Integer nbOfGiftToReceive) {
        this.nbOfGiftToReceive = nbOfGiftToReceive;
    }

    public Integer getNbOfGiftToDonate() {
        return nbOfGiftToDonate;
    }

    public Participation nbOfGiftToDonate(Integer nbOfGiftToDonate) {
        this.nbOfGiftToDonate = nbOfGiftToDonate;
        return this;
    }

    public void setNbOfGiftToDonate(Integer nbOfGiftToDonate) {
        this.nbOfGiftToDonate = nbOfGiftToDonate;
    }

    public String getUserAlias() {
        return userAlias;
    }

    public Participation userAlias(String userAlias) {
        this.userAlias = userAlias;
        return this;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public Set<GiftDrawing> getGiftDrawings() {
        return giftDrawings;
    }

    public Participation giftDrawings(Set<GiftDrawing> giftDrawings) {
        this.giftDrawings = giftDrawings;
        return this;
    }

    public Participation addGiftDrawing(GiftDrawing giftDrawing) {
        this.giftDrawings.add(giftDrawing);
        giftDrawing.setDonor(this);
        return this;
    }

    public Participation removeGiftDrawing(GiftDrawing giftDrawing) {
        this.giftDrawings.remove(giftDrawing);
        giftDrawing.setDonor(null);
        return this;
    }

    public void setGiftDrawings(Set<GiftDrawing> giftDrawings) {
        this.giftDrawings = giftDrawings;
    }

    public Set<GiftIdea> getGiftIdeas() {
        return giftIdeas;
    }

    public Participation giftIdeas(Set<GiftIdea> giftIdeas) {
        this.giftIdeas = giftIdeas;
        return this;
    }

    public Participation addGiftIdea(GiftIdea giftIdea) {
        this.giftIdeas.add(giftIdea);
        giftIdea.setRecipient(this);
        return this;
    }

    public Participation removeGiftIdea(GiftIdea giftIdea) {
        this.giftIdeas.remove(giftIdea);
        giftIdea.setRecipient(null);
        return this;
    }

    public void setGiftIdeas(Set<GiftIdea> giftIdeas) {
        this.giftIdeas = giftIdeas;
    }

    public User getUser() {
        return user;
    }

    public Participation user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public Participation event(Event event) {
        this.event = event;
        return this;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Set<DrawingExclusionGroup> getDrawingExclusionGroups() {
        return drawingExclusionGroups;
    }

    public Participation drawingExclusionGroups(Set<DrawingExclusionGroup> drawingExclusionGroups) {
        this.drawingExclusionGroups = drawingExclusionGroups;
        return this;
    }

    public Participation addDrawingExclusionGroup(DrawingExclusionGroup drawingExclusionGroup) {
        this.drawingExclusionGroups.add(drawingExclusionGroup);
        drawingExclusionGroup.getParticipations().add(this);
        return this;
    }

    public Participation removeDrawingExclusionGroup(DrawingExclusionGroup drawingExclusionGroup) {
        this.drawingExclusionGroups.remove(drawingExclusionGroup);
        drawingExclusionGroup.getParticipations().remove(this);
        return this;
    }

    public void setDrawingExclusionGroups(Set<DrawingExclusionGroup> drawingExclusionGroups) {
        this.drawingExclusionGroups = drawingExclusionGroups;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Participation)) {
            return false;
        }
        return id != null && id.equals(((Participation) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Participation{" +
            "id=" + getId() +
            ", nbOfGiftToReceive=" + getNbOfGiftToReceive() +
            ", nbOfGiftToDonate=" + getNbOfGiftToDonate() +
            ", userAlias='" + getUserAlias() + "'" +
            "}";
    }
}
