package com.halvarezdev.salon.booking.service;

import com.halvarezdev.salon.booking.model.Appointment;
import com.halvarezdev.salon.booking.model.AppointmentStatus;
import com.halvarezdev.salon.booking.model.Customer;
import com.halvarezdev.salon.catalog.model.ServiceItem;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class BookingService {

    public List<Appointment> listAppointments(LocalDateTime from, LocalDateTime to) {
        return Appointment.list("startTime >= ?1 and endTime <= ?2 order by startTime", from, to);
    }

    public boolean isSlotAvailable(String specialistName, LocalDateTime start, LocalDateTime end) {
        long count = Appointment.count(
            "specialistName = ?1 and status not in (?2, ?3) and ((startTime < ?5 and endTime > ?4))",
            specialistName,
            AppointmentStatus.CANCELLED,
            AppointmentStatus.PENDING_PAYMENT,
            start,
            end
        );
        return count == 0;
    }

    @Transactional
    public Appointment createReservation(
            String customerName,
            String customerEmail,
            String customerPhone,
            Long serviceId,
            String specialistName,
            LocalDateTime startTime,
            String notes) {

        ServiceItem service = ServiceItem.findById(serviceId);
        if (service == null) {
            throw new WebApplicationException("Servicio no encontrado", Response.Status.BAD_REQUEST);
        }

        LocalDateTime endTime = startTime.plusMinutes(service.durationMinutes);

        if (!isSlotAvailable(specialistName, startTime, endTime)) {
            throw new WebApplicationException("El horario seleccionado ya no se encuentra disponible", Response.Status.CONFLICT);
        }

        Customer customer = Customer.findOrCreate(customerName, customerEmail, customerPhone);

        Appointment appointment = new Appointment();
        appointment.customer = customer;
        appointment.service = service;
        appointment.specialistName = specialistName;
        appointment.startTime = startTime;
        appointment.endTime = endTime;
        appointment.status = AppointmentStatus.PENDING_PAYMENT;
        appointment.notes = notes;
        appointment.persist();

        return appointment;
    }

    @Transactional
    public void confirmAppointment(Long appointmentId) {
        Appointment appointment = Appointment.findById(appointmentId);
        if (appointment != null) {
            appointment.status = AppointmentStatus.CONFIRMED;
        }
    }

    @Transactional
    public void attachEvidencePhoto(Long appointmentId, String photoUrl) {
        Appointment appointment = Appointment.findById(appointmentId);
        if (appointment == null) {
            throw new WebApplicationException("Cita no encontrada", Response.Status.NOT_FOUND);
        }
        appointment.evidencePhotoUrl = photoUrl;
    }
}
