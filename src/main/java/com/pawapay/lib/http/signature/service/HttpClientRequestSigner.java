package com.pawapay.lib.http.signature.service;

import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;

/* High level signature generator service */
public interface HttpClientRequestSigner {

    /* Enriches http request with signature, content digest and signature input headers */
    void sign(HttpRequest request, EntityDetails entity, HttpContext context) throws Exception;

}
