package com.innovativesoftware.domsommelier_backend.sparkling_wine_tests;

import com.innovativesoftware.domsommelier_backend.product_management.product.repository.SparklingWineRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class SparklingWineRepositoryTest {

    @Autowired
    private SparklingWineRepository sparklingWineRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testFindDistinctCategories() {
        Set<String> categories = sparklingWineRepository.findDistinctSubcategories();
        assertTrue(categories.contains("Шампанское"));
        assertTrue(categories.contains("Кава"));
        assertEquals(4, categories.size());
    }

    @Test
    void testFindDistinctCountryNames() {
        Set<String> countries = sparklingWineRepository.findDistinctCountryNames();
        assertTrue(countries.contains("Франция"));
        assertTrue(countries.contains("Испания"));
        assertEquals(4, countries.size());
    }

    @Test
    void testFindDistinctSugarContents() {
        Set<String> contents = sparklingWineRepository.findDistinctSugarContents();
        assertTrue(contents.contains("Brut"));
        assertTrue(contents.contains("Demi-Sec"));
        assertEquals(4, contents.size());
    }

    @Test
    void testFindDistinctProducers() {
        Set<String> producers = sparklingWineRepository.findDistinctProducers();
        assertTrue(producers.contains("Moet & Chandon"));
        assertTrue(producers.contains("Martini & Rossi"));
        assertEquals(10, producers.size());
    }

    @Test
    void testFindDistinctColors() {
        Set<String> colors = sparklingWineRepository.findDistinctColors();
        assertTrue(colors.contains("Белое"));
        assertTrue(colors.contains("Розовое"));
        assertEquals(2, colors.size());
    }

    @Test
    void testFindDistinctVolumes() {
        Set<String> volumes = sparklingWineRepository.findDistinctVolumes();
        assertTrue(volumes.contains("0.75"));
        assertEquals(2, volumes.size());
    }

    @Test
    void testFindDistinctFeatures() {
        Set<String> features = sparklingWineRepository.findDistinctFeatures();
        assertTrue(features.contains("Традиционный метод"));
        assertTrue(features.contains("Популярно в Испании"));
        assertEquals(11, features.size());
    }
}
