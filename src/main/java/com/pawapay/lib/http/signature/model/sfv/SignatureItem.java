package com.pawapay.lib.http.signature.model.sfv;

import java.util.Objects;
import org.greenbytes.http.sfv.Item;
import org.greenbytes.http.sfv.Parameters;

public class SignatureItem implements Item<String> {
    private final String value;
    private final Parameters params;

    private SignatureItem(String value, Parameters params) {
        this.value = checkParam(Objects.requireNonNull(value, "value must not be null"));
        this.params = Objects.requireNonNull(params, "params must not be null");
    }

    /**
     * Creates a {@link SignatureItem} instance representing the specified
     * {@code String} value.
     *
     * @param value
     *            a {@code String} value.
     * @return a {@link SignatureItem} representing {@code value}.
     */
    public static SignatureItem valueOf(String value) {
        return new SignatureItem(value, Parameters.EMPTY);
    }

    @Override
    public SignatureItem withParams(Parameters params) {
        if (Objects.requireNonNull(params, "params must not be null").isEmpty()) {
            return this;
        } else {
            return new SignatureItem(this.value, params);
        }
    }

    @Override
    public Parameters getParams() {
        return params;
    }

    @Override
    public StringBuilder serializeTo(StringBuilder sb) {
        for (int i = 0; i < value.length(); i++) {
            sb.append(value.charAt(i));
        }
        params.serializeTo(sb);
        return sb;
    }

    @Override
    public String serialize() {
        return serializeTo(new StringBuilder(value.length())).toString();
    }

    @Override
    public String get() {
        return this.value;
    }

    private static String checkParam(String value) {
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c < 0x20 || c >= 0x7f) {
                throw new IllegalArgumentException(
                    String.format("Invalid character in String at position %d: '%c' (0x%04x)", i, c, (int) c));
            }
        }
        return value;
    }

}
