package com.halvarezdev.salon.booking.model;

import com.halvarezdev.salon.catalog.model.ServiceItem;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_appointments")
public class Appointment extends PanacheEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    public Customer customer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    public ServiceItem service;

    @Column(name = "start_time", nullable = false)
    public LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    public LocalDateTime endTime;

    @Column(name = "specialist_name", length = 100)
    public String specialistName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public AppointmentStatus status = AppointmentStatus.PENDING_PAYMENT;

    @Column(length = 1000)
    public String notes;

    @Column(name = "evidence_photo_url", length = 1000)
    public String evidencePhotoUrl;

    @Version
    public Long version;
}
