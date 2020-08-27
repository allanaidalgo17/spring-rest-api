package com.serviceorder.domain.service;

import java.util.List;
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

    public List<Client> listClients() {
        return repository.findAll();
    }

    public Optional<Client> getClientById(Long clientId) {
        return repository.findById(clientId);
    }

    public Client createClient(Client client) throws BusinessException {
        Optional<Client> result = repository.findByEmail(client.getEmail());
        
        if(result.isPresent() && !result.get().equals(client)) {
            throw new BusinessException("Email already exists.");
        }

        return repository.save(client);
    }

    public void removeClient(Long id){
        repository.deleteById(id);
    }

    public Boolean existsById(Long id) {
        return repository.existsById(id);
    }
    
}