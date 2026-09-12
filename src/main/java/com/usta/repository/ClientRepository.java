package com.usta.repository;

import com.usta.entity.Client;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ClientRepository implements PanacheRepository<Client> {

    public List<Client> listAllClients() {
        return listAll();
    }

    public Optional<Client> findByIdOptional(Long id) {
        return findById(id) != null ? Optional.of(findById(id)) : Optional.empty();
    }

    public void save(Client client) {
        persist(client);
    }

    public void delete(Client client) {
        deleteById(client.getId());
    }
}