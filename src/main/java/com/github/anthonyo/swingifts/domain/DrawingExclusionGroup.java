package com.github.anthonyo.swingifts.domain;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A DrawingExclusionGroup.
 */
@Entity
@Table(name = "drawing_exclusion_group")
public class DrawingExclusionGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(name = "drawing_exclusion_group_participation",
               joinColumns = @JoinColumn(name = "drawing_exclusion_group_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "participation_id", referencedColumnName = "id"))
    private Set<Participation> participations = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnore
    private Event event;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Participation> getParticipations() {
        return participations;
    }

    public DrawingExclusionGroup participations(Set<Participation> participations) {
        this.participations = participations;
        return this;
    }

    public DrawingExclusionGroup addParticipation(Participation participation) {
        this.participations.add(participation);
        participation.getDrawingExclusionGroups().add(this);
        return this;
    }

    public DrawingExclusionGroup removeParticipation(Participation participation) {
        this.participations.remove(participation);
        participation.getDrawingExclusionGroups().remove(this);
        return this;
    }

    public void setParticipations(Set<Participation> participations) {
        this.participations = participations;
    }

    public Event getEvent() {
        return event;
    }

    public DrawingExclusionGroup event(Event event) {
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
        if (!(o instanceof DrawingExclusionGroup)) {
            return false;
        }
        return id != null && id.equals(((DrawingExclusionGroup) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "DrawingExclusionGroup{" +
            "id=" + getId() +
            "}";
    }
}
