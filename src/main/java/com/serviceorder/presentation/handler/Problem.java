package com.serviceorder.presentation.handler;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Builder;

@JsonInclude(Include.NON_NULL)
@Builder
public class Problem {

    private final Integer status;
    private final OffsetDateTime dateTime;
    private final String message;
    private final List<Field> fields;

}