package com.halvarezdev.salon.payment.service;

import com.halvarezdev.salon.booking.model.Appointment;
import com.halvarezdev.salon.booking.service.BookingService;
import com.halvarezdev.salon.payment.model.PaymentStatus;
import com.halvarezdev.salon.payment.model.PaymentTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.Optional;

@ApplicationScoped
public class PaymentService {

    private static final Logger LOG = Logger.getLogger(PaymentService.class);

    @Inject
    BookingService bookingService;

    @ConfigProperty(name = "salon.payment.mercadopago.token", defaultValue = "")
    String mpToken;

    @Transactional
    public PaymentTransaction registerIntent(Long appointmentId, String provider) {
        Appointment appointment = Appointment.findById(appointmentId);
        if (appointment == null) {
            throw new IllegalArgumentException("Cita no encontrada para pago");
        }

        PaymentTransaction tx = new PaymentTransaction();
        tx.appointmentId = appointmentId;
        tx.gatewayProvider = provider;
        tx.amount = appointment.service.price;
        tx.currency = "PEN";
        tx.status = PaymentStatus.PENDING;
        tx.persist();

        LOG.infof("Intención de pago registrada para cita #%d por S/ %.2f", appointmentId, tx.amount);
        return tx;
    }

    @Transactional
    public void processWebhook(String provider, String externalPaymentId, String paymentStatusStr, String payload) {
        LOG.infof("Procesando webhook de %s con ID externo: %s y estado: %s", provider, externalPaymentId, paymentStatusStr);

        Optional<PaymentTransaction> existing = PaymentTransaction.find("externalPaymentId", externalPaymentId).firstResultOptional();
        if (existing.isPresent() && existing.get().status == PaymentStatus.APPROVED) {
            LOG.infof("Pago %s ya procesado previamente. Ignorando por idempotencia.", externalPaymentId);
            return;
        }

        PaymentTransaction tx = existing.orElseGet(() -> {
            PaymentTransaction newTx = new PaymentTransaction();
            newTx.gatewayProvider = provider;
            newTx.externalPaymentId = externalPaymentId;
            return newTx;
        });

        tx.rawPayload = payload;
        tx.updatedAt = LocalDateTime.now();

        if ("approved".equalsIgnoreCase(paymentStatusStr) || "paid".equalsIgnoreCase(paymentStatusStr)) {
            tx.status = PaymentStatus.APPROVED;
            if (tx.appointmentId != null) {
                bookingService.confirmAppointment(tx.appointmentId);
                LOG.infof("Cita #%d confirmada con éxito tras pago aprobado.", tx.appointmentId);
            }
        } else if ("rejected".equalsIgnoreCase(paymentStatusStr) || "cancelled".equalsIgnoreCase(paymentStatusStr)) {
            tx.status = PaymentStatus.REJECTED;
        }

        tx.persist();
    }
}
