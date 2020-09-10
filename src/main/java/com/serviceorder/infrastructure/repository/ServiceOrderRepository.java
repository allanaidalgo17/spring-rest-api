package com.serviceorder.domain.repository;

import com.serviceorder.domain.model.ServiceOrder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {
    
}