package com.example.rest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import jakarta.ws.rs.ApplicationPath;

public class RestApplicationTest {

    @Test
    public void applicationPathIsApi() {
        ApplicationPath ann = RestApplication.class.getAnnotation(ApplicationPath.class);
        assertThat(ann).isNotNull();
        assertThat(ann.value()).isEqualTo("/api");
    }
}
