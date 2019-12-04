package com.github.anthonyo.swingifts.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.github.anthonyo.swingifts.web.rest.vm.JsonViews;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;

/**
 * A GiftDrawing.
 */
@Entity
@Table(name = "gift_drawing")
public class GiftDrawing implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("giftDrawings")
    @JsonView(JsonViews.EventGet.class)
    private Participation recipient;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("giftDrawings")
    private Participation donor;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("giftDrawings")
    private Event event;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Participation getRecipient() {
        return recipient;
    }

    public GiftDrawing recipient(Participation participation) {
        this.recipient = participation;
        return this;
    }

    public void setRecipient(Participation participation) {
        this.recipient = participation;
    }

    public Participation getDonor() {
        return donor;
    }

    public GiftDrawing donor(Participation participation) {
        this.donor = participation;
        return this;
    }

    public void setDonor(Participation participation) {
        this.donor = participation;
    }

    public Event getEvent() {
        return event;
    }

    public GiftDrawing event(Event event) {
        this.event = event;
        return this;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GiftDrawing)) {
            return false;
        }
        return id != null && id.equals(((GiftDrawing) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "GiftDrawing{" +
            "id=" + getId() +
            "}";
    }
}
