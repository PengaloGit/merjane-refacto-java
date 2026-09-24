package com.nimbleways.springboilerplate.services.implementations.ProductProcessor;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
@AllArgsConstructor
class ExpirableProductProcessor implements ProductProcessor {


    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public void process(Product product) {

        if (product.isAvailable(LocalDate.now())) {
            product.decreaseAvailability();
            productRepository.save(product);
            return;
        }

        notificationService.sendExpirationNotification(product.getName(), product.getExpiryDate());

        product.markUnavailable();

        productRepository.save(product);
    }
}
