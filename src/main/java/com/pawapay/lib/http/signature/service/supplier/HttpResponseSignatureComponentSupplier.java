package com.pawapay.lib.http.signature.service.supplier;

import static com.pawapay.lib.http.signature.util.HttpUtils.getHeaderValue;

import jakarta.annotation.Nonnull;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.apache.hc.core5.http.HttpResponse;

@AllArgsConstructor
class HttpResponseSignatureComponentSupplier implements SignatureComponentSupplier {

    private final HttpResponse response;

    @Override
    public Optional<Integer> status() {
        return Optional.of(this.response.getCode());
    }

    @Override
    public Optional<String> header(@Nonnull String name) {
        return getHeaderValue(this.response, name);
    }

}
