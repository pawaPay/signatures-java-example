package com.pawapay.lib.http.signature.model;

import static com.pawapay.lib.http.signature.util.Sequences.findFirst;

import jakarta.annotation.Nonnull;
import java.util.EnumSet;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContentDigestAlgorithm {
    SHA256("sha-256"),
    SHA512("sha-512");

    final String alias;

    public static Optional<ContentDigestAlgorithm> byAlias(@Nonnull final String alias) {
        return findFirst(EnumSet.allOf(ContentDigestAlgorithm.class), algorithm -> alias.equals(algorithm.alias));
    }

    public static ContentDigestAlgorithm requireByAlias(@Nonnull final String alias) {
        return byAlias(alias).orElseThrow();
    }

}
