package com.github.anthonyo.swingifts.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * A GiftIdeaReservation.
 */
@Entity
@Table(name = "gift_idea_reservation")
public class GiftIdeaReservation implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("giftIdeaReservations")
    private Participation participation;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("giftIdeaReservations")
    private GiftIdea giftIdea;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public GiftIdeaReservation creationDate(Instant creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Participation getParticipation() {
        return participation;
    }

    public GiftIdeaReservation participation(Participation participation) {
        this.participation = participation;
        return this;
    }

    public void setParticipation(Participation participation) {
        this.participation = participation;
    }

    public GiftIdea getGiftIdea() {
        return giftIdea;
    }

    public GiftIdeaReservation giftIdea(GiftIdea giftIdea) {
        this.giftIdea = giftIdea;
        return this;
    }

    public void setGiftIdea(GiftIdea giftIdea) {
        this.giftIdea = giftIdea;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GiftIdeaReservation)) {
            return false;
        }
        return id != null && id.equals(((GiftIdeaReservation) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "GiftIdeaReservation{" +
            "id=" + getId() +
            ", creationDate='" + getCreationDate() + "'" +
            "}";
    }
}
