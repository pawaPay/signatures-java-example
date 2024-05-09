package com.pawapay.lib.http.signature.util;

import jakarta.annotation.Nonnull;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.apache.hc.core5.http.MessageHeaders;
import org.apache.hc.core5.http.ProtocolException;

@UtilityClass
public class HttpUtils {

    public static Optional<String> getHeaderValue(@Nonnull final MessageHeaders headers, @Nonnull final String name) {
        try {
            final var header = headers.getHeader(name);
            return header != null ? Optional.of(header.getValue()) : Optional.empty();
        } catch (ProtocolException exception) {
            throw new RuntimeException("Duplicate headers detected for %s".formatted(name), exception);
        }
    }

}
