package com.pawapay.lib.http.signature.util;

import static java.time.format.DateTimeFormatter.ISO_INSTANT;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtils {

    public static String utcDateTimeString() {
        return ISO_INSTANT.format(LocalDateTime.now().atOffset(ZoneOffset.UTC));
    }

}
