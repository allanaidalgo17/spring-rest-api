package com.serviceorder.presentation.model;

import java.time.OffsetDateTime;

public class CommentDTO {

    private Long id;
    private String description;
    private OffsetDateTime sendingDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getSendingDate() {
        return sendingDate;
    }

    public void setSendingDate(OffsetDateTime sendingDate) {
        this.sendingDate = sendingDate;
    }
    
}