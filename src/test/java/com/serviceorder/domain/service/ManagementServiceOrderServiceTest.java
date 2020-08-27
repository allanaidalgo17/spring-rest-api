package com.serviceorder.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.serviceorder.domain.exception.BusinessException;
import com.serviceorder.domain.exception.ResourceNotFoundException;
import com.serviceorder.domain.model.Client;
import com.serviceorder.domain.model.ServiceOrder;
import com.serviceorder.domain.model.ServiceOrderStatus;
import com.serviceorder.domain.repository.ClientRepository;
import com.serviceorder.domain.repository.CommentRepository;
import com.serviceorder.domain.repository.ServiceOrderRepository;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ManagementServiceOrderServiceTest {

    @InjectMocks
    private ManagementServiceOrderService service;

    @Mock
    private ServiceOrderRepository repository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CommentRepository commentRepository;

    @Test
    public void shouldSaveServiceOrderSuccessfully() {
        Client client = new Client(1l, "name", "name@email.com", "55555555");
        ServiceOrder serviceOrder = new ServiceOrder(1l,"description", BigDecimal.TEN, client);

        when(clientRepository.findById(anyLong())).thenReturn(Optional.of(client));
        when(repository.save(serviceOrder)).thenReturn(serviceOrder);

        ServiceOrder result = service.createServiceOrder(serviceOrder);

        assertNotNull(result);
        assertEquals(client, result.getClient());
        assertEquals(ServiceOrderStatus.OPENED, result.getStatus());
        verify(repository, times(1)).save(any(ServiceOrder.class));
    }

    @Test
    public void shouldThrowExceptionWhenSaveServiceOrderWithNonExistingClient() {
        ServiceOrder serviceOrder = new ServiceOrder(1l,"description", BigDecimal.TEN, new Client());

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> {
            service.createServiceOrder(serviceOrder);
        });

        verify(repository, never()).save(any(ServiceOrder.class));
    }

    @Test
    public void shouldListAllServiceOrders() {
        List<ServiceOrder> serviceOrders = new ArrayList<>();
        serviceOrders.add(new ServiceOrder(1l,"description", BigDecimal.TEN, new Client()));
        serviceOrders.add(new ServiceOrder(2l,"description2", BigDecimal.TEN, new Client()));
        serviceOrders.add(new ServiceOrder(3l,"description3", BigDecimal.TEN, new Client()));

        when(repository.findAll()).thenReturn(serviceOrders);

        List<ServiceOrder> result = service.listServiceOrders();

        verify(repository, times(1)).findAll();
        assertEquals(serviceOrders, result);
    }

    @Test
    public void shouldFindServiceOrderById() {
        ServiceOrder serviceOrder = new ServiceOrder();

        when(repository.findById(anyLong())).thenReturn(Optional.of(serviceOrder));

        Optional<ServiceOrder> result = service.getServiceOrderById(serviceOrder.getId());

        verify(repository, times(1)).findById(serviceOrder.getId());
        assertNotNull(result);
    }

    @Test
    public void shouldDeleteServiceOrder() {
        Long serviceOrderId = 1l;

        service.removeServiceOrder(serviceOrderId);

        verify(repository, times(1)).deleteById(serviceOrderId);
    }

    @Test
    public void shouldReturnThatServiceOrderExists() {
        Long serviceOrderId = 1l;

        when(repository.existsById(anyLong())).thenReturn(true);

        Boolean result = service.existsById(serviceOrderId);

        verify(repository, times(1)).existsById(serviceOrderId);
        assertTrue(result);
    }

    @Test
    public void shouldReturnThatServiceOrderNotExists() {
        Long serviceOrderId = 1l;

        when(repository.existsById(anyLong())).thenReturn(false);

        Boolean result = service.existsById(serviceOrderId);

        verify(repository, times(1)).existsById(serviceOrderId);
        assertFalse(result);
    }

    @Test
    public void shouldCloseServiceOrderSuccessfully() {
        ServiceOrder serviceOrder = new ServiceOrder(1l,"description", BigDecimal.TEN, new Client());
        serviceOrder.setStatus(ServiceOrderStatus.OPENED);

        when(repository.findById(anyLong())).thenReturn(Optional.of(serviceOrder));
        when(repository.save(serviceOrder)).thenReturn(serviceOrder);

        service.closeServiceOrder(serviceOrder.getId());

        assertEquals(ServiceOrderStatus.FINALIZED, serviceOrder.getStatus());
        verify(repository, times(1)).save(any(ServiceOrder.class));
    }

    @Test
    public void shouldThrowExceptionWhenCloseServiceOrderNonExistent() {
        Long serviceOrderId = 1l;

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.closeServiceOrder(serviceOrderId);
        });

        verify(repository, never()).save(any(ServiceOrder.class));
    }

    @Test
    public void shouldThrowExceptionWhenCloseServiceOrderNotOpened() {
        ServiceOrder serviceOrder = new ServiceOrder(1l,"description", BigDecimal.TEN, new Client());
        serviceOrder.setStatus(ServiceOrderStatus.CANCELED);

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> {
            service.closeServiceOrder(serviceOrder.getId());
        });

        verify(repository, never()).save(any(ServiceOrder.class));
    }

    @Test
    public void shouldCancelServiceOrderSuccessfully() {
        ServiceOrder serviceOrder = new ServiceOrder(1l,"description", BigDecimal.TEN, new Client());
        serviceOrder.setStatus(ServiceOrderStatus.OPENED);

        when(repository.findById(anyLong())).thenReturn(Optional.of(serviceOrder));
        when(repository.save(serviceOrder)).thenReturn(serviceOrder);

        service.cancelServiceOrder(serviceOrder.getId());

        assertEquals(ServiceOrderStatus.CANCELED, serviceOrder.getStatus());
        verify(repository, times(1)).save(any(ServiceOrder.class));
    }

    @Test
    public void shouldThrowExceptionWhenCancelServiceOrderNonExistent() {
        Long serviceOrderId = 1l;

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.cancelServiceOrder(serviceOrderId);
        });

        verify(repository, never()).save(any(ServiceOrder.class));
    }

    @Test
    public void shouldThrowExceptionWhenCancelServiceOrderNotOpened() {
        ServiceOrder serviceOrder = new ServiceOrder(1l,"description", BigDecimal.TEN, new Client());
        serviceOrder.setStatus(ServiceOrderStatus.FINALIZED);

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> {
            service.cancelServiceOrder(serviceOrder.getId());
        });

        verify(repository, never()).save(any(ServiceOrder.class));
    }
    
}