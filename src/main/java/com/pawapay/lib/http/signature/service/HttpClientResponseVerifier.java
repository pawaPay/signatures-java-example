package com.pawapay.lib.http.signature.service;

import com.pawapay.lib.http.signature.model.SignatureVerificationResult;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;

public interface HttpClientResponseVerifier {

    SignatureVerificationResult verify(HttpResponse response, EntityDetails entity, HttpContext context) throws Exception;

}
