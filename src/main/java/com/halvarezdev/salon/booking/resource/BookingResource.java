package com.halvarezdev.salon.booking.resource;

import com.halvarezdev.salon.booking.model.Appointment;
import com.halvarezdev.salon.booking.service.BookingService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Path("/api/bookings")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookingResource {

    @Inject
    BookingService bookingService;

    public record CreateBookingRequest(
            String customerName,
            String customerEmail,
            String customerPhone,
            Long serviceId,
            String specialistName,
            LocalDateTime startTime,
            String notes
    ) {}

    public record AttachEvidenceRequest(
            String photoUrl
    ) {}

    @GET
    public List<Appointment> getAppointments(
            @QueryParam("date") String dateStr) {
        LocalDate date = (dateStr != null && !dateStr.isBlank()) ? LocalDate.parse(dateStr) : LocalDate.now();
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = date.plusDays(1).atStartOfDay();
        return bookingService.listAppointments(from, to);
    }

    @POST
    public Response createBooking(CreateBookingRequest req) {
        Appointment appointment = bookingService.createReservation(
                req.customerName(),
                req.customerEmail(),
                req.customerPhone(),
                req.serviceId(),
                req.specialistName(),
                req.startTime(),
                req.notes()
        );
        return Response.status(Response.Status.CREATED).entity(appointment).build();
    }

    @PUT
    @Path("/{id}/evidence")
    public Response attachEvidence(@PathParam("id") Long id, AttachEvidenceRequest req) {
        bookingService.attachEvidencePhoto(id, req.photoUrl());
        return Response.ok().build();
    }
}
