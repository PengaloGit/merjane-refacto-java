package com.nimbleways.springboilerplate.services.implementations.ProductProcessor;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NormalProductProcessorTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private Product product;

    @InjectMocks
    private NormalProductProcessor processor;

    @Test
    void shouldDecreaseAvailabilityAndSaveWhenProductIsAvailable() {
        when(product.isAvailable(any(LocalDate.class)))
                .thenReturn(true);

        processor.process(product);

        verify(product).decreaseAvailability();
        verify(productRepository).save(product);
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldSendDelayNotificationWhenProductIsNotAvailableAndHasLeadTime() {
        when(product.isAvailable(any(LocalDate.class)))
                .thenReturn(false);
        when(product.getLeadTime()).thenReturn(5);
        when(product.getName()).thenReturn("Product A");

        processor.process(product);

        verify(notificationService)
                .sendDelayNotification(5, "Product A");

        verify(product, never()).decreaseAvailability();
        verify(productRepository, never()).save(product);
    }

    @Test
    void shouldDoNothingWhenProductIsNotAvailableAndLeadTimeIsZero() {
        when(product.isAvailable(any(LocalDate.class)))
                .thenReturn(false);
        when(product.getLeadTime()).thenReturn(0);

        processor.process(product);

        verify(product, never()).decreaseAvailability();
        verify(productRepository, never()).save(product);
        verifyNoInteractions(notificationService);
    }
}