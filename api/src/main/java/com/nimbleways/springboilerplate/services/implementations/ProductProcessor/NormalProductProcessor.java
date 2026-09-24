package com.nimbleways.springboilerplate.services.implementations.ProductProcessor;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
class NormalProductProcessor implements ProductProcessor {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public void process(Product product) {
        if (product.isAvailable(LocalDate.now())) {
            product.decreaseAvailability();
            productRepository.save(product);
            return;
        }

        if (product.getLeadTime() > 0) {
            notificationService.sendDelayNotification(product.getLeadTime(), product.getName());
        }
    }

}
