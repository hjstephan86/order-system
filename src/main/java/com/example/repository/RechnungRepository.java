package com.example.repository;

import java.util.List;
import java.util.Optional;

import com.example.entity.Rechnung;
import com.example.entity.RechnungsStatus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class RechnungRepository {

    @PersistenceContext
    private EntityManager em;

    public Rechnung save(Rechnung rechnung) {
        if (rechnung.getId() == null) {
            em.persist(rechnung);
            return rechnung;
        } else {
            return em.merge(rechnung);
        }
    }

    public Optional<Rechnung> findById(Long id) {
        Rechnung rechnung = em.find(Rechnung.class, id);
        return Optional.ofNullable(rechnung);
    }

    public List<Rechnung> findAll() {
        TypedQuery<Rechnung> query = em.createQuery(
                "SELECT r FROM Rechnung r ORDER BY r.erstellungsdatum DESC",
                Rechnung.class);
        return query.getResultList();
    }

    public Rechnung findByBestellungId(Long bestellungId) {
        TypedQuery<Rechnung> query = em.createQuery(
                "SELECT r FROM Rechnung r WHERE r.bestellung.id = :bestellungId",
                Rechnung.class);
        query.setParameter("bestellungId", bestellungId);
        List<Rechnung> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    public List<Rechnung> findByStatus(RechnungsStatus status) {
        TypedQuery<Rechnung> query = em.createQuery(
                "SELECT r FROM Rechnung r WHERE r.status = :status ORDER BY r.erstellungsdatum DESC",
                Rechnung.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    public void delete(Rechnung rechnung) {
        if (em.contains(rechnung)) {
            em.remove(rechnung);
        } else {
            em.remove(em.merge(rechnung));
        }
    }
}
