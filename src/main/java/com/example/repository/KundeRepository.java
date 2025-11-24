package com.example.repository;

import java.util.List;
import java.util.Optional;

import com.example.entity.Kunde;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
public class KundeRepository {

    @PersistenceContext
    private EntityManager em;

    public Kunde save(Kunde kunde) {
        if (kunde.getId() == null) {
            em.persist(kunde);
            return kunde;
        } else {
            return em.merge(kunde);
        }
    }

    public Optional<Kunde> findById(Long id) {
        return Optional.ofNullable(em.find(Kunde.class, id));
    }

    public Optional<Kunde> findByEmail(String email) {
        TypedQuery<Kunde> query = em.createQuery(
                "SELECT k FROM Kunde k WHERE k.email = :email", Kunde.class);
        query.setParameter("email", email);
        return query.getResultList().stream().findFirst();
    }

    public List<Kunde> findAll() {
        return em.createQuery("SELECT k FROM Kunde k", Kunde.class).getResultList();
    }

    public void delete(Kunde kunde) {
        em.remove(em.contains(kunde) ? kunde : em.merge(kunde));
    }
}
