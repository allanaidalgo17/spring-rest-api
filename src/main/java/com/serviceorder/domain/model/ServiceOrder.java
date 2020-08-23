package com.serviceorder.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.groups.ConvertGroup;
import javax.validation.groups.Default;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.serviceorder.domain.validation.ValidationGroups;

@Entity
public class ServiceOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @NotBlank
    private String description;

    @NotNull
    private BigDecimal price;

    @JsonProperty(access = Access.READ_ONLY)
    private OffsetDateTime openingDate;

    @JsonProperty(access = Access.READ_ONLY)
    private OffsetDateTime endingDate;

    @Valid
    @ConvertGroup(from = Default.class, to = ValidationGroups.ClientId.class)
    @NotNull
    @ManyToOne
    private Client client;

    @OneToMany(mappedBy = "serviceOrder")
    private List<Comment> comments =  new ArrayList<>();

    @JsonProperty(access = Access.READ_ONLY)
    @Enumerated(EnumType.STRING)
    private ServiceOrderStatus status;

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public OffsetDateTime getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(OffsetDateTime openingDate) {
        this.openingDate = openingDate;
    }

    public OffsetDateTime getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(OffsetDateTime endingDate) {
        this.endingDate = endingDate;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public ServiceOrderStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceOrderStatus status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((Id == null) ? 0 : Id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ServiceOrder other = (ServiceOrder) obj;
        if (Id == null) {
            if (other.Id != null)
                return false;
        } else if (!Id.equals(other.Id))
            return false;
        return true;
    }

}