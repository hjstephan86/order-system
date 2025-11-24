package com.example.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.example.entity.Produkt;

import jakarta.inject.Inject;

@ExtendWith(ArquillianExtension.class)
public class ProduktRepositoryIT {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "test.war")
                .addPackages(true, "com.example.entity")
                .addPackages(true, "com.example.repository")
                .addAsResource("META-INF/persistence.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Inject
    private ProduktRepository produktRepository;

    @Test
    public void testSaveProdukt() {
        Produkt produkt = new Produkt("Laptop", "Gaming Laptop", new BigDecimal("999.99"), 5);
        Produkt saved = produktRepository.save(produkt);

        assertNotNull(saved.getId());
        assertEquals(new BigDecimal("999.99"), saved.getPreis());
    }

    @Test
    public void testFindByNameContaining() {
        produktRepository.save(new Produkt("Gaming Laptop", "High-end", new BigDecimal("1500"), 3));
        produktRepository.save(new Produkt("Office Laptop", "Business", new BigDecimal("800"), 10));
        produktRepository.save(new Produkt("Gaming Maus", "RGB", new BigDecimal("50"), 20));

        List<Produkt> results = produktRepository.findByNameContaining("Laptop");

        assertTrue(results.size() >= 2);
        assertTrue(results.stream().allMatch(p -> p.getName().toLowerCase().contains("laptop")));
    }

    @Test
    public void testUpdateLagerbestand() {
        Produkt produkt = new Produkt("Testprodukt", "Test", new BigDecimal("100"), 10);
        Produkt saved = produktRepository.save(produkt);

        saved.setLagerbestand(5);
        Produkt updated = produktRepository.save(saved);

        assertEquals(5, updated.getLagerbestand());
    }
}
