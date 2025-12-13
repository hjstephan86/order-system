package com.example.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.example.entity.Produkt;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class ProduktRepositoryTest {

    private ProduktRepository repo;
    private EntityManager em;

    @BeforeEach
    public void setUp() {
        repo = new ProduktRepository();
        em = Mockito.mock(EntityManager.class);
        // inject em via reflection
        try {
            java.lang.reflect.Field f = ProduktRepository.class.getDeclaredField("em");
            f.setAccessible(true);
            f.set(repo, em);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void saveCallsPersistWhenIdNull() {
        Produkt p = new Produkt();
        p.setId(null);

        doNothing().when(em).persist(p);
        Produkt saved = repo.save(p);

        verify(em).persist(p);
        assertThat(saved).isSameAs(p);
    }

    @Test
    public void saveCallsMergeWhenIdNotNull() {
        Produkt p = new Produkt();
        p.setId(5L);

        Produkt merged = new Produkt();
        when(em.merge(p)).thenReturn(merged);

        Produkt result = repo.save(p);

        verify(em).merge(p);
        assertThat(result).isSameAs(merged);
    }

    @Test
    public void findByIdReturnsOptionalFromFind() {
        Produkt p = new Produkt();
        when(em.find(Produkt.class, 1L)).thenReturn(p);

        Optional<Produkt> opt = repo.findById(1L);
        assertThat(opt).contains(p);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findAllUsesQuery() {
        TypedQuery<Produkt> q = (TypedQuery<Produkt>) Mockito.mock(TypedQuery.class);
        when(em.createQuery(org.mockito.ArgumentMatchers.anyString(), Mockito.eq(Produkt.class))).thenReturn(q);
        when(q.getResultList()).thenReturn(List.of(new Produkt(), new Produkt()));

        List<Produkt> all = repo.findAll();
        assertThat(all).hasSize(2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findByNameContainingSetsParameter() {
        TypedQuery<Produkt> q = (TypedQuery<Produkt>) Mockito.mock(TypedQuery.class);
        when(em.createQuery(org.mockito.ArgumentMatchers.anyString(), Mockito.eq(Produkt.class))).thenReturn(q);
        when(q.setParameter(Mockito.eq("name"), Mockito.any())).thenReturn(q);
        when(q.getResultList()).thenReturn(List.of());

        List<Produkt> res = repo.findByNameContaining("x");
        assertThat(res).isEmpty();
        verify(q).setParameter(Mockito.eq("name"), Mockito.eq("%x%"));
    }

    @Test
    public void deleteUsesContainsAndRemove() {
        Produkt p = new Produkt();
        when(em.contains(p)).thenReturn(true);
        doNothing().when(em).remove(p);

        repo.delete(p);
        verify(em).remove(p);
    }

    // Ergänzungen für ProduktRepositoryTest.java
    // Diese Tests fügen Sie der bestehenden Klasse hinzu

    @Test
    public void findByIdReturnsEmptyWhenNotFound() {
        when(em.find(Produkt.class, 999L)).thenReturn(null);

        assertThat(repo.findById(999L)).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findByNameContainingReturnsMatchingProducts() {
        TypedQuery<Produkt> q = (TypedQuery<Produkt>) Mockito.mock(TypedQuery.class);
        when(em.createQuery(org.mockito.ArgumentMatchers.anyString(), Mockito.eq(Produkt.class))).thenReturn(q);
        when(q.setParameter(Mockito.eq("name"), Mockito.eq("%laptop%"))).thenReturn(q);

        Produkt p1 = new Produkt();
        Produkt p2 = new Produkt();
        when(q.getResultList()).thenReturn(List.of(p1, p2));

        List<Produkt> results = repo.findByNameContaining("laptop");

        assertThat(results).hasSize(2);
        assertThat(results).containsExactly(p1, p2);
    }

    @Test
    public void deleteCallsMergeAndRemoveWhenNotContained() {
        Produkt p = new Produkt();
        Produkt merged = new Produkt();

        when(em.contains(p)).thenReturn(false);
        when(em.merge(p)).thenReturn(merged);

        repo.delete(p);

        verify(em).merge(p);
        verify(em).remove(merged);
    }
}
