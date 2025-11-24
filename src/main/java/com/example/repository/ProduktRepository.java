package com.example.repository;

import java.util.List;
import java.util.Optional;

import com.example.entity.Produkt;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class ProduktRepository {

    @PersistenceContext
    private EntityManager em;

    public Produkt save(Produkt produkt) {
        if (produkt.getId() == null) {
            em.persist(produkt);
            return produkt;
        } else {
            return em.merge(produkt);
        }
    }

    public Optional<Produkt> findById(Long id) {
        return Optional.ofNullable(em.find(Produkt.class, id));
    }

    public List<Produkt> findAll() {
        return em.createQuery("SELECT p FROM Produkt p", Produkt.class).getResultList();
    }

    public List<Produkt> findByNameContaining(String name) {
        return em.createQuery(
                "SELECT p FROM Produkt p WHERE LOWER(p.name) LIKE LOWER(:name)", Produkt.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    public void delete(Produkt produkt) {
        em.remove(em.contains(produkt) ? produkt : em.merge(produkt));
    }
}
