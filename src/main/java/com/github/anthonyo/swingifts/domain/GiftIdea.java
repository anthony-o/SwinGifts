package com.github.anthonyo.swingifts.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * A GiftIdea.
 */
@Entity
@Table(name = "gift_idea")
public class GiftIdea implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "url")
    private String url;

    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;

    @Column(name = "modification_date")
    private Instant modificationDate;

    @ManyToOne(optional = false)
    @JsonIgnoreProperties("giftIdeas")
    private Participation creator;

    @ManyToOne
    @JsonIgnoreProperties("giftIdeas")
    private Participation taker;

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

    public Participation getTaker() {
        return taker;
    }

    public GiftIdea taker(Participation participation) {
        this.taker = participation;
        return this;
    }

    public void setTaker(Participation participation) {
        this.taker = participation;
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
