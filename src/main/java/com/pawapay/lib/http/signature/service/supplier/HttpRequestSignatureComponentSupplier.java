package com.pawapay.lib.http.signature.service.supplier;

import static com.pawapay.lib.http.signature.util.HttpUtils.getHeaderValue;

import jakarta.annotation.Nonnull;
import java.net.URISyntaxException;
import java.util.Optional;
import org.apache.hc.core5.http.HttpRequest;

class HttpRequestSignatureComponentSupplier extends RequestSignatureComponentSupplierBase {

    private final HttpRequest request;

    public HttpRequestSignatureComponentSupplier(@Nonnull final HttpRequest request) throws URISyntaxException {
        super(request.getUri());
        this.request = request;
    }

    @Override
    public Optional<String> method() {
        return Optional.of(this.request.getMethod());
    }

    
    @Override
    public Optional<String> header(@Nonnull final String name) {
        return getHeaderValue(this.request, name);
    }

}
