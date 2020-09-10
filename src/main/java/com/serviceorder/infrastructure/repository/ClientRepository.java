package com.serviceorder.domain.repository;

import java.util.List;
import java.util.Optional;

import com.serviceorder.domain.model.Client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    List<Client> findByName(String name);

    List<Client> findByNameContaining(String name);

    Optional<Client> findByEmail(String email);
}