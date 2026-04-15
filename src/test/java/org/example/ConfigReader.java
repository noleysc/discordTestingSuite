package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ConfigReader {

    private static final Properties properties = new Properties();

    static {
        // try (...) {} syntax called "try-with-resources"
        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException("config.properties not found in src/test/resources");
            }

            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    private ConfigReader() {
    }

    public static String get(String key) {
        String value = properties.getProperty(key);

        if (value == null || value.isBlank()) {
            throw new RuntimeException("Missing required config value for key: " + key);
        }

        return value;
    }
}