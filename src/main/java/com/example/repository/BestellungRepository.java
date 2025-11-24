package com.example.repository;

import com.example.entity.Bestellung;
import com.example.entity.BestellStatus;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Stateless
public class BestellungRepository {
    
    @PersistenceContext
    private EntityManager em;
    
    public Bestellung save(Bestellung bestellung) {
        if (bestellung.getId() == null) {
            em.persist(bestellung);
            return bestellung;
        } else {
            return em.merge(bestellung);
        }
    }
    
    public Optional<Bestellung> findById(Long id) {
        return Optional.ofNullable(em.find(Bestellung.class, id));
    }
    
    public List<Bestellung> findByKundeId(Long kundeId) {
        TypedQuery<Bestellung> query = em.createQuery(
            "SELECT b FROM Bestellung b WHERE b.kunde.id = :kundeId ORDER BY b.bestelldatum DESC", 
            Bestellung.class);
        query.setParameter("kundeId", kundeId);
        return query.getResultList();
    }
    
    public List<Bestellung> findByStatus(BestellStatus status) {
        TypedQuery<Bestellung> query = em.createQuery(
            "SELECT b FROM Bestellung b WHERE b.status = :status", Bestellung.class);
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    public List<Bestellung> findAll() {
        return em.createQuery("SELECT b FROM Bestellung b", Bestellung.class).getResultList();
    }
}
