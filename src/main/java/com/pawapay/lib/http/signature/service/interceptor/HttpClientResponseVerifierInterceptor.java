package com.pawapay.lib.http.signature.service.interceptor;

import static com.pawapay.lib.http.signature.util.LoggingUtils.appendBody;
import static com.pawapay.lib.http.signature.util.LoggingUtils.appendHeaders;

import com.pawapay.lib.http.signature.service.HttpClientResponseVerifier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpResponseInterceptor;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.protocol.HttpContext;

@Slf4j
@RequiredArgsConstructor
public class HttpClientResponseVerifierInterceptor implements HttpResponseInterceptor {

    private final HttpClientResponseVerifier responseVerifier;

    @Override
    public void process(HttpResponse response, EntityDetails entity, HttpContext context) {
        try {
            final var bodyString = EntityUtils.toString((HttpEntity) entity);

            final var loggedMessage = new StringBuilder()
            .append("\nResponse: ").append(response.getCode()).append('\n');

            appendHeaders(response, loggedMessage);
            appendBody(new StringEntity(bodyString), loggedMessage);

            log.info(loggedMessage.toString());

            final var verificationResult = responseVerifier.verify(response, new StringEntity(bodyString), context);

            log.info("\nSignature verification, valid = %s - %s\n".formatted(verificationResult.valid(), verificationResult.description()));
        } catch (Exception exception) {
            log.error("Exception during signature verification. {}", exception.getMessage());
        }
    }
    
}
