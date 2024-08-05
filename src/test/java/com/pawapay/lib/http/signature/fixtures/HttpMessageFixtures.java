package com.pawapay.lib.http.signature.fixtures;

import static com.pawapay.lib.http.signature.fixtures.Configuration.getConfiguration;
import static com.pawapay.lib.http.signature.util.DateUtils.utcDateTimeString;
import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;
import static org.apache.hc.core5.http.HttpHeaders.CONTENT_TYPE;

import com.google.common.io.Resources;
import jakarta.annotation.Nonnull;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.UUID;
import java.util.function.Supplier;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

public class HttpMessageFixtures {

    private static final Supplier<String> DEPOSIT_REQUEST_SUPPLIER = () -> """
        {
            "depositId": "%s",
            "amount": "15",
            "currency": "ZMW",
            "correspondent": "MTN_MOMO_ZMB",
            "payer": {
                "type": "MSISDN",
                "address": {
                    "value": "260763456789"
                }
            },
            "customerTimestamp": "%s",
            "statementDescription": "Signed deposit"
        }""".formatted(UUID.randomUUID().toString(), utcDateTimeString());

    public static ClassicHttpRequest createDepositRequest() {
        return createRequest(DEPOSIT_REQUEST_SUPPLIER, URI.create(getConfiguration().baseUrl()).resolve("./deposits"));
    }

    public static ClassicHttpRequest createPublicKeyRequest() {
        return new HttpGet(URI.create(getConfiguration().baseUrl()).resolve("./public-key/http"));
    }

    public static PrivateKey getRequestSigningKey() throws IOException {
        return decodePrivateKey(Resources.toString(Resources.getResource("private-key.pem"), StandardCharsets.UTF_8));
    }

    private static ClassicHttpRequest createRequest(@Nonnull Supplier<String> dataSupplier, @Nonnull URI uri) {
        final var entity = createRequestEntity(dataSupplier);
        final var request = new HttpPost(uri);
        request.setEntity(entity);
        request.addHeader(AUTHORIZATION, getConfiguration().authToken());
        request.addHeader(CONTENT_TYPE, APPLICATION_JSON);
        return request;
    }

    private static HttpEntity createRequestEntity(@Nonnull Supplier<String> dataSupplier) {
        return new StringEntity(dataSupplier.get(), APPLICATION_JSON);
    }

    private static PrivateKey decodePrivateKey(@Nonnull String pemKey) {
        try {
            var pemKeyPair = (PEMKeyPair) parsePEMString(pemKey);
            return new JcaPEMKeyConverter().getKeyPair(pemKeyPair).getPrivate();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public static PublicKey decodePublicKey(@Nonnull String pemKey) {
        try {
            var keyInfo = (SubjectPublicKeyInfo) parsePEMString(pemKey);
            return new JcaPEMKeyConverter().getPublicKey(keyInfo);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private static Object parsePEMString(@Nonnull final String pem) throws IOException {
        return new PEMParser(new StringReader(pem)).readObject();
    }

}
