package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class Singleton {

    private static Singleton INSTANCE;
    private final ObjectMapper objectMapper;

    private Singleton() {
        objectMapper = new ObjectMapper();
    }

    public static Singleton getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Singleton();
        }
        return INSTANCE;
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public <T> T readJsonFromFile(String fileName, Class<T> clazz) throws IOException {
        return objectMapper.readValue(new File(fileName), clazz);
    }
}

