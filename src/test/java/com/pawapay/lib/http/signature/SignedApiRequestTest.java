package com.pawapay.lib.http.signature;

import static com.pawapay.lib.http.signature.fixtures.Configuration.getConfiguration;
import static com.pawapay.lib.http.signature.fixtures.HttpMessageFixtures.createDepositRequest;
import static com.pawapay.lib.http.signature.fixtures.HttpMessageFixtures.createPublicKeyRequest;
import static com.pawapay.lib.http.signature.fixtures.HttpMessageFixtures.decodePublicKey;
import static com.pawapay.lib.http.signature.fixtures.HttpMessageFixtures.getRequestSigningKey;
import static com.pawapay.lib.http.signature.util.Sequences.first;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.pawapay.lib.http.signature.service.HttpSignatureConfigParams;
import com.pawapay.lib.http.signature.service.impl.HttpClientRequestSignerImpl;
import com.pawapay.lib.http.signature.service.impl.HttpClientResponseVerifierImpl;
import com.pawapay.lib.http.signature.service.impl.SignatureGeneratorImpl;
import com.pawapay.lib.http.signature.service.impl.SignatureVerifierImpl;
import com.pawapay.lib.http.signature.service.interceptor.HttpClientRequestSigningInterceptor;
import com.pawapay.lib.http.signature.service.interceptor.HttpClientResponseVerifierInterceptor;
import java.io.IOException;
import java.security.PublicKey;
import java.security.Security;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;

public class SignedApiRequestTest {

    private static final ObjectMapper OBJECT_MAPPER = new JsonMapper();

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Test
    public void depositRequest() throws IOException {
        final var parameters = new DefaultHttpSignatureConfigParams();

        final var signatureGenerator = new SignatureGeneratorImpl(getConfiguration().keyId(), getRequestSigningKey(), parameters);
        final var requestSigner = new HttpClientRequestSignerImpl(signatureGenerator, parameters);
        final var requestSigningInterceptor = new HttpClientRequestSigningInterceptor(requestSigner);

        final var signatureVerifier = new SignatureVerifierImpl(getPublicKey());
        final var responseVerifier = new HttpClientResponseVerifierImpl(parameters, signatureVerifier);
        final var responseVerifierInterceptor = new HttpClientResponseVerifierInterceptor(responseVerifier);

        try (
            final var client = HttpClientBuilder.create()
                .addRequestInterceptorFirst(requestSigningInterceptor)
                .addResponseInterceptorFirst(responseVerifierInterceptor)
                .build()
        ) {
            client.execute(createDepositRequest(), response -> null);
        }
    }

    private PublicKey getPublicKey() throws IOException {
        try (final var client = HttpClientBuilder.create().build()) {
            return client.execute(createPublicKeyRequest(), this::toPublicKey);
        }
    }

    private PublicKey toPublicKey(ClassicHttpResponse response) throws IOException, ParseException {
        final var responseBodyString = EntityUtils.toString(response.getEntity(), UTF_8);
        final var keys = OBJECT_MAPPER.readValue(responseBodyString, new TypeReference<List<PublicKeyResponse>>() {
        });
        return decodePublicKey(first(keys).map(PublicKeyResponse::key).orElseThrow());
    }

    static class DefaultHttpSignatureConfigParams implements HttpSignatureConfigParams {

    }

    record PublicKeyResponse(@Nonnull String id, @Nonnull String key) {

    }

}
