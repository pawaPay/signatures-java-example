package com.pawapay.lib.http.signature.service.impl;

import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.EXPIRES_META_PARAM;
import static com.pawapay.lib.http.signature.model.SignatureVerificationResult.createInvalidResult;
import static com.pawapay.lib.http.signature.model.SignatureVerificationResult.createValidResult;
import static com.pawapay.lib.http.signature.util.SignatureUtils.getSignature;
import static com.pawapay.lib.http.signature.util.SignatureUtils.getSignatureAlgorithm;

import com.pawapay.lib.http.signature.model.SignatureIdentifier;
import com.pawapay.lib.http.signature.model.SignatureVerificationResult;
import com.pawapay.lib.http.signature.service.SignatureVerifier;
import jakarta.annotation.Nonnull;
import java.security.PublicKey;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class SignatureVerifierImpl implements SignatureVerifier {

    private PublicKey publicKey;

    @Override
    public SignatureVerificationResult verify(@Nonnull SignatureIdentifier identifier, @Nonnull byte[] signature) throws Exception {
        if (isExpired(identifier)) {
            return createInvalidResult("Http Signature is expired");
        }

        final var signerVerifier = getSignature(getSignatureAlgorithm(identifier));
        signerVerifier.initVerify(publicKey);
        signerVerifier.update(identifier.data());
        return signerVerifier.verify(signature) ? createValidResult() : createInvalidResult("Http Signature is invalid");
    }

    private boolean isExpired(@Nonnull final SignatureIdentifier identifier) {
        final var expires = (long) identifier.input().metadata().get(EXPIRES_META_PARAM);
        return System.currentTimeMillis() > expires;
    }

}
