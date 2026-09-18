package com.halvarezdev.salon.catalog.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "catalog_categories")
public class Category extends PanacheEntity {

    @Column(nullable = false, unique = true, length = 100)
    public String name;

    @Column(length = 255)
    public String description;

    @Column(name = "icon_url", length = 500)
    public String iconUrl;
}
