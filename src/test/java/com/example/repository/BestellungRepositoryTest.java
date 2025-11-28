package com.example.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.example.entity.BestellStatus;
import com.example.entity.Bestellung;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class BestellungRepositoryTest {

    private BestellungRepository repo;
    private EntityManager em;

    @BeforeEach
    public void setUp() throws Exception {
        repo = new BestellungRepository();
        em = Mockito.mock(EntityManager.class);
        java.lang.reflect.Field f = BestellungRepository.class.getDeclaredField("em");
        f.setAccessible(true);
        f.set(repo, em);
    }

    @Test
    public void savePersistsWhenIdNull() {
        Bestellung b = new Bestellung();
        b.setId(null);

        Bestellung saved = repo.save(b);
        assertThat(saved).isSameAs(b);
    }

    @Test
    public void saveMergesWhenIdNotNull() {
        Bestellung b = new Bestellung();
        b.setId(9L);
        Bestellung merged = new Bestellung();
        when(em.merge(b)).thenReturn(merged);

        Bestellung result = repo.save(b);
        assertThat(result).isSameAs(merged);
    }

    @Test
    public void findByIdUsesFind() {
        Bestellung b = new Bestellung();
        when(em.find(Bestellung.class, 3L)).thenReturn(b);
        Optional<Bestellung> opt = repo.findById(3L);
        assertThat(opt).contains(b);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findByKundeIdUsesTypedQuery() {
        TypedQuery<Bestellung> q = (TypedQuery<Bestellung>) Mockito.mock(TypedQuery.class);
        when(em.createQuery(org.mockito.ArgumentMatchers.anyString(), Mockito.eq(Bestellung.class))).thenReturn(q);
        when(q.setParameter(Mockito.eq("kundeId"), Mockito.eq(1L))).thenReturn(q);
        when(q.getResultList()).thenReturn(List.of(new Bestellung()));

        List<Bestellung> res = repo.findByKundeId(1L);
        assertThat(res).hasSize(1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findByStatusUsesTypedQuery() {
        TypedQuery<Bestellung> q = (TypedQuery<Bestellung>) Mockito.mock(TypedQuery.class);
        when(em.createQuery(org.mockito.ArgumentMatchers.anyString(), Mockito.eq(Bestellung.class))).thenReturn(q);
        when(q.setParameter(Mockito.eq("status"), Mockito.eq(BestellStatus.NEU))).thenReturn(q);
        when(q.getResultList()).thenReturn(List.of());

        List<Bestellung> res = repo.findByStatus(BestellStatus.NEU);
        assertThat(res).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findAllReturnsList() {
        TypedQuery<Bestellung> q = (TypedQuery<Bestellung>) Mockito.mock(TypedQuery.class);
        when(em.createQuery(org.mockito.ArgumentMatchers.anyString(), Mockito.eq(Bestellung.class))).thenReturn(q);
        when(q.getResultList()).thenReturn(List.of(new Bestellung(), new Bestellung()));

        List<Bestellung> all = repo.findAll();
        assertThat(all).hasSize(2);
    }
}
