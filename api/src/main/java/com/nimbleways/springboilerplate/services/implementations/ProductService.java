package com.nimbleways.springboilerplate.services.implementations;

import java.util.Set;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.exceptions.OrderNotFoundException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.services.implementations.ProductProcessor.ProductProcessorFactory;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nimbleways.springboilerplate.entities.Product;


@AllArgsConstructor
@Service
public class ProductService {

    private final OrderRepository orderRepository;

    private ProductProcessorFactory productProcessorFactory;

    public ProcessOrderResponse processOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        Set<Product> products = order.getItems();
        for (Product product : products) {
            productProcessorFactory.getProcessor(product.getType()).process(product);
        }
        return new ProcessOrderResponse(order.getId());
    }
}