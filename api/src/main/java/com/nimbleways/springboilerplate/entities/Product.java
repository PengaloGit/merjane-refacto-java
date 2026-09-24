package com.nimbleways.springboilerplate.entities;

import com.nimbleways.springboilerplate.enumeration.ProductType;
import lombok.*;

import java.time.LocalDate;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "lead_time")
    private Integer leadTime;

    @Column(name = "available")
    private Integer available;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ProductType type;

    @Column(name = "name")
    private String name;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "season_start_date")
    private LocalDate seasonStartDate;

    @Column(name = "season_end_date")
    private LocalDate seasonEndDate;

    public boolean isAvailable(LocalDate today) {
        return switch (type) {
            case NORMAL -> available > 0;

            case SEASONAL -> available > 0
                    && today.isAfter(seasonStartDate)
                    && today.isBefore(seasonEndDate);

            case EXPIRABLE -> available > 0
                    && expiryDate.isAfter(today);
        };
    }

    public void decreaseAvailability() {
        available--;
    }

    public void markUnavailable() {
        available = 0;
    }
}
