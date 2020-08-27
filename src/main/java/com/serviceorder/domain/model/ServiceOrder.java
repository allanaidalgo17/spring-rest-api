package com.serviceorder.domain.model;

import java.io.Serializable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
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

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ServiceOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String description;

    @NotNull
    private BigDecimal price;

    @JsonProperty(access = Access.READ_ONLY)
    private OffsetDateTime openingDate;

    @Column(name = "ending_date")
    @JsonProperty(access = Access.READ_ONLY)
    private OffsetDateTime closingDate;

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

    public ServiceOrder(Long id, String description, BigDecimal price, Client client) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.client = client;
    }

}