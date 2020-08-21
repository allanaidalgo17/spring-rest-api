package com.serviceorder.domain.service;

import java.util.Optional;

import com.serviceorder.domain.exception.BusinessException;
import com.serviceorder.domain.model.Client;
import com.serviceorder.domain.repository.ClientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegistrationClientService {

    @Autowired
    private ClientRepository repository;

    public Client save(Client client) throws BusinessException {
        Optional<Client> result = repository.findByEmail(client.getEmail());
        
        if(result.isPresent() && !result.get().equals(client)) {
            throw new BusinessException("Email already exists.");
        }

        return repository.save(client);
    }

    public void delete(Long id){
        repository.deleteById(id);
    }
    
}