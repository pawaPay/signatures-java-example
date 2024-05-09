package com.pawapay.lib.http.signature.service.impl;

import static com.pawapay.lib.http.signature.util.SignatureUtils.getSignature;

import com.pawapay.lib.http.signature.service.HttpSignatureConfigParams;
import com.pawapay.lib.http.signature.service.SignatureGenerator;
import jakarta.annotation.Nonnull;
import java.security.PrivateKey;
import java.security.Signature;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SignatureGeneratorImpl implements SignatureGenerator {

    @Getter
    private final String keyId;
    private final PrivateKey privateKey;
    private final HttpSignatureConfigParams params;

    @Override
    public byte[] sign(@Nonnull byte[] data) {
        try {
            final var algorithm = params.signatureAlgorithm();
            Signature signature = getSignature(algorithm);
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
