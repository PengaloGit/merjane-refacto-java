package com.nimbleways.springboilerplate.services.implementations.ProductProcessor;

import com.nimbleways.springboilerplate.enumeration.ProductType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductProcessorFactory {

    private final NormalProductProcessor normalProcessor;
    private final SeasonalProductProcessor seasonalProcessor;
    private final ExpirableProductProcessor expirableProcessor;

    public ProductProcessor getProcessor(ProductType type) {
        return switch (type) {
            case NORMAL -> normalProcessor;
            case SEASONAL -> seasonalProcessor;
            case EXPIRABLE -> expirableProcessor;
        };
    }
}