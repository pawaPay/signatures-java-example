package com.pawapay.lib.http.signature.service.interceptor;

import static com.pawapay.lib.http.signature.util.LoggingUtils.appendBody;
import static com.pawapay.lib.http.signature.util.LoggingUtils.appendHeaders;

import com.pawapay.lib.http.signature.service.HttpClientRequestSigner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;

@Slf4j
@RequiredArgsConstructor
public class HttpClientRequestSigningInterceptor implements HttpRequestInterceptor {

    private final HttpClientRequestSigner requestSigner;

    @Override
    public void process(HttpRequest request, EntityDetails entity, HttpContext context) {
        try {
            requestSigner.sign(request, entity, context);

            final var loggedMessage = new StringBuilder()
                .append("\nRequest: ")
                .append(request.getMethod()).append(' ')
                .append(request.getUri()).append('\n');

            appendHeaders(request, loggedMessage);
            appendBody(entity, loggedMessage);

            log.info(loggedMessage.toString());
        } catch (Exception exception) {
            log.error("Unable to generate signature. {}", exception.getMessage());
        }
    }

}
