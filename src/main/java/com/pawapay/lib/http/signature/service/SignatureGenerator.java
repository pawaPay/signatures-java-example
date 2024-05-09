package com.pawapay.lib.http.signature.service;

import jakarta.annotation.Nonnull;

public interface SignatureGenerator {

    String keyId();

    byte[] sign(@Nonnull byte[] dataToSign);

}
