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
class SeasonalProductProcessorTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private Product product;

    @InjectMocks
    private SeasonalProductProcessor processor;

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
    void shouldSendOutOfStockAndMarkUnavailableWhenRestockOccursAfterSeasonEnd() {
        LocalDate today = LocalDate.now();

        when(product.isAvailable(any(LocalDate.class)))
                .thenReturn(false);
        when(product.getLeadTime()).thenReturn(10);
        when(product.getSeasonEndDate()).thenReturn(today.plusDays(5));
        when(product.getName()).thenReturn("Summer Product");

        processor.process(product);

        verify(notificationService)
                .sendOutOfStockNotification("Summer Product");

        verify(product).markUnavailable();
        verify(productRepository).save(product);

        verify(notificationService, never())
                .sendDelayNotification(anyInt(), anyString());
    }

    @Test
    void shouldSendOutOfStockNotificationWhenSeasonHasNotStartedYet() {
        LocalDate today = LocalDate.now();

        when(product.isAvailable(any(LocalDate.class)))
                .thenReturn(false);
        when(product.getLeadTime()).thenReturn(2);
        when(product.getSeasonEndDate()).thenReturn(today.plusDays(30));
        when(product.getSeasonStartDate()).thenReturn(today.plusDays(5));
        when(product.getName()).thenReturn("Winter Product");

        processor.process(product);

        verify(notificationService)
                .sendOutOfStockNotification("Winter Product");

        verify(product, never()).markUnavailable();
        verify(productRepository).save(product);

        verify(notificationService, never())
                .sendDelayNotification(anyInt(), anyString());
    }

    @Test
    void shouldSendDelayNotificationWhenProductIsInSeasonButUnavailable() {
        LocalDate today = LocalDate.now();

        when(product.isAvailable(any(LocalDate.class)))
                .thenReturn(false);
        when(product.getLeadTime()).thenReturn(3);
        when(product.getSeasonEndDate()).thenReturn(today.plusDays(20));
        when(product.getSeasonStartDate()).thenReturn(today.minusDays(5));
        when(product.getName()).thenReturn("Season Product");

        processor.process(product);

        verify(notificationService)
                .sendDelayNotification(3, "Season Product");

        verify(product, never()).markUnavailable();
        verify(productRepository).save(product);

        verify(notificationService, never())
                .sendOutOfStockNotification(anyString());
    }
}