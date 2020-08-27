package com.serviceorder.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.serviceorder.domain.exception.BusinessException;
import com.serviceorder.domain.model.Client;
import com.serviceorder.domain.repository.ClientRepository;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RegistrationClientServiceTest {

    @InjectMocks
    private RegistrationClientService service;

    @Mock
    private ClientRepository repository;

    private static final Long CLIENT_ID = 1l;

    @Test
    public void shouldSaveClientSuccessfully() {
        Client client = createClient(CLIENT_ID);

        when(repository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(repository.save(client)).thenReturn(client);

        Client result = service.createClient(client);

        assertNotNull(result);
        verify(repository, times(1)).save(any(Client.class));
    }

    @Test
    public void shouldThrowExceptionWhenSaveClientWithExistingEmail() {
        Client client = createClient(CLIENT_ID);

        when(repository.findByEmail(anyString())).thenReturn(Optional.of(new Client()));

        assertThrows(BusinessException.class, () -> {
            service.createClient(client);
        });

        verify(repository, never()).save(any(Client.class));
    }

    @Test
    public void shouldListAllClients() {
        List<Client> clients = new ArrayList<>();
        clients.add(createClient(CLIENT_ID));
        clients.add(createClient(2l));
        clients.add(createClient(3l));

        when(repository.findAll()).thenReturn(clients);

        List<Client> result = service.listClients();

        verify(repository, times(1)).findAll();
        assertEquals(clients, result);
    }

    @Test
    public void shouldFindClientById() {
        Client client = createClient(CLIENT_ID);

        when(repository.findById(anyLong())).thenReturn(Optional.of(client));

        Optional<Client> result = service.getClientById(client.getId());

        verify(repository, times(1)).findById(client.getId());
        assertNotNull(result);
    }

    @Test
    public void shouldDeleteClient() {
        service.removeClient(CLIENT_ID);

        verify(repository, times(1)).deleteById(CLIENT_ID);
    }

    @Test
    public void shouldReturnThatClientExists() {
        when(repository.existsById(anyLong())).thenReturn(true);

        Boolean result = service.existsById(CLIENT_ID);

        verify(repository, times(1)).existsById(CLIENT_ID);
        assertTrue(result);
    }

    @Test
    public void shouldReturnThatClientNotExists() {
        when(repository.existsById(anyLong())).thenReturn(false);

        Boolean result = service.existsById(CLIENT_ID);

        verify(repository, times(1)).existsById(CLIENT_ID);
        assertFalse(result);
    }

    private Client createClient(Long id) {
        return new Client(id, "name", "name@email.com", "55555555");
    }
    
}