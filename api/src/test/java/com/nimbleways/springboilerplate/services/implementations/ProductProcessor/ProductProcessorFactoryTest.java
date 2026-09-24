package com.nimbleways.springboilerplate.services.implementations.ProductProcessor;

import com.nimbleways.springboilerplate.enumeration.ProductType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class ProductProcessorFactoryTest {

    @Mock
    private NormalProductProcessor normalProcessor;

    @Mock
    private SeasonalProductProcessor seasonalProcessor;

    @Mock
    private ExpirableProductProcessor expirableProcessor;

    @InjectMocks
    private ProductProcessorFactory factory;

    @Test
    void shouldReturnNormalProcessor() {
        assertSame(
                normalProcessor,
                factory.getProcessor(ProductType.NORMAL)
        );
    }

    @Test
    void shouldReturnSeasonalProcessor() {
        assertSame(
                seasonalProcessor,
                factory.getProcessor(ProductType.SEASONAL)
        );
    }

    @Test
    void shouldReturnExpirableProcessor() {
        assertSame(
                expirableProcessor,
                factory.getProcessor(ProductType.EXPIRABLE)
        );
    }
}