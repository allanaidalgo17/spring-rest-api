package com.serviceorder.presentation.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.serviceorder.domain.exception.ResourceNotFoundException;
import com.serviceorder.domain.model.Comment;
import com.serviceorder.domain.model.ServiceOrder;
import com.serviceorder.domain.repository.CommentRepository;
import com.serviceorder.domain.repository.ServiceOrderRepository;
import com.serviceorder.domain.service.ManagementServiceOrderService;
import com.serviceorder.presentation.model.CommentDTO;
import com.serviceorder.presentation.model.CommentInput;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service-orders/{serviceOrderId}/comments")
public class CommentController {

    @Autowired
    private ServiceOrderRepository serviceOrderRepository;

    @Autowired
    private ManagementServiceOrderService service;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public List<CommentDTO> listComments(@PathVariable Long serviceOrderId) {
        ServiceOrder serviceOrder = serviceOrderRepository.findById(serviceOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Service order not found."));

        return toCollectionModel(serviceOrder.getComments());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO addComment(@PathVariable Long serviceOrderId,
                @Valid @RequestBody CommentInput commentInput) {

        Comment comment = service.addComment(serviceOrderId, commentInput.getDescription());
        return toModel(comment);
    }

    private CommentDTO toModel(Comment comment) {
        return modelMapper.map(comment, CommentDTO.class);
    }

    private List<CommentDTO> toCollectionModel(List<Comment> comments) {
        return comments.stream()
                .map(comment -> toModel(comment))
                .collect(Collectors.toList());
    }
    
}