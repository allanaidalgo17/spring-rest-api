package com.serviceorder.domain.model.client;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    List<Client> findByName(String name);

    List<Client> findByNameContaining(String name);
}