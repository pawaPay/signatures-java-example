package com.pawapay.lib.http.signature.model;

import static com.pawapay.lib.http.signature.util.Sequences.findFirst;

import jakarta.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HttpSignatureAlgorithm {
    ECDSA_P256_SHA256("ecdsa-p256-sha256"),
    ECDSA_P384_SHA384("ecdsa-p384-sha384"),
    RSA_V1_5_SHA256("rsa-v1_5-sha256"),
    RSA_PSS_SHA512("rsa-pss-sha512");

    final String alias;

    public static Optional<HttpSignatureAlgorithm> byAlias(@Nonnull final String alias) {
        return findFirst(EnumSet.allOf(HttpSignatureAlgorithm.class), algorithm -> alias.equals(algorithm.alias));
    }
}
