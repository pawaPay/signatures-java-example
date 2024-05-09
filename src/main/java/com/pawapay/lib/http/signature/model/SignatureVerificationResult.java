package com.pawapay.lib.http.signature.model;

import jakarta.annotation.Nonnull;

public interface SignatureVerificationResult {

    boolean valid();

    String description();

    static SignatureVerificationResult createValidResult() {
        return new SignatureVerificationResultImpl(true, "Http Signature is valid");
    }

    static SignatureVerificationResult createInvalidResult(@Nonnull final String description) {
        return new SignatureVerificationResultImpl(false, description);
    }

    record SignatureVerificationResultImpl( boolean valid, String description) implements SignatureVerificationResult {

    }

}
