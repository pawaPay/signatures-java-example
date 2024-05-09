package com.pawapay.lib.http.signature.service;

import com.pawapay.lib.http.signature.model.SignatureIdentifier;
import com.pawapay.lib.http.signature.model.SignatureVerificationResult;
import jakarta.annotation.Nonnull;

public interface SignatureVerifier {

    SignatureVerificationResult verify(@Nonnull SignatureIdentifier identifier, @Nonnull byte[] signature) throws Exception;

}
