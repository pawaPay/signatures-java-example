package com.pawapay.lib.http.signature.service;

import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;

public interface HttpClientRequestSigner {

    void sign(HttpRequest request, EntityDetails entity, HttpContext context) throws Exception;

}
