package com.pawapay.lib.http.signature.model;

import jakarta.annotation.Nonnull;

public record SignatureIdentifier(@Nonnull SignatureInput input, @Nonnull String header, @Nonnull byte[] data) {

}
