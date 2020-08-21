package com.serviceorder.presentation.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.serviceorder.domain.model.Client;
import com.serviceorder.domain.repository.ClientRepository;
import com.serviceorder.domain.service.RegistrationClientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/clients")
public class ClientController {

    @Autowired
    private ClientRepository repository;

    @Autowired
    private RegistrationClientService service;

    @GetMapping
    public List<Client> listClients() {
        return repository.findAll();
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<Client> getClientById(@PathVariable Long clientId) {
        Optional<Client> client = repository.findById(clientId);

        if(client.isPresent()) {
            return ResponseEntity.ok(client.get());
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Client addClient(@Valid @RequestBody Client client) {
        return service.save(client);
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<Client> updateClient(@PathVariable Long clientId,
             @Valid @RequestBody Client client) {

        if(!repository.existsById(clientId)) {
            return ResponseEntity.notFound().build();
        }

        client.setId(clientId);
        Client response = service.save(client);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> removeClient(@PathVariable Long clientId) {

        if(!repository.existsById(clientId)) {
            return ResponseEntity.notFound().build();
        }

        service.delete(clientId);

        return ResponseEntity.noContent().build();
    }
    
}