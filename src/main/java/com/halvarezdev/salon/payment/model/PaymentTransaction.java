package com.halvarezdev.salon.payment.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
public class PaymentTransaction extends PanacheEntity {

    @Column(name = "appointment_id", nullable = false)
    public Long appointmentId;

    @Column(name = "gateway_provider", nullable = false, length = 50)
    public String gatewayProvider; // MERCADO_PAGO, CULQI

    @Column(name = "external_payment_id", unique = true, length = 100)
    public String externalPaymentId;

    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal amount;

    @Column(nullable = false, length = 10)
    public String currency = "PEN";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    public PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    public LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    public LocalDateTime updatedAt;

    @Column(name = "raw_payload", columnDefinition = "TEXT")
    public String rawPayload;
}
