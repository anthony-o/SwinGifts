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

    @OneToMany(mappedBy = "recipient")
    private Set<GiftIdea> giftIdeas = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("participations")
    private User user;

    @ManyToMany
    @JoinTable(name = "gift_drawing",
               joinColumns = @JoinColumn(name = "donor_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "recipient_id", referencedColumnName = "id"))
    private Set<Participation> recipients = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("participations")
    private Event event;

    @ManyToMany(mappedBy = "recipients")
    @JsonIgnore
    private Set<Participation> donors = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "drawing_exclusion_group_participation",
               joinColumns = @JoinColumn(name = "participation_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "drawing_exclusion_group_id", referencedColumnName = "id"))
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

    public Set<Participation> getRecipients() {
        return recipients;
    }

    public Participation recipients(Set<Participation> participations) {
        this.recipients = participations;
        return this;
    }

    public Participation addRecipient(Participation participation) {
        this.recipients.add(participation);
        participation.getDonors().add(this);
        return this;
    }

    public Participation removeRecipient(Participation participation) {
        this.recipients.remove(participation);
        participation.getDonors().remove(this);
        return this;
    }

    public void setRecipients(Set<Participation> participations) {
        this.recipients = participations;
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

    public Set<Participation> getDonors() {
        return donors;
    }

    public Participation donors(Set<Participation> participations) {
        this.donors = participations;
        return this;
    }

    public Participation addDonor(Participation participation) {
        this.donors.add(participation);
        participation.getRecipients().add(this);
        return this;
    }

    public Participation removeDonor(Participation participation) {
        this.donors.remove(participation);
        participation.getRecipients().remove(this);
        return this;
    }

    public void setDonors(Set<Participation> participations) {
        this.donors = participations;
    }

    public Set<DrawingExclusionGroup> getDrawingExclusionGroups() {
        return drawingExclusionGroups;
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
