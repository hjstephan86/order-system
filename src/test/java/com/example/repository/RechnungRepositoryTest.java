package com.example.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.example.entity.Rechnung;
import com.example.entity.RechnungsStatus;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class RechnungRepositoryTest {

    private RechnungRepository repo;
    private EntityManager em;

    @BeforeEach
    public void setUp() throws Exception {
        repo = new RechnungRepository();
        em = Mockito.mock(EntityManager.class);
        java.lang.reflect.Field f = RechnungRepository.class.getDeclaredField("em");
        f.setAccessible(true);
        f.set(repo, em);
    }

    @Test
    public void savePersistsWhenIdNull() {
        Rechnung r = new Rechnung();
        r.setId(null);

        Rechnung saved = repo.save(r);
        assertThat(saved).isSameAs(r);
    }

    @Test
    public void saveMergesWhenIdNotNull() {
        Rechnung r = new Rechnung();
        r.setId(11L);
        Rechnung merged = new Rechnung();
        when(em.merge(r)).thenReturn(merged);

        Rechnung res = repo.save(r);
        assertThat(res).isSameAs(merged);
    }

    @Test
    public void findByIdReturnsOptional() {
        Rechnung r = new Rechnung();
        when(em.find(Rechnung.class, 3L)).thenReturn(r);
        assertThat(repo.findById(3L)).contains(r);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findAllReturnsList() {
        TypedQuery<Rechnung> q = (TypedQuery<Rechnung>) Mockito.mock(TypedQuery.class);
        when(em.createQuery(org.mockito.ArgumentMatchers.anyString(), Mockito.eq(Rechnung.class))).thenReturn(q);
        when(q.getResultList()).thenReturn(List.of(new Rechnung(), new Rechnung()));

        List<Rechnung> all = repo.findAll();
        assertThat(all).hasSize(2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findByBestellungIdReturnsNullWhenEmpty() {
        TypedQuery<Rechnung> q = (TypedQuery<Rechnung>) Mockito.mock(TypedQuery.class);
        when(em.createQuery(org.mockito.ArgumentMatchers.anyString(), Mockito.eq(Rechnung.class))).thenReturn(q);
        when(q.setParameter(Mockito.eq("bestellungId"), Mockito.eq(5L))).thenReturn(q);
        when(q.getResultList()).thenReturn(List.of());

        Rechnung r = repo.findByBestellungId(5L);
        assertThat(r).isNull();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findByStatusReturnsList() {
        TypedQuery<Rechnung> q = (TypedQuery<Rechnung>) Mockito.mock(TypedQuery.class);
        when(em.createQuery(org.mockito.ArgumentMatchers.anyString(), Mockito.eq(Rechnung.class))).thenReturn(q);
        when(q.setParameter(Mockito.eq("status"), Mockito.eq(RechnungsStatus.OFFEN))).thenReturn(q);
        when(q.getResultList()).thenReturn(List.of(new Rechnung()));

        List<Rechnung> res = repo.findByStatus(RechnungsStatus.OFFEN);
        assertThat(res).hasSize(1);
    }

    @Test
    public void deleteRemovesMergedWhenNotContained() {
        Rechnung r = new Rechnung();
        when(em.contains(r)).thenReturn(false);
        when(em.merge(r)).thenReturn(r);

        repo.delete(r);
        verify(em).remove(r);
    }
}
