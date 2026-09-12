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

@Path("/products")
@Produces(MediaType.TEXT_HTML)
public class ProductController {

    @Inject
    ProductRepository repository;

    @Location("products/index.html")
    Template indexTemplate;

    @Location("products/form.html")
    Template formTemplate;

    @Location("products/edit.html")
    Template editTemplate;

    @GET
    public TemplateInstance index() {
        return indexTemplate.data("products", repository.listAll());
    }

    @GET
    @Path("/new")
    public TemplateInstance form() {
        return formTemplate.instance();
    }

    @POST
    @Path("/save")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response save(@FormParam("name") String name,
                         @FormParam("price") Double price,
                         @FormParam("stock") Integer stock) {
        if (stock == null || stock < 0) {
            throw new BadRequestException("Stock must be an integer greater than or equal to 0");
        }
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setStock(stock);
        repository.persist(p);
        return Response.seeOther(URI.create("/products")).build();
    }

    @GET
    @Path("/edit/{id}")
    public TemplateInstance edit(@PathParam("id") Long id) {
        return editTemplate.data("product", repository.findById(id));
    }

    @POST
    @Path("/update/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response update(@PathParam("id") Long id,
                           @FormParam("name") String name,
                           @FormParam("price") Double price,
                           @FormParam("stock") Integer stock) {
        if (stock == null || stock < 0) {
            throw new BadRequestException("Stock must be an integer greater than or equal to 0");
        }
        Product p = repository.findById(id);
        p.setName(name);
        p.setPrice(price);
        p.setStock(stock);
        return Response.seeOther(URI.create("/products")).build();
    }

    @POST
    @Path("/delete/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        repository.deleteById(id);
        return Response.seeOther(URI.create("/products")).build();
    }
}