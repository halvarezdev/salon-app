package com.halvarezdev.salon.catalog.resource;

import com.halvarezdev.salon.catalog.model.Category;
import com.halvarezdev.salon.catalog.model.ServiceItem;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/catalog")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CatalogResource {

    @GET
    @Path("/categories")
    public List<Category> listCategories() {
        return Category.listAll();
    }

    @GET
    @Path("/services")
    public List<ServiceItem> listServices() {
        return ServiceItem.list("isActive", true);
    }

    @POST
    @Path("/categories")
    @Transactional
    public Response createCategory(Category category) {
        category.persist();
        return Response.status(Response.Status.CREATED).entity(category).build();
    }

    @POST
    @Path("/services")
    @Transactional
    public Response createService(ServiceItem service) {
        service.persist();
        return Response.status(Response.Status.CREATED).entity(service).build();
    }
}
