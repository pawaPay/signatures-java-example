package com.pawapay.lib.http.signature.service.impl;

import static com.google.common.base.Preconditions.checkState;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.CONTENT_DIGEST_HEADER;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.SIGNATURE_HEADER;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.SIGNATURE_INPUT_HEADER;
import static com.pawapay.lib.http.signature.model.SignatureVerificationResult.createInvalidResult;
import static com.pawapay.lib.http.signature.util.DigestUtils.verifySerializedDigest;
import static com.pawapay.lib.http.signature.util.HttpUtils.getHeaderValue;
import static com.pawapay.lib.http.signature.util.SignatureUtils.deserializeInput;
import static com.pawapay.lib.http.signature.util.SignatureUtils.generate;
import static com.pawapay.lib.http.signature.util.SignatureUtils.getFromHeader;
import static org.apache.hc.core5.http.io.entity.EntityUtils.toByteArray;

import com.pawapay.lib.http.signature.model.SignatureIdentifier;
import com.pawapay.lib.http.signature.model.SignatureVerificationResult;
import com.pawapay.lib.http.signature.service.HttpClientResponseVerifier;
import com.pawapay.lib.http.signature.service.HttpSignatureConfigParams;
import com.pawapay.lib.http.signature.service.SignatureVerifier;
import com.pawapay.lib.http.signature.service.supplier.SignatureComponentSupplier;
import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.protocol.HttpContext;

@Slf4j
@AllArgsConstructor
public class HttpClientResponseVerifierImpl implements HttpClientResponseVerifier {

    private final HttpSignatureConfigParams parameters;
    private final SignatureVerifier verifier;

    @Override
    public SignatureVerificationResult verify(HttpResponse response, EntityDetails entity, HttpContext context) throws Exception {
        if (entity.getContentLength() > 0) {
            final var digestHeader = response.getHeader(CONTENT_DIGEST_HEADER);
            checkState(digestHeader != null, "Digest header is mandatory if content present");
            final var content = toByteArray((HttpEntity) entity);
            final var wrapped = new ByteArrayEntity(content, ContentType.parse(entity.getContentType()));
            ((BasicClassicHttpResponse) response).setEntity(wrapped);
            final var verificationResult = verifySerializedDigest(digestHeader.getValue(), content);

            if (!verificationResult) {
                return createInvalidResult("Content digest verification failed");
            }
        }

        final var signatureHeader = response.getHeader(SIGNATURE_HEADER);
        checkState(signatureHeader != null, "Signature header is mandatory");
        final var identifier = getSignatureIdentifier(response);
        final var signature = getFromHeader(signatureHeader.getValue(), parameters.signatureName());
        return verifier.verify(identifier, signature);
    }

    private SignatureIdentifier getSignatureIdentifier(@Nonnull final HttpResponse response) {
        final var signatureInput = getHeaderValue(response, SIGNATURE_INPUT_HEADER);
        checkState(signatureInput.isPresent(), "Signature-Input header is mandatory");
        final var input = deserializeInput(signatureInput.get());
        return generate(input, SignatureComponentSupplier.create(response));
    }

}
