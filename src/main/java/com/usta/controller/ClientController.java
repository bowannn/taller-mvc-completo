package com.usta.controller;

import com.usta.entity.Client;
import com.usta.entity.Product;
import com.usta.repository.ClientRepository;
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
import java.util.List;

@Path("/clientes")
@Produces(MediaType.TEXT_HTML)
public class ClientController {

    @Inject
    ClientRepository clientRepository;

    @Inject
    ProductRepository productRepository;

    @Location("clientes/index.html")
    Template indexTemplate;

    @Location("clientes/form.html")
    Template formTemplate;

    @Location("clientes/edit.html")
    Template editTemplate;

    @GET
    public TemplateInstance index() {
        return indexTemplate.data("clients", clientRepository.listAllClients());
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
    public Response save(@FormParam("name") String name, @FormParam("email") String email) {
        if (email == null || !email.contains("@")) {
            throw new BadRequestException("Correo inválido");
        }
        Client c = new Client();
        c.setName(name);
        c.setEmail(email);
        clientRepository.save(c);
        return Response.seeOther(URI.create("/clientes")).build();
    }

    @GET
    @Path("/editar/{id}")
    public TemplateInstance edit(@PathParam("id") Long id) {
        Client client = clientRepository.findByIdOptional(id).orElseThrow(NotFoundException::new);
        return editTemplate.data("client", client).data("products", productRepository.listAll());
    }

    @POST
    @Path("/actualizar/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response update(@PathParam("id") Long id,
                           @FormParam("name") String name,
                           @FormParam("email") String email,
                           @FormParam("productIds") List<Long> productIds) {
        Client client = clientRepository.findByIdOptional(id).orElseThrow(NotFoundException::new);

        if (email == null || !email.contains("@")) {
            throw new BadRequestException("Correo inválido");
        }

        client.setName(name);
        client.setEmail(email);

        // Sincronizar colección
        client.getPurchasedProducts().clear();
        if (productIds != null) {
            for (Long pid : productIds) {
                Product p = productRepository.findById(pid);
                if (p != null) client.addProduct(p);
            }
        }
        return Response.seeOther(URI.create("/clientes")).build();
    }

    @POST
    @Path("/eliminar/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        Client client = clientRepository.findByIdOptional(id).orElseThrow(NotFoundException::new);
        clientRepository.delete(client);
        return Response.seeOther(URI.create("/clientes")).build();
    }
}