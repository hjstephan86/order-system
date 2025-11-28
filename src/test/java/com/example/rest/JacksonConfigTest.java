package com.example.rest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonConfigTest {

    @Test
    public void getContextReturnsObjectMapperWithJavaTimeModule() throws Exception {
        JacksonConfig cfg = new JacksonConfig();
        ObjectMapper mapper = cfg.getContext(Object.class);
        assertThat(mapper).isNotNull();
        // should be able to serialize java.time types
        String serialized = mapper.writeValueAsString(java.time.LocalDateTime.now());
        assertThat(serialized).isNotNull();
    }
}
