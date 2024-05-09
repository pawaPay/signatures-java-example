package com.pawapay.lib.http.signature.service.supplier;

import jakarta.annotation.Nonnull;
import java.net.URISyntaxException;
import java.util.Optional;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;

public interface SignatureComponentSupplier {

    default Optional<String> method() {
        return Optional.empty();
    }

    default Optional<Integer> status() {
        return Optional.empty();
    }

    default Optional<String> authority() {
        return Optional.empty();
    }

    default Optional<String> scheme() {
        return Optional.empty();
    }

    default Optional<String> targetUri() {
        return Optional.empty();
    }

    default Optional<String> requestTarget() {
        return Optional.empty();
    }

    default Optional<String> path() {
        return Optional.empty();
    }

    default Optional<String> query() {
        return Optional.empty();
    }

    default Optional<String> queryParam(@Nonnull String name) {
        return Optional.empty();
    }

    default Optional<String> header(@Nonnull String name) {
        return Optional.empty();
    }

    static SignatureComponentSupplier create(@Nonnull final HttpRequest request) throws URISyntaxException {
        return new HttpRequestSignatureComponentSupplier(request);
    }

    static SignatureComponentSupplier create(@Nonnull final HttpResponse response) {
        return new HttpResponseSignatureComponentSupplier(response);
    }

}
