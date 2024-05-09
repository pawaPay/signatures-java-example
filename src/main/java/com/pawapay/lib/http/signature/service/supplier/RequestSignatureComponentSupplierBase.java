package com.pawapay.lib.http.signature.service.supplier;

import static com.pawapay.lib.http.signature.util.Sequences.findFirst;

import jakarta.annotation.Nonnull;
import java.net.URI;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URIBuilder;

@AllArgsConstructor
abstract class RequestSignatureComponentSupplierBase implements SignatureComponentSupplier {

    private URI uri;

    @Override
    public Optional<String> authority() {
        return Optional.ofNullable(this.uri.getRawAuthority());
    }

    @Override
    public Optional<String> scheme() {
        return Optional.ofNullable(this.uri.getScheme());
    }

    @Override
    public Optional<String> targetUri() {
        return Optional.ofNullable(this.uri.toString());
    }

    @Override
    public Optional<String> requestTarget() {
        return Optional.ofNullable(this.uri.toString());
    }
    @Override
    public Optional<String> path() {
        return Optional.ofNullable(this.uri.getRawPath());
    }

    @Override
    public Optional<String> query() {
        return Optional.ofNullable(this.uri.getRawQuery());
    }

    @Override
    public Optional<String> queryParam(@Nonnull final String name) {
        return findFirst(new URIBuilder(this.uri).getQueryParams(), param -> param.getName().equals(name))
            .map(NameValuePair::getValue);
    }

}
