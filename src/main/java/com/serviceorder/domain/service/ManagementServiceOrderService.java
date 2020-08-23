package com.serviceorder.domain.service;

import java.time.OffsetDateTime;

import com.serviceorder.domain.exception.BusinessException;
import com.serviceorder.domain.exception.ResourceNotFoundException;
import com.serviceorder.domain.model.Client;
import com.serviceorder.domain.model.Comment;
import com.serviceorder.domain.model.ServiceOrder;
import com.serviceorder.domain.model.ServiceOrderStatus;
import com.serviceorder.domain.repository.ClientRepository;
import com.serviceorder.domain.repository.CommentRepository;
import com.serviceorder.domain.repository.ServiceOrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ManagementServiceOrderService {

    @Autowired
    private ServiceOrderRepository repository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CommentRepository commentRepository;

    public ServiceOrder save(ServiceOrder serviceOrder) throws BusinessException {
        Client client = clientRepository.findById(serviceOrder.getClient().getId())
                .orElseThrow(() -> new BusinessException("Client not found."));
        
        serviceOrder.setClient(client);
        serviceOrder.setStatus(ServiceOrderStatus.OPENED);
        serviceOrder.setOpeningDate(OffsetDateTime.now());

        return repository.save(serviceOrder);
    }

    public void delete(Long serviceOrderId){
        repository.deleteById(serviceOrderId);
    }

    public void closeServiceOrder(Long serviceOrderId) {
        ServiceOrder serviceOrder = getServiceOrder(serviceOrderId);

        if(!ServiceOrderStatus.OPENED.equals(serviceOrder.getStatus())){
            throw new BusinessException("Service order cannot be ended.");
        }

        serviceOrder.setStatus(ServiceOrderStatus.FINALIZED);
        serviceOrder.setEndingDate(OffsetDateTime.now());

        repository.save(serviceOrder);
    }

    public Comment addComment(Long serviceOrderId, String description) {
        ServiceOrder serviceOrder = getServiceOrder(serviceOrderId);

        Comment comment = new Comment();
        comment.setDescription(description);
        comment.setSendingDate(OffsetDateTime.now());
        comment.setServiceOrder(serviceOrder);

        return commentRepository.save(comment);
    }

    private ServiceOrder getServiceOrder(Long serviceOrederId) {
        return repository.findById(serviceOrederId)
            .orElseThrow(() -> new ResourceNotFoundException("Service order not found."));
    }
    
}