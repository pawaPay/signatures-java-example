package com.pawapay.lib.http.signature.service;

import jakarta.annotation.Nonnull;

/* Low level signature generator service */
public interface SignatureGenerator {

    String keyId();

    /* Generates binary signature value for passed-in data */
    byte[] sign(@Nonnull byte[] dataToSign);

}
