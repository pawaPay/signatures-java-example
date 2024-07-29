package com.pawapay.lib.http.signature.service;

import com.pawapay.lib.http.signature.model.SignatureIdentifier;
import com.pawapay.lib.http.signature.model.SignatureVerificationResult;
import jakarta.annotation.Nonnull;

/* Low level signature verification service */
public interface SignatureVerifier {

    /* Verifies passed in binary signature value against signature identifier */
    SignatureVerificationResult verify(@Nonnull SignatureIdentifier identifier, @Nonnull byte[] signature) throws Exception;

}
