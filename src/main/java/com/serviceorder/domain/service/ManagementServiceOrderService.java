package com.serviceorder.domain.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

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

    public List<ServiceOrder> listServiceOrders() {
        return repository.findAll();
    }

    public Optional<ServiceOrder> getServiceOrderById(Long serviceOrderId) {
        return repository.findById(serviceOrderId);
    }

    public ServiceOrder createServiceOrder(ServiceOrder serviceOrder) throws BusinessException {
        Client client = clientRepository.findById(serviceOrder.getClient().getId())
                .orElseThrow(() -> new BusinessException("Client not found."));
        
        serviceOrder.setClient(client);
        serviceOrder.setStatus(ServiceOrderStatus.OPENED);
        serviceOrder.setOpeningDate(OffsetDateTime.now());

        return repository.save(serviceOrder);
    }

    public void removeServiceOrder(Long serviceOrderId){
        repository.deleteById(serviceOrderId);
    }

    public void closeServiceOrder(Long serviceOrderId) {
        ServiceOrder serviceOrder = getServiceOrder(serviceOrderId);

        if(!ServiceOrderStatus.OPENED.equals(serviceOrder.getStatus())){
            throw new BusinessException("Service order cannot be closed.");
        }

        serviceOrder.setStatus(ServiceOrderStatus.FINALIZED);
        serviceOrder.setClosingDate(OffsetDateTime.now());

        repository.save(serviceOrder);
    }

    public void cancelServiceOrder(Long serviceOrderId) {
        ServiceOrder serviceOrder = getServiceOrder(serviceOrderId);

        if(!ServiceOrderStatus.OPENED.equals(serviceOrder.getStatus())){
            throw new BusinessException("Service order cannot be closed.");
        }

        serviceOrder.setStatus(ServiceOrderStatus.CANCELED);
        serviceOrder.setClosingDate(OffsetDateTime.now());

        repository.save(serviceOrder);
    }

    public Comment createComment(Long serviceOrderId, String description) {
        ServiceOrder serviceOrder = getServiceOrder(serviceOrderId);

        Comment comment = new Comment();
        comment.setDescription(description);
        comment.setSendingDate(OffsetDateTime.now());
        comment.setServiceOrder(serviceOrder);

        return commentRepository.save(comment);
    }

    private ServiceOrder getServiceOrder(Long serviceOrderId) {
        return repository.findById(serviceOrderId)
            .orElseThrow(() -> new ResourceNotFoundException("Service order not found."));
    }

    public Boolean existsById(Long id) {
        return repository.existsById(id);
    }
    
}