package com.pawapay.lib.http.signature.service.impl;

import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.ACCEPT_DIGEST_HEADER;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.ACCEPT_SIGNATURE_HEADER;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.CONTENT_DIGEST_HEADER;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.DATE_HEADER;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.SIGNATURE_HEADER;
import static com.pawapay.lib.http.signature.model.HttpSignatureConstants.SIGNATURE_INPUT_HEADER;
import static com.pawapay.lib.http.signature.util.DateUtils.utcDateTimeString;
import static com.pawapay.lib.http.signature.util.DigestUtils.acceptDigestHeaderValue;
import static com.pawapay.lib.http.signature.util.DigestUtils.createSerializedDigest;
import static com.pawapay.lib.http.signature.util.SignatureUtils.acceptSignatureHeaderValue;
import static com.pawapay.lib.http.signature.util.SignatureUtils.createSignatureHeader;
import static com.pawapay.lib.http.signature.util.SignatureUtils.generate;
import static com.pawapay.lib.http.signature.util.SignatureUtils.generateMetadata;
import static com.pawapay.lib.http.signature.util.SignatureUtils.toComponentItems;
import static org.apache.hc.core5.http.io.entity.EntityUtils.toByteArray;

import com.pawapay.lib.http.signature.model.SignatureIdentifier;
import com.pawapay.lib.http.signature.model.SignatureInput;
import com.pawapay.lib.http.signature.service.HttpClientRequestSigner;
import com.pawapay.lib.http.signature.service.HttpSignatureConfigParams;
import com.pawapay.lib.http.signature.service.SignatureGenerator;
import com.pawapay.lib.http.signature.service.supplier.SignatureComponentSupplier;
import jakarta.annotation.Nonnull;
import java.io.IOException;
import java.net.URISyntaxException;
import lombok.AllArgsConstructor;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.protocol.HttpContext;

@AllArgsConstructor
public class HttpClientRequestSignerImpl implements HttpClientRequestSigner {

    private final SignatureGenerator generator;
    private final HttpSignatureConfigParams parameters;

    @Override
    public void sign(HttpRequest request, EntityDetails entity, HttpContext context) throws IOException, URISyntaxException {
        final var content = toByteArray((HttpEntity) entity);
        request.addHeader(CONTENT_DIGEST_HEADER, createSerializedDigest(parameters.digestAlgorithm(), content));
        request.addHeader(DATE_HEADER, utcDateTimeString());

        final var identifier = getSignatureIdentifier(request);
        final var signature = generator.sign(identifier.data());

        request.addHeader(SIGNATURE_HEADER, createSignatureHeader(parameters.signatureName(), signature));
        request.addHeader(SIGNATURE_INPUT_HEADER, identifier.header());
        request.addHeader(ACCEPT_SIGNATURE_HEADER, acceptSignatureHeaderValue());
        request.addHeader(ACCEPT_DIGEST_HEADER, acceptDigestHeaderValue());
    }

    private SignatureIdentifier getSignatureIdentifier(@Nonnull final HttpRequest request) throws URISyntaxException {
        final var metadata = generateMetadata(parameters, generator.keyId());
        final var input = new SignatureInput(parameters.signatureName(), toComponentItems(parameters.requestComponents()), metadata);
        return generate(input, SignatureComponentSupplier.create(request));
    }

}
