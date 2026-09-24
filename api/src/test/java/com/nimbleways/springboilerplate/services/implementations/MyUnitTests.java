package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enumeration.ProductType;
import com.nimbleways.springboilerplate.exceptions.OrderNotFoundException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.implementations.ProductProcessor.ProductProcessor;
import com.nimbleways.springboilerplate.services.implementations.ProductProcessor.ProductProcessorFactory;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@UnitTest
public class MyUnitTests {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductProcessorFactory productProcessorFactory;

    @Mock
    private ProductProcessor normalProcessor;

    @Mock
    private ProductProcessor seasonalProcessor;

    @Mock
    private Product normalProduct;

    @Mock
    private Product seasonalProduct;

    @Mock
    private Order order;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldProcessAllProductsAndReturnOrderId() {

        Long orderId = 1L;

        Set<Product> products = new HashSet<>();
        products.add(normalProduct);
        products.add(seasonalProduct);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(order.getId())
                .thenReturn(orderId);

        when(order.getItems())
                .thenReturn(products);

        when(normalProduct.getType())
                .thenReturn(ProductType.NORMAL);

        when(seasonalProduct.getType())
                .thenReturn(ProductType.SEASONAL);

        when(productProcessorFactory.getProcessor(ProductType.NORMAL))
                .thenReturn(normalProcessor);

        when(productProcessorFactory.getProcessor(ProductType.SEASONAL))
                .thenReturn(seasonalProcessor);

        ProcessOrderResponse response =
                productService.processOrder(orderId);

        assertNotNull(response);

        verify(normalProcessor).process(normalProduct);
        verify(seasonalProcessor).process(seasonalProduct);

        verify(productProcessorFactory)
                .getProcessor(ProductType.NORMAL);

        verify(productProcessorFactory)
                .getProcessor(ProductType.SEASONAL);
    }

    @Test
    void shouldThrowOrderNotFoundExceptionWhenOrderDoesNotExist() {

        Long orderId = 99L;

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                OrderNotFoundException.class,
                () -> productService.processOrder(orderId)
        );

        verifyNoInteractions(productProcessorFactory);
    }

    @Test
    void shouldReturnResponseWhenOrderHasNoProducts() {

        Long orderId = 1L;

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        when(order.getId())
                .thenReturn(orderId);

        when(order.getItems())
                .thenReturn(Set.of());

        ProcessOrderResponse response =
                productService.processOrder(orderId);

        assertNotNull(response);

        verifyNoInteractions(productProcessorFactory);
    }
}