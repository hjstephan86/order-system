package com.example.repository;

import com.example.entity.Kunde;
import jakarta.inject.Inject;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(ArquillianExtension.class)
public class KundeRepositoryIT {
    
    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
            .addPackages(true, "com.example.entity")
            .addPackages(true, "com.example.repository")
            .addAsResource("META-INF/persistence.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }
    
    @Inject
    private KundeRepository kundeRepository;
    
    @Test
    public void testSaveAndFindKunde() {
        Kunde kunde = new Kunde("Max Mustermann", "max@test.com", "Teststraße 1");
        Kunde saved = kundeRepository.save(kunde);
        
        assertNotNull(saved.getId());
        assertEquals("Max Mustermann", saved.getName());
        assertEquals("max@test.com", saved.getEmail());
    }
    
    @Test
    public void testFindByEmail() {
        Kunde kunde = new Kunde("Anna Schmidt", "anna@test.com", "Beispielweg 2");
        kundeRepository.save(kunde);
        
        Optional<Kunde> found = kundeRepository.findByEmail("anna@test.com");
        
        assertTrue(found.isPresent());
        assertEquals("Anna Schmidt", found.get().getName());
    }
    
    @Test
    public void testFindAll() {
        kundeRepository.save(new Kunde("Kunde 1", "k1@test.com", "Adresse 1"));
        kundeRepository.save(new Kunde("Kunde 2", "k2@test.com", "Adresse 2"));
        
        List<Kunde> kunden = kundeRepository.findAll();
        
        assertTrue(kunden.size() >= 2);
    }
    
    @Test
    public void testUpdate() {
        Kunde kunde = new Kunde("Original Name", "original@test.com", "Original Adresse");
        Kunde saved = kundeRepository.save(kunde);
        
        saved.setName("Updated Name");
        Kunde updated = kundeRepository.save(saved);
        
        assertEquals("Updated Name", updated.getName());
    }
    
    @Test
    public void testDelete() {
        Kunde kunde = new Kunde("Zu Löschen", "delete@test.com", "Delete Street");
        Kunde saved = kundeRepository.save(kunde);
        Long id = saved.getId();
        
        kundeRepository.delete(saved);
        
        Optional<Kunde> found = kundeRepository.findById(id);
        assertTrue(found.isEmpty());
    }
}
