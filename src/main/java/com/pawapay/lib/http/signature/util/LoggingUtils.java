package com.pawapay.lib.http.signature.util;

import jakarta.annotation.Nonnull;
import java.io.IOException;
import lombok.experimental.UtilityClass;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.MessageHeaders;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

@UtilityClass
public class LoggingUtils {

    public static void appendHeaders(@Nonnull final MessageHeaders headers, @Nonnull final StringBuilder loggedMessage) {
        loggedMessage.append('\n');
        for (final Header header : headers.getHeaders()) {
            loggedMessage.append(header.getName())
                .append(": ")
                .append(header.getValue())
                .append('\n');
        }
    }

    public static void appendBody(@Nonnull final EntityDetails entity, @Nonnull final StringBuilder loggedMessage) throws IOException, ParseException {
        loggedMessage.append('\n').append(EntityUtils.toString((HttpEntity) entity)).append('\n');
    }

}
