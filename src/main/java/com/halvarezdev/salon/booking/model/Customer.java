package com.halvarezdev.salon.booking.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "booking_customers")
public class Customer extends PanacheEntity {

    @Column(name = "full_name", nullable = false, length = 150)
    public String fullName;

    @Column(nullable = false, length = 150)
    public String email;

    @Column(nullable = false, length = 20)
    public String phone;

    public static Customer findOrCreate(String fullName, String email, String phone) {
        Customer existing = find("email", email).firstResult();
        if (existing != null) {
            existing.fullName = fullName;
            existing.phone = phone;
            return existing;
        }
        Customer customer = new Customer();
        customer.fullName = fullName;
        customer.email = email;
        customer.phone = phone;
        customer.persist();
        return customer;
    }
}
