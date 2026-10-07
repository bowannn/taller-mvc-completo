package com.usta.repository;

import com.usta.entity.Client;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ClientRepository implements PanacheRepository<Client> {
    // La clase queda completamente limpia. 
    // PanacheRepository ya provee de forma nativa y optimizada los métodos 
    // listAll(), findByIdOptional(id), persist() y deleteById().
}
