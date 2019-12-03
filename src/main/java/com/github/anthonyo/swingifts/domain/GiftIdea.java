package com.github.anthonyo.swingifts.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.anthonyo.swingifts.web.rest.vm.JsonViews;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * A GiftIdea.
 */
@Entity
@Table(name = "gift_idea")
@JsonView(JsonViews.GiftIdeaGet.class)
public class GiftIdea implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 2048)
    @Column(name = "description", length = 2048, nullable = false)
    private String description;

    @Size(max = 2048)
    @Column(name = "url", length = 2048)
    private String url;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(name = "modification_date")
    private Instant modificationDate;

    @OneToMany(mappedBy = "giftIdea")
    @JsonView({
        JsonViews.ParticipationGet.class,
        JsonViews.GiftIdeaGet.class
    })
    private Set<GiftIdeaReservation> giftIdeaReservations = new HashSet<>();

    @ManyToOne(optional = false)
    @JsonIgnoreProperties("giftIdeas")
    @JsonView({
        JsonViews.ParticipationGet.class,
        JsonViews.GiftIdeaGet.class
    })
    private Participation creator;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("giftIdeas")
    private Participation recipient;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public GiftIdea description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public GiftIdea url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public GiftIdea creationDate(Instant creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Instant getModificationDate() {
        return modificationDate;
    }

    public GiftIdea modificationDate(Instant modificationDate) {
        this.modificationDate = modificationDate;
        return this;
    }

    public void setModificationDate(Instant modificationDate) {
        this.modificationDate = modificationDate;
    }

    public Set<GiftIdeaReservation> getGiftIdeaReservations() {
        return giftIdeaReservations;
    }

    public GiftIdea giftIdeaReservations(Set<GiftIdeaReservation> giftIdeaReservations) {
        this.giftIdeaReservations = giftIdeaReservations;
        return this;
    }

    public GiftIdea addGiftIdeaReservation(GiftIdeaReservation giftIdeaReservation) {
        this.giftIdeaReservations.add(giftIdeaReservation);
        giftIdeaReservation.setGiftIdea(this);
        return this;
    }

    public GiftIdea removeGiftIdeaReservation(GiftIdeaReservation giftIdeaReservation) {
        this.giftIdeaReservations.remove(giftIdeaReservation);
        giftIdeaReservation.setGiftIdea(null);
        return this;
    }

    public void setGiftIdeaReservations(Set<GiftIdeaReservation> giftIdeaReservations) {
        this.giftIdeaReservations = giftIdeaReservations;
    }

    public Participation getCreator() {
        return creator;
    }

    public GiftIdea creator(Participation participation) {
        this.creator = participation;
        return this;
    }

    public void setCreator(Participation participation) {
        this.creator = participation;
    }

    public Participation getRecipient() {
        return recipient;
    }

    public GiftIdea recipient(Participation participation) {
        this.recipient = participation;
        return this;
    }

    public void setRecipient(Participation participation) {
        this.recipient = participation;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GiftIdea)) {
            return false;
        }
        return id != null && id.equals(((GiftIdea) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "GiftIdea{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", url='" + getUrl() + "'" +
            ", creationDate='" + getCreationDate() + "'" +
            ", modificationDate='" + getModificationDate() + "'" +
            "}";
    }
}
