package com.pawapay.lib.http.signature.model;

import jakarta.annotation.Nonnull;

/* Represents signature input, signature header value and data to sign */
public record SignatureIdentifier(@Nonnull SignatureInput input, @Nonnull String header, @Nonnull byte[] data) {

}
