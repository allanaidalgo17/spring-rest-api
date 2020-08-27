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
import com.serviceorder.domain.model.Comment;
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

    private static final Long SERVICE_ORDER_ID = 1l;

    @Test
    public void shouldSaveServiceOrderSuccessfully() {
        ServiceOrder serviceOrder = createServiceOrder(SERVICE_ORDER_ID);

        when(clientRepository.findById(anyLong())).thenReturn(Optional.of(serviceOrder.getClient()));
        when(repository.save(serviceOrder)).thenReturn(serviceOrder);

        ServiceOrder result = service.createServiceOrder(serviceOrder);

        assertNotNull(result);
        assertEquals(ServiceOrderStatus.OPENED, result.getStatus());
        verify(repository, times(1)).save(any(ServiceOrder.class));
    }

    @Test
    public void shouldThrowExceptionWhenSaveServiceOrderWithNonExistingClient() {
        ServiceOrder serviceOrder = createServiceOrder(SERVICE_ORDER_ID);

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> {
            service.createServiceOrder(serviceOrder);
        });

        verify(repository, never()).save(any(ServiceOrder.class));
    }

    @Test
    public void shouldListAllServiceOrders() {
        List<ServiceOrder> serviceOrders = new ArrayList<>();
        serviceOrders.add(createServiceOrder(1l));
        serviceOrders.add(createServiceOrder(2l));
        serviceOrders.add(createServiceOrder(3l));

        when(repository.findAll()).thenReturn(serviceOrders);

        List<ServiceOrder> result = service.listServiceOrders();

        verify(repository, times(1)).findAll();
        assertEquals(serviceOrders, result);
    }

    @Test
    public void shouldFindServiceOrderById() {
        ServiceOrder serviceOrder = createServiceOrder(SERVICE_ORDER_ID);

        when(repository.findById(anyLong())).thenReturn(Optional.of(serviceOrder));

        Optional<ServiceOrder> result = service.getServiceOrderById(serviceOrder.getId());

        verify(repository, times(1)).findById(serviceOrder.getId());
        assertNotNull(result);
    }

    @Test
    public void shouldDeleteServiceOrder() {
        service.removeServiceOrder(SERVICE_ORDER_ID);

        verify(repository, times(1)).deleteById(SERVICE_ORDER_ID);
    }

    @Test
    public void shouldReturnThatServiceOrderExists() {
        when(repository.existsById(anyLong())).thenReturn(true);

        Boolean result = service.existsById(SERVICE_ORDER_ID);

        verify(repository, times(1)).existsById(SERVICE_ORDER_ID);
        assertTrue(result);
    }

    @Test
    public void shouldReturnThatServiceOrderNotExists() {
        when(repository.existsById(anyLong())).thenReturn(false);

        Boolean result = service.existsById(SERVICE_ORDER_ID);

        verify(repository, times(1)).existsById(SERVICE_ORDER_ID);
        assertFalse(result);
    }

    @Test
    public void shouldCloseServiceOrderSuccessfully() {
        ServiceOrder serviceOrder = createServiceOrder(SERVICE_ORDER_ID);
        serviceOrder.setStatus(ServiceOrderStatus.OPENED);

        when(repository.findById(anyLong())).thenReturn(Optional.of(serviceOrder));
        when(repository.save(serviceOrder)).thenReturn(serviceOrder);

        service.closeServiceOrder(serviceOrder.getId());

        assertEquals(ServiceOrderStatus.FINALIZED, serviceOrder.getStatus());
        verify(repository, times(1)).save(any(ServiceOrder.class));
    }

    @Test
    public void shouldThrowExceptionWhenCloseServiceOrderNonExistent() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.closeServiceOrder(SERVICE_ORDER_ID);
        });

        verify(repository, never()).save(any(ServiceOrder.class));
    }

    @Test
    public void shouldThrowExceptionWhenCloseServiceOrderNotOpened() {
        ServiceOrder serviceOrder = createServiceOrder(SERVICE_ORDER_ID);
        serviceOrder.setStatus(ServiceOrderStatus.CANCELED);

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> {
            service.closeServiceOrder(serviceOrder.getId());
        });

        verify(repository, never()).save(any(ServiceOrder.class));
    }

    @Test
    public void shouldCancelServiceOrderSuccessfully() {
        ServiceOrder serviceOrder = createServiceOrder(SERVICE_ORDER_ID);
        serviceOrder.setStatus(ServiceOrderStatus.OPENED);

        when(repository.findById(anyLong())).thenReturn(Optional.of(serviceOrder));
        when(repository.save(serviceOrder)).thenReturn(serviceOrder);

        service.cancelServiceOrder(serviceOrder.getId());

        assertEquals(ServiceOrderStatus.CANCELED, serviceOrder.getStatus());
        verify(repository, times(1)).save(any(ServiceOrder.class));
    }

    @Test
    public void shouldThrowExceptionWhenCancelServiceOrderNonExistent() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.cancelServiceOrder(SERVICE_ORDER_ID);
        });

        verify(repository, never()).save(any(ServiceOrder.class));
    }

    @Test
    public void shouldThrowExceptionWhenCancelServiceOrderNotOpened() {
        ServiceOrder serviceOrder = createServiceOrder(SERVICE_ORDER_ID);
        serviceOrder.setStatus(ServiceOrderStatus.FINALIZED);

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> {
            service.cancelServiceOrder(serviceOrder.getId());
        });

        verify(repository, never()).save(any(ServiceOrder.class));
    }

    @Test
    public void shouldSaveCommentSuccessfully() {
        ServiceOrder serviceOrder = createServiceOrder(SERVICE_ORDER_ID);
        String description = "description";

        Comment comment = new Comment();
        comment.setDescription(description);
        comment.setServiceOrder(serviceOrder);

        when(repository.findById(anyLong())).thenReturn(Optional.of(serviceOrder));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = service.createComment(serviceOrder.getId(), description);

        assertEquals(description, result.getDescription());
        assertEquals(serviceOrder, result.getServiceOrder());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    public void shouldThrowExceptionWhenSaveCommentWithServiceOrderNonExistent() {
        String description = "description";

        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.createComment(SERVICE_ORDER_ID, description);
        });

        verify(commentRepository, never()).save(any(Comment.class));
    }

    private ServiceOrder createServiceOrder(Long id) {
        Client client = new Client(1l, "name", "name@email.com", "55555555");
        ServiceOrder serviceOrder = new ServiceOrder(id,"description", BigDecimal.TEN, client);

        return serviceOrder;
    }
    
}