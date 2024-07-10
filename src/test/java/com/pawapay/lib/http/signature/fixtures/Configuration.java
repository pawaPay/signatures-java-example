package com.pawapay.lib.http.signature.fixtures;

import static java.lang.Thread.currentThread;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import javax.annotation.Nonnull;

public record Configuration(@Nonnull String baseUrl, @Nonnull String authToken, @Nonnull String keyId) {

    private static class Holder {

        private static final Configuration INSTANCE = readFromConfigYml();
    }

    public static Configuration getConfiguration() {
        return Holder.INSTANCE;
    }

    private static Configuration readFromConfigYml() {
        try (final var resourceStream = currentThread().getContextClassLoader().getResourceAsStream("config.yml")) {
            final var objectMapper = new ObjectMapper(new YAMLFactory());
            return objectMapper.readValue(resourceStream, Configuration.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

}
