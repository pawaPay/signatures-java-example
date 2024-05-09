package com.pawapay.lib.http.signature.service;

import static com.pawapay.lib.http.signature.model.ContentDigestAlgorithm.SHA512;
import static com.pawapay.lib.http.signature.model.HttpSignatureAlgorithm.ECDSA_P256_SHA256;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.ALGORITHM_META_PARAM;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.AUTHORITY_COMPONENT;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.CONTENT_DIGEST_HEADER;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.CONTENT_LENGTH_HEADER;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.CONTENT_TYPE_HEADER;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.CREATED_META_PARAM;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.DATE_HEADER;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.EXPIRES_META_PARAM;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.KEY_ID_META_PARAM;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.METHOD_COMPONENT;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.NONCE_META_PARAM;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.PATH_COMPONENT;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.QUERY_COMPONENT;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.QUERY_PARAM_COMPONENT;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.REQUEST_TARGET_COMPONENT;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.SCHEME_COMPONENT;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.STATUS_COMPONENT;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.TAG_META_PARAM;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.TARGET_URI_COMPONENT;
import static java.time.Duration.ofSeconds;
import static java.util.List.of;

import com.pawapay.lib.http.signature.model.ContentDigestAlgorithm;
import com.pawapay.lib.http.signature.model.HttpSignatureAlgorithm;
import java.util.LinkedList;
import java.util.List;

public interface HttpSignatureConfigParams {

    String SIGNATURE_NAME = "sig-pp";
    long EXPIRES_MILLIS = ofSeconds(60L).toMillis();

    List<String> SUPPORTED_COMPONENTS = of(
        METHOD_COMPONENT,
        STATUS_COMPONENT,
        AUTHORITY_COMPONENT,
        SCHEME_COMPONENT,
        TARGET_URI_COMPONENT,
        REQUEST_TARGET_COMPONENT,
        PATH_COMPONENT,
        QUERY_COMPONENT,
        QUERY_PARAM_COMPONENT // should specify query param name explicitly
    );

    List<String> SUPPORTED_META_PARAMS = of(
        ALGORITHM_META_PARAM,
        CREATED_META_PARAM,
        EXPIRES_META_PARAM,
        KEY_ID_META_PARAM,
        NONCE_META_PARAM,
        TAG_META_PARAM
    );

    LinkedList<String> DEFAULT_REQUEST_COMPONENTS = new LinkedList<>(of(
        METHOD_COMPONENT,
        AUTHORITY_COMPONENT,
        PATH_COMPONENT,
        QUERY_COMPONENT,
        DATE_HEADER,
        CONTENT_DIGEST_HEADER,
        CONTENT_TYPE_HEADER,
        CONTENT_LENGTH_HEADER
    ));

    LinkedList<String> DEFAULT_RESPONSE_COMPONENTS = new LinkedList<>(of(
        STATUS_COMPONENT,
        DATE_HEADER,
        CONTENT_DIGEST_HEADER,
        CONTENT_TYPE_HEADER,
        CONTENT_LENGTH_HEADER
    ));

    LinkedList<String> DEFAULT_METADATA = new LinkedList<>(of(
        ALGORITHM_META_PARAM,
        KEY_ID_META_PARAM,
        CREATED_META_PARAM,
        EXPIRES_META_PARAM
    ));


    default String signatureName() {
        return SIGNATURE_NAME;
    }


    default ContentDigestAlgorithm digestAlgorithm() {
        return SHA512;
    }


    default HttpSignatureAlgorithm signatureAlgorithm() {
        return ECDSA_P256_SHA256;
    }


    default LinkedList<String> requestComponents() {
        return DEFAULT_REQUEST_COMPONENTS;
    }


    default LinkedList<String> responseComponents() {
        return DEFAULT_RESPONSE_COMPONENTS;
    }


    default LinkedList<String> metadata() {
        return DEFAULT_METADATA;
    }

    default long expires() {
        return EXPIRES_MILLIS;
    }

}
