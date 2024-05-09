package com.pawapay.lib.http.signature.util;

import static com.google.common.base.Preconditions.checkState;
import static com.pawapay.lib.http.signature.model.HttpSignatureAlgorithm.RSA_PSS_SHA512;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.ALGORITHM_META_PARAM;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.AUTHORITY_COMPONENT;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.CREATED_META_PARAM;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.EXPIRES_META_PARAM;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.KEY_ID_META_PARAM;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.METHOD_COMPONENT;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.NONCE_META_PARAM;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.NONCE_META_PARAM_VALUE_LENGTH;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.PATH_COMPONENT;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.QUERY_COMPONENT;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.QUERY_PARAM_COMPONENT;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.REQUEST_TARGET_COMPONENT;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.SCHEME_COMPONENT;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.SIGNATURE_PARAMS_COMPONENT;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.STATUS_COMPONENT;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.TAG_META_PARAM;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.TAG_META_PARAM_VALUE;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.TARGET_URI_COMPONENT;
import static com.pawapay.lib.http.signature.util.Sequences.first;
import static com.pawapay.lib.http.signature.util.Sequences.map;
import static com.pawapay.lib.http.signature.util.Sequences.mapToSet;
import static java.lang.String.join;
import static java.util.List.copyOf;
import static java.util.Map.of;

import com.google.common.io.BaseEncoding;
import com.pawapay.lib.http.signature.model.HttpSignatureAlgorithm;
import com.pawapay.lib.http.signature.model.SignatureIdentifier;
import com.pawapay.lib.http.signature.model.SignatureInput;
import com.pawapay.lib.http.signature.model.sfv.SignatureData;
import com.pawapay.lib.http.signature.model.sfv.SignatureItem;
import com.pawapay.lib.http.signature.service.HttpSignatureConfigParams;
import com.pawapay.lib.http.signature.service.supplier.SignatureComponentSupplier;
import jakarta.annotation.Nonnull;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Random;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.greenbytes.http.sfv.ByteSequenceItem;
import org.greenbytes.http.sfv.Dictionary;
import org.greenbytes.http.sfv.InnerList;
import org.greenbytes.http.sfv.IntegerItem;
import org.greenbytes.http.sfv.ListElement;
import org.greenbytes.http.sfv.Parameters;
import org.greenbytes.http.sfv.Parser;
import org.greenbytes.http.sfv.StringItem;

@Slf4j
@UtilityClass
public class SignatureUtils {

    private static final Random RANDOM = new SecureRandom();

    public static byte[] getFromHeader(@Nonnull final String header, @Nonnull final String name) {
        final var signatures = new Parser(header).parseDictionary();
        final var signatureItem = signatures.get().get(name);
        checkState(signatureItem != null, "Signature with provided name {} not found", name);
        return ((ByteSequenceItem) signatureItem).get().array();
    }

    public static String createSignatureHeader(@Nonnull final String name, @Nonnull final byte[] signature) {
        return Dictionary.valueOf(of(name, ByteSequenceItem.valueOf(signature))).serialize();
    }

    public static SignatureInput deserializeInput(@Nonnull final String header) {
        final var components = new LinkedList<StringItem>();
        final var metadata = new LinkedHashMap<String, Object>();

        final var inputDictionary = new Parser(header).parseDictionary();
        final var name = first(inputDictionary.get().keySet()).orElseThrow();
        final var componentItems = (ArrayList<?>) inputDictionary.get().get(name).get();
        final var metadataParams = inputDictionary.get().get(name).getParams();

        for (Object item : componentItems) {
            components.add((StringItem) item);
        }

        for (String key : metadataParams.keySet()) {
            metadata.put(key, metadataParams.get(key).get());
        }

        return new SignatureInput(name, components, metadata);
    }

    public static String serializeInput(@Nonnull final SignatureInput input) {
        return Dictionary.valueOf(
            of(input.name(), InnerList.valueOf(new ArrayList<>(input.components())).withParams(Parameters.valueOf(input.metadata())))
        ).serialize();
    }

    public static String generateRandomString(int length) {
        final byte[] buffer = new byte[length];
        RANDOM.nextBytes(buffer);
        return BaseEncoding.base64Url().omitPadding().encode(buffer);
    }

    public static HttpSignatureAlgorithm getSignatureAlgorithm(@Nonnull SignatureIdentifier identifier) {
        final var algorithmAlias = identifier.input().metadata().get(ALGORITHM_META_PARAM);
        checkState(algorithmAlias != null, "Algorithm (alg) is mandatory");
        return HttpSignatureAlgorithm.byAlias((String) algorithmAlias).orElseThrow(() -> new IllegalArgumentException("Unsupported signature algorithm"));
    }

