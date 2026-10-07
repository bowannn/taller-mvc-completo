package com.usta.controller;

import java.net.URI;
import com.usta.entity.Client;
import com.usta.entity.Product;
import com.usta.repository.ClientRepository;
import com.usta.repository.ProductRepository;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/products")
@Produces(MediaType.TEXT_HTML)
public class ProductController {

    @Inject
    ProductRepository repository;

    // Inyectamos el repositorio de clientes para poder desvincular los productos
    @Inject
    ClientRepository clientRepository;

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
        validateStock(stock);
        
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setStock(stock);
        repository.persist(p);
        return Response.seeOther(URI.create("/products")).build();
    }

    // Nuevo método centralizado para evitar duplicación
    private void validateStock(Integer stock) {
        if (stock == null || stock < 0) {
            throw new BadRequestException("Stock must be an integer greater than or equal to 0");
        }
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
        Product p = repository.findById(id);
        if (p != null) {
            // 1. Recorremos todos los clientes y les quitamos este producto de su lista
            for (Client client : clientRepository.listAll()) {
                client.getPurchasedProducts().removeIf(prod -> prod.getId().equals(id));
            }

            // 2. Ahora que ningún cliente depende de él, podemos borrarlo de forma segura
            repository.delete(p);
        }
        return Response.seeOther(URI.create("/products")).build();
    }
}