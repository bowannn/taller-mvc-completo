package com.usta.controller;

import com.usta.entity.Product;
import com.usta.repository.ProductRepository;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("/productos")
@Produces(MediaType.TEXT_HTML)
public class ProductController {

    @Inject
    ProductRepository repository;

    @Location("productos/index.html")
    Template indexTemplate;

    @Location("productos/form.html")
    Template formTemplate;

    @Location("productos/edit.html")
    Template editTemplate;

    @GET
    public TemplateInstance index() {
        return indexTemplate.data("products", repository.listAll());
    }

    @GET
    @Path("/nuevo")
    public TemplateInstance form() {
        return formTemplate.instance();
    }

    @POST
    @Path("/guardar")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response save(@FormParam("name") String name,
                         @FormParam("price") Double price,
                         @FormParam("stock") Integer stock) {
        if (stock == null || stock < 0) {
            throw new BadRequestException("El stock debe ser un entero mayor o igual a 0");
        }
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setStock(stock);
        repository.persist(p);
        return Response.seeOther(URI.create("/productos")).build();
    }

    @GET
    @Path("/editar/{id}")
    public TemplateInstance edit(@PathParam("id") Long id) {
        return editTemplate.data("product", repository.findById(id));
    }

    @POST
    @Path("/actualizar/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response update(@PathParam("id") Long id,
                           @FormParam("name") String name,
                           @FormParam("price") Double price,
                           @FormParam("stock") Integer stock) {
        if (stock == null || stock < 0) {
            throw new BadRequestException("El stock debe ser un entero mayor o igual a 0");
        }
        Product p = repository.findById(id);
        p.setName(name);
        p.setPrice(price);
        p.setStock(stock);
        return Response.seeOther(URI.create("/productos")).build();
    }

    @POST
    @Path("/eliminar/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        repository.deleteById(id);
        return Response.seeOther(URI.create("/productos")).build();
    }
}