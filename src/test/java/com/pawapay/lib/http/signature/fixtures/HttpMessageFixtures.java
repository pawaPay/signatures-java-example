package com.pawapay.lib.http.signature.fixtures;

import static com.pawapay.lib.http.signature.util.DateUtils.utcDateTimeString;
import static org.apache.hc.core5.http.ContentType.APPLICATION_JSON;
import static org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;
import static org.apache.hc.core5.http.HttpHeaders.CONTENT_TYPE;

import jakarta.annotation.Nonnull;
import java.net.URI;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;
import java.util.function.Supplier;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;

public class HttpMessageFixtures {

    public static final String CUSTOMER_TEST_KEY_ID = "CUSTOMER_TEST_KEY_ID";

    // Sample private key. !!! Do not use in production !!!
    private static final String REQUEST_PRIVATE_KEY = """
        -----BEGIN PRIVATE KEY-----
        MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQghZKTDcEWYulJXOUv
        qycUvPNISbJMdqispQnMW/xrs5qgCgYIKoZIzj0DAQehRANCAARg8EwlC7ecD124
        CoatnOL5g3idSL+/k9FCRXbSJwuMIiD6G+kgIP6SNfkGIvTFMr36T8qsQeJ8YiiL
        HT11yNRq
        -----END PRIVATE KEY-----""";

    // Should be added in customer panel with value of 'CUSTOMER_TEST_KEY_ID' constant
    private static final String REQUEST_PUBLIC_KEY = """
        -----BEGIN PUBLIC KEY-----
        MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEYPBMJQu3nA9duAqGrZzi+YN4nUi/
        v5PRQkV20icLjCIg+hvpICD+kjX5BiL0xTK9+k/KrEHifGIoix09dcjUag==
        -----END PUBLIC KEY-----""";

    private static final String AUTHORIZATION_TOKEN = "Bearer !!! Your API token here !!!";

    public static final String BASE_URL = "https://api.sandbox.pawapay.cloud";

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
        return createRequest(DEPOSIT_REQUEST_SUPPLIER, URI.create(BASE_URL).resolve("./deposits"));
    }

    public static ClassicHttpRequest createPublicKeyRequest() {
        return new HttpGet(URI.create(BASE_URL).resolve("./public-key/http"));
    }

    public static PrivateKey getRequestSigningKey(){
        return decodePrivateKey(REQUEST_PRIVATE_KEY);
    }

    private static ClassicHttpRequest createRequest(@Nonnull Supplier<String> dataSupplier, @Nonnull URI uri) {
        final var entity = createRequestEntity(dataSupplier);
        final var request = new HttpPost(uri);
        request.setEntity(entity);
        request.addHeader(AUTHORIZATION, AUTHORIZATION_TOKEN);
        request.addHeader(CONTENT_TYPE, APPLICATION_JSON);
        return request;
    }

    private static HttpEntity createRequestEntity(@Nonnull Supplier<String> dataSupplier) {
        return new StringEntity(dataSupplier.get(), APPLICATION_JSON);
    }

    private static PrivateKey decodePrivateKey(@Nonnull String pemKey) {
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(removeHeaderAndFooter(pemKey, "PRIVATE KEY"));
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public static PublicKey decodePublicKey(@Nonnull String pemKey) {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(removeHeaderAndFooter(pemKey, "PUBLIC KEY"));
            return KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(publicKeyBytes));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private static String removeHeaderAndFooter(@javax.annotation.Nonnull String pemKey, @javax.annotation.Nonnull String type) {
        return pemKey
            .replace("-----BEGIN %s-----".formatted(type), "")
            .replaceAll("\\r\\n|\\r|\\n", "")
            .replace("-----END %s-----".formatted(type), "");
    }

}
