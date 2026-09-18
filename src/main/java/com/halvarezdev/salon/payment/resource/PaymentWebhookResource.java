package com.halvarezdev.salon.payment.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.halvarezdev.salon.payment.model.PaymentTransaction;
import com.halvarezdev.salon.payment.service.PaymentService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("/api/payments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PaymentWebhookResource {

    private static final Logger LOG = Logger.getLogger(PaymentWebhookResource.class);

    @Inject
    PaymentService paymentService;

    public record CreateIntentRequest(Long appointmentId, String provider) {}

    @POST
    @Path("/intent")
    public Response createIntent(CreateIntentRequest req) {
        String provider = (req.provider() != null && !req.provider().isBlank()) ? req.provider() : "MERCADO_PAGO";
        PaymentTransaction tx = paymentService.registerIntent(req.appointmentId(), provider);
        return Response.status(Response.Status.CREATED).entity(tx).build();
    }

    @POST
    @Path("/webhook/mercadopago")
    public Response handleMercadoPagoWebhook(JsonNode payload) {
        LOG.infof("Mercado Pago Webhook payload: %s", payload.toString());
        String type = payload.path("type").asText();
        if ("payment".equalsIgnoreCase(type)) {
            String paymentId = payload.path("data").path("id").asText();
            // Nota: Aquí se consulta el estado del pago con Mercado Pago SDK o REST client
            // Simulamos o registramos la recepción
            paymentService.processWebhook("MERCADO_PAGO", paymentId, "approved", payload.toString());
        }
        return Response.ok().build();
    }

    @POST
    @Path("/webhook/culqi")
    public Response handleCulqiWebhook(JsonNode payload) {
        LOG.infof("Culqi Webhook payload: %s", payload.toString());
        // Formato estándar Culqi webhook event
        String eventType = payload.path("type").asText();
        String externalId = payload.path("data").path("id").asText();
        if ("charge.creation.succeeded".equalsIgnoreCase(eventType) || "order.status.changed".equalsIgnoreCase(eventType)) {
            paymentService.processWebhook("CULQI", externalId, "approved", payload.toString());
        }
        return Response.ok().build();
    }
}
