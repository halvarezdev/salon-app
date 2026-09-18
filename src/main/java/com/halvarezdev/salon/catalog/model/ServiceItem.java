package com.halvarezdev.salon.catalog.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "catalog_services")
public class ServiceItem extends PanacheEntity {

    @Column(nullable = false, length = 150)
    public String name;

    @Column(length = 500)
    public String description;

    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal price;

    @Column(name = "duration_minutes", nullable = false)
    public Integer durationMinutes;

    @Column(name = "is_active", nullable = false)
    public Boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "category_id")
    public Category category;
}
