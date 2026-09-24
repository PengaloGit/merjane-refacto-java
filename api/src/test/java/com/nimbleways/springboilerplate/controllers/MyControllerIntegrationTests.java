package com.nimbleways.springboilerplate.controllers;

import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enumeration.ProductType;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;

// import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Specify the controller class you want to test
// This indicates to spring boot to only load UsersController into the context
// Which allows a better performance and needs to do less mocks
@SpringBootTest
@AutoConfigureMockMvc
public class MyControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void processOrderShouldProcessProductsAccordingToTheirType() throws Exception {

        List<Product> products = createProducts();

        productRepository.saveAll(products);

        Order order = createOrder(new HashSet<>(products));
        order = orderRepository.save(order);

        mockMvc.perform(
                        post("/orders/{orderId}/processOrder", order.getId())
                                .contentType("application/json")
                )
                .andExpect(status().isOk());

        Order resultOrder = orderRepository.findById(order.getId())
                .orElseThrow();

        assertEquals(order.getId(), resultOrder.getId());

        assertAvailability(products, "USB Cable", 29);

        assertAvailability(products, "USB Dongle", 0);

        assertAvailability(products, "Keyboard", 4);

        assertAvailability(products, "Butter", 29);

        assertAvailability(products, "Milk", 0);

        assertAvailability(products, "Watermelon", 29);

        assertAvailability(products, "Grapes", 30);

        assertAvailability(products, "Strawberry", 4);

        assertAvailability(products, "Peach", 29);
    }

    @Test
    void processOrderShouldReturnNotFoundWhenOrderDoesNotExist()
            throws Exception {

        Long unknownOrderId = 999999L;

        mockMvc.perform(
                        post(
                                "/orders/{orderId}/processOrder",
                                unknownOrderId
                        )
                )
                .andExpect(status().isNotFound());
    }

    private void assertAvailability(
            List<Product> products,
            String productName,
            Integer expectedAvailability
    ) {

        Product product = products.stream()
                .filter(p -> p.getName().equals(productName))
                .findFirst()
                .orElseThrow();

        Product persistedProduct = productRepository
                .findById(product.getId())
                .orElseThrow();

        Assertions.assertEquals(expectedAvailability, persistedProduct.getAvailable(), "Unexpected availability for " + productName);
    }

    private static Order createOrder(Set<Product> products) {
        Order order = new Order();
        order.setItems(products);
        return order;
    }

    private static List<Product> createProducts() {

        LocalDate today = LocalDate.now();

        List<Product> products = new ArrayList<>();


        products.add(
                new Product(
                        null,
                        15,
                        30,
                        ProductType.NORMAL,
                        "USB Cable",
                        null,
                        null,
                        null
                )
        );


        products.add(
                new Product(
                        null,
                        10,
                        0,
                        ProductType.NORMAL,
                        "USB Dongle",
                        null,
                        null,
                        null
                )
        );


        products.add(
                new Product(
                        null,
                        0,
                        5,
                        ProductType.NORMAL,
                        "Keyboard",
                        null,
                        null,
                        null
                )
        );


        products.add(
                new Product(
                        null,
                        15,
                        30,
                        ProductType.EXPIRABLE,
                        "Butter",
                        today.plusDays(26),
                        null,
                        null
                )
        );


        products.add(
                new Product(
                        null,
                        90,
                        6,
                        ProductType.EXPIRABLE,
                        "Milk",
                        today.minusDays(2),
                        null,
                        null
                )
        );


        products.add(
                new Product(
                        null,
                        15,
                        30,
                        ProductType.SEASONAL,
                        "Watermelon",
                        null,
                        today.minusDays(2),
                        today.plusDays(58)
                )
        );


        products.add(
                new Product(
                        null,
                        15,
                        30,
                        ProductType.SEASONAL,
                        "Grapes",
                        null,
                        today.plusDays(180),
                        today.plusDays(240)
                )
        );


        products.add(
                new Product(
                        null,
                        0,
                        5,
                        ProductType.SEASONAL,
                        "Strawberry",
                        null,
                        today.minusDays(10),
                        today.plusDays(20)
                )
        );

        products.add(
                new Product(
                        null,
                        0,
                        30,
                        ProductType.SEASONAL,
                        "Peach",
                        null,
                        today.minusDays(10),
                        today.plusDays(10)
                )
        );

        return products;
    }
}
