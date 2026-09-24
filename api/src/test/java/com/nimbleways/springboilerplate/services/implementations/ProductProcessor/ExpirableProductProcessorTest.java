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
class ExpirableProductProcessorTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private Product product;

    @InjectMocks
    private ExpirableProductProcessor processor;

    @Test
    void shouldDecreaseAvailabilityAndSaveWhenProductIsAvailable() {
        when(product.isAvailable(any(LocalDate.class)))
                .thenReturn(true);

        processor.process(product);

        verify(product).decreaseAvailability();
        verify(productRepository).save(product);

        verify(product, never()).markUnavailable();
        verifyNoInteractions(notificationService);
    }

    @Test
    void shouldNotifyExpirationAndMarkUnavailableWhenProductIsNotAvailable() {
        LocalDate expiryDate = LocalDate.now().minusDays(1);

        when(product.isAvailable(any(LocalDate.class)))
                .thenReturn(false);
        when(product.getName()).thenReturn("Milk");
        when(product.getExpiryDate()).thenReturn(expiryDate);

        processor.process(product);

        verify(notificationService)
                .sendExpirationNotification("Milk", expiryDate);

        verify(product).markUnavailable();
        verify(productRepository).save(product);

        verify(product, never()).decreaseAvailability();
    }
}