    public static Signature getSignature(@Nonnull HttpSignatureAlgorithm algorithm)
        throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        final var jcaAlgorithm = switch (algorithm) {
            case ECDSA_P256_SHA256 -> "SHA256withECDSA";
            case ECDSA_P384_SHA384 -> "SHA384withECDSA";
            case RSA_V1_5_SHA256 -> "SHA256withRSA";
            case RSA_PSS_SHA512 -> "SHA512withRSA/PSS";
        };
        final var signerVerifier = Signature.getInstance(jcaAlgorithm, "BC");
        if (RSA_PSS_SHA512.equals(algorithm)) {
            signerVerifier.setParameter(new PSSParameterSpec("SHA-512", "MGF1", MGF1ParameterSpec.SHA512, 64, 1));
        }
        return signerVerifier;
    }

    public static SignatureIdentifier generate(@Nonnull final SignatureInput desiredInput, @Nonnull final SignatureComponentSupplier supplier) {
        final var data = new LinkedHashMap<StringItem, ListElement<?>>();
        for (StringItem component : desiredInput.components()) {
            switch (component.get()) {
                case METHOD_COMPONENT -> addIfPresent(supplier.method(), component, data);
                case STATUS_COMPONENT -> addIfPresent(supplier.status(), component, data);
                case AUTHORITY_COMPONENT -> addIfPresent(supplier.authority(), component, data);
                case SCHEME_COMPONENT -> addIfPresent(supplier.scheme(), component, data);
                case TARGET_URI_COMPONENT -> addIfPresent(supplier.targetUri(), component, data);
                case REQUEST_TARGET_COMPONENT -> addIfPresent(supplier.requestTarget(), component, data);
                case PATH_COMPONENT -> addIfPresent(supplier.path(), component, data);
                case QUERY_COMPONENT -> addIfPresent(supplier.query(), component, data);
                case QUERY_PARAM_COMPONENT -> addIfPresent(supplier.queryParam(getQueryParamName(component)), component, data); // Must be provided with name param
                default -> addIfPresent(supplier.header(component.get()), component, data);
            }
        }
        final var included = new LinkedList<>(data.keySet());
        final var includedInput = new SignatureInput(desiredInput.name(), included, desiredInput.metadata());
        final var params = InnerList.valueOf(copyOf(included)).withParams(Parameters.valueOf(desiredInput.metadata()));
        data.put(StringItem.valueOf(SIGNATURE_PARAMS_COMPONENT), params);
        final var dataToSign = SignatureData.valueOf(data).serialize();
        log.info("\nSignature Base:\n\n" + dataToSign);
        return new SignatureIdentifier(includedInput, serializeInput(includedInput), dataToSign.getBytes());
    }

    public static LinkedHashMap<String, Object> generateMetadata(@Nonnull HttpSignatureConfigParams params, @Nonnull String keyId) {
        final var metadata = new LinkedHashMap<String, Object>();
        final var currentTime = System.currentTimeMillis();
        for (String param : params.metadata()) {
            final var value = switch (param) {
                case ALGORITHM_META_PARAM -> params.signatureAlgorithm().alias();
                case CREATED_META_PARAM -> currentTime;
                case EXPIRES_META_PARAM -> currentTime + params.expires();
                case KEY_ID_META_PARAM -> keyId;
                case NONCE_META_PARAM -> generateRandomString(NONCE_META_PARAM_VALUE_LENGTH);
                case TAG_META_PARAM -> TAG_META_PARAM_VALUE;
                default -> throw new IllegalStateException("Unexpected metadata parameter: " + param);
            };
            metadata.put(param, value);
        }
        return metadata;
    }

    private static void addIfPresent(
        @Nonnull final Optional<?> optional,
        @Nonnull StringItem name,
        @Nonnull final LinkedHashMap<StringItem, ListElement<?>> data
    ) {
        optional.ifPresent(value -> addValue(value, name, data));
    }

    private static void addValue(@Nonnull final Object value, @Nonnull StringItem name, @Nonnull final LinkedHashMap<StringItem, ListElement<?>> data) {
        if (value instanceof String) {
            data.put(name, SignatureItem.valueOf((String) value));
        } else if (value instanceof Integer) {
            data.put(name, IntegerItem.valueOf(((Integer) value).longValue()));
        } else if (value instanceof Long) {
            data.put(name, IntegerItem.valueOf((Long) value));
        } else {
            throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    public static String acceptSignatureHeaderValue() {
        return join(",", mapToSet(HttpSignatureAlgorithm.class, HttpSignatureAlgorithm::alias));
    }

    public static LinkedList<StringItem> toComponentItems(@Nonnull final LinkedList<String> componentNames) {
        return new LinkedList<>(map(componentNames, name ->  StringItem.valueOf(name.toLowerCase())));
    }

    private static String getQueryParamName(@Nonnull final StringItem item) {
        return (String) item.getParams().get("name").get();
    }

}
