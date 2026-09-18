package com.halvarezdev.salon.shared.web;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

@ApplicationScoped
public class SpaRoutingFilter {

    public void init(@Observes Router router) {
        // Intercepta rutas antes del handler 404 por defecto
        router.route().order(10_000).handler(this::handle);
    }

    private void handle(RoutingContext rc) {
        String path = rc.normalizedPath();
        // Si no es llamada a API (/api/...) ni endpoint de Quarkus (/q/...) ni archivo con extensión (.js, .css, .ico, etc.)
        if (!path.startsWith("/api") && !path.startsWith("/q") && !path.contains(".")) {
            rc.reroute("/index.html");
        } else {
            rc.next();
        }
    }
}
