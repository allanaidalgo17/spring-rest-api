package com.serviceorder.presentation.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.serviceorder.domain.model.ServiceOrderStatus;

public class ServiceOrderDTO {

    private Long id;
    private ClientDTO client;
    private String description;
    private BigDecimal price;
    private OffsetDateTime openingDate;
    private OffsetDateTime endingDate;
    private ServiceOrderStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClientDTO getClient() {
        return client;
    }

    public void setClient(ClientDTO client) {
        this.client = client;
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

    public ServiceOrderStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceOrderStatus status) {
        this.status = status;
    }
    
}