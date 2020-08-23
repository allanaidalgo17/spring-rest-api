package com.serviceorder.presentation.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.serviceorder.domain.model.ServiceOrder;
import com.serviceorder.domain.repository.ServiceOrderRepository;
import com.serviceorder.domain.service.ManagementServiceOrderService;
import com.serviceorder.presentation.model.ServiceOrderDTO;
import com.serviceorder.presentation.model.ServiceOrderInput;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service-orders")
public class ServiceOrderController {

    @Autowired
    private ServiceOrderRepository repository;

    @Autowired
    private ManagementServiceOrderService service;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    public List<ServiceOrderDTO> listServiceOrders() {
        return toCollectionModel(repository.findAll());
    }

    @GetMapping("/{serviceOrderId}")
    public ResponseEntity<ServiceOrderDTO> getServiceOrderById(@PathVariable Long serviceOrderId) {
        Optional<ServiceOrder> serviceOrder = repository.findById(serviceOrderId);

        if(serviceOrder.isPresent()) {
            ServiceOrderDTO serviceOrderDTO = toModel(serviceOrder.get());
            return ResponseEntity.ok(serviceOrderDTO);
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceOrderDTO addServiceOrder(@Valid @RequestBody ServiceOrderInput serviceOrderInput) {
        return toModel(repository.save(toEntity(serviceOrderInput)));
    }

    @PutMapping("/{serviceOrderId}")
    public ResponseEntity<ServiceOrder> updateServiceOrder(@PathVariable Long serviceOrderId,
             @Valid @RequestBody ServiceOrderInput serviceOrderInput) {

        if(!repository.existsById(serviceOrderId)) {
            return ResponseEntity.notFound().build();
        }

        ServiceOrder serviceOrder = toEntity(serviceOrderInput);
        serviceOrder.setId(serviceOrderId);
        ServiceOrder response = service.save(serviceOrder);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{serviceOrderId}/closing")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeServiceOrder(@PathVariable Long serviceOrderId) {
        service.closeServiceOrder(serviceOrderId);
    }

    private ServiceOrderDTO toModel(ServiceOrder serviceOrder) {
        return modelMapper.map(serviceOrder, ServiceOrderDTO.class);      
    }

    private List<ServiceOrderDTO> toCollectionModel(List<ServiceOrder> serviceOrders) {
        return serviceOrders.stream()
                .map(serviceOrder -> toModel(serviceOrder))
                .collect(Collectors.toList());
    }

    private ServiceOrder toEntity(ServiceOrderInput serviceOrderInput) {
        return modelMapper.map(serviceOrderInput, ServiceOrder.class);
    }
    
}