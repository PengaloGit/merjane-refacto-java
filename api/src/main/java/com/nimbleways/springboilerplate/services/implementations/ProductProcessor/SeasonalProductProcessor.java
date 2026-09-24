package com.nimbleways.springboilerplate.services.implementations.ProductProcessor;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.implementations.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
class SeasonalProductProcessor implements ProductProcessor {

    private final ProductRepository productRepository;
    private final NotificationService notificationService;

    @Override
    public void process(Product product) {
        LocalDate today = LocalDate.now();

        if (product.isAvailable(today)) {
            product.decreaseAvailability();
            productRepository.save(product);
            return;
        }

        if (restockWouldOccurAfterSeasonEnds(product, today)) {
            notificationService.sendOutOfStockNotification(product.getName());
            product.markUnavailable();

        }
        else if (seasonHasNotStartedYet(product, today)) {
            notificationService.sendOutOfStockNotification(product.getName());

        }
        else {
            notificationService.sendDelayNotification(
                    product.getLeadTime(),
                    product.getName()
            );
        }

        productRepository.save(product);
    }

    private static boolean restockWouldOccurAfterSeasonEnds(
            Product product,
            LocalDate today) {

        return today.plusDays(product.getLeadTime())
                .isAfter(product.getSeasonEndDate());
    }

    private static boolean seasonHasNotStartedYet(
            Product product,
            LocalDate today) {

        return product.getSeasonStartDate().isAfter(today);
    }

}
