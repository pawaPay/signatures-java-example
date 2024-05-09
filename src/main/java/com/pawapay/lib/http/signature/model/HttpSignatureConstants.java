package com.pawapay.lib.http.signature.model;

public class HttpSignatureConstants {

    public static final String SIGNATURE_PARAMS_COMPONENT = "@signature-params"; // signature

    public static final String METHOD_COMPONENT = "@method"; // http request
    public static final String STATUS_COMPONENT = "@status"; // http response

    public static final String AUTHORITY_COMPONENT = "@authority"; // uri
    public static final String SCHEME_COMPONENT = "@scheme"; // uri
    public static final String TARGET_URI_COMPONENT = "@target-uri"; // uri
    public static final String REQUEST_TARGET_COMPONENT = "@request-target"; // uri
    public static final String PATH_COMPONENT = "@path"; // uri
    public static final String QUERY_COMPONENT = "@query"; // uri
    public static final String QUERY_PARAM_COMPONENT = "@query-param"; // uri - not recommended due to possible duplicate param names

    public static final String SIGNATURE_HEADER = "Signature";
    public static final String SIGNATURE_INPUT_HEADER = "Signature-Input";
    public static final String ACCEPT_SIGNATURE_HEADER = "Accept-Signature";
    public static final String CONTENT_DIGEST_HEADER = "Content-Digest";
    public static final String ACCEPT_DIGEST_HEADER = "Accept-Digest";

    public static final String DATE_HEADER = "Signature-Date";
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String CONTENT_LENGTH_HEADER = "Content-Length";

    public static final String ALGORITHM_META_PARAM = "alg"; // metadata
    public static final String CREATED_META_PARAM = "created"; // metadata
    public static final String EXPIRES_META_PARAM = "expires"; // metadata
    public static final String KEY_ID_META_PARAM = "keyid"; // metadata
    public static final String NONCE_META_PARAM = "nonce"; // metadata
    public static final String TAG_META_PARAM = "tag"; // metadata

    public static final String TAG_META_PARAM_VALUE = "pawaPay";
    public static final int NONCE_META_PARAM_VALUE_LENGTH = 16; // metadata

}
