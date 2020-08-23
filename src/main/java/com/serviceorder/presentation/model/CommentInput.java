package com.serviceorder.presentation.model;

import javax.validation.constraints.NotBlank;

public class CommentInput {

    @NotBlank
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}