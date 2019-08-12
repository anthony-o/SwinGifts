package com.github.anthonyo.swingifts.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "event")
    private Set<Participation> participations = new HashSet<>();

    @OneToMany(mappedBy = "event")
    private Set<DrawingExclusionGroup> drawingExclusionGroups = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("events")
    private User admin;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
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
            "}";
    }
}
