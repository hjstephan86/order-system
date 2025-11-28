package com.example.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.example.entity.Kunde;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class KundeRepositoryTest {

    private KundeRepository repo;
    private EntityManager em;

    @BeforeEach
    public void setUp() throws Exception {
        repo = new KundeRepository();
        em = Mockito.mock(EntityManager.class);
        java.lang.reflect.Field f = KundeRepository.class.getDeclaredField("em");
        f.setAccessible(true);
        f.set(repo, em);
    }

    @Test
    public void savePersistsWhenIdNull() {
        Kunde k = new Kunde();
        k.setId(null);

        when(em.merge(k)).thenReturn(k); // merge not called in this path, but safe
        Kunde saved = repo.save(k);

        assertThat(saved).isSameAs(k);
    }

    @Test
    public void findByIdDelegatesToFind() {
        Kunde k = new Kunde();
        when(em.find(Kunde.class, 2L)).thenReturn(k);

        Optional<Kunde> opt = repo.findById(2L);
        assertThat(opt).contains(k);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findByEmailUsesQuery() {
        TypedQuery<Kunde> q = (TypedQuery<Kunde>) Mockito.mock(TypedQuery.class);
        when(em.createQuery(org.mockito.ArgumentMatchers.anyString(), Mockito.eq(Kunde.class))).thenReturn(q);
        when(q.setParameter(Mockito.eq("email"), Mockito.eq("a@b"))).thenReturn(q);
        when(q.getResultList()).thenReturn(List.of(new Kunde()));

        Optional<Kunde> opt = repo.findByEmail("a@b");
        assertThat(opt).isPresent();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findAllReturnsList() {
        TypedQuery<Kunde> q = (TypedQuery<Kunde>) Mockito.mock(TypedQuery.class);
        when(em.createQuery(org.mockito.ArgumentMatchers.anyString(), Mockito.eq(Kunde.class))).thenReturn(q);
        when(q.getResultList()).thenReturn(List.of(new Kunde(), new Kunde()));

        List<Kunde> all = repo.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    public void deleteCallsRemoveWithMergedWhenNotContained() {
        Kunde k = new Kunde();
        when(em.contains(k)).thenReturn(false);
        when(em.merge(k)).thenReturn(k);

        repo.delete(k);
        verify(em).remove(k);
    }
}
