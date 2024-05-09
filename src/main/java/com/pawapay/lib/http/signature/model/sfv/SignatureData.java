package com.pawapay.lib.http.signature.model.sfv;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.greenbytes.http.sfv.ListElement;
import org.greenbytes.http.sfv.StringItem;
import org.greenbytes.http.sfv.Type;

public class SignatureData implements Type<Map<StringItem, ListElement<?>>> {

    private final Map<StringItem, ListElement<?>> value;

    private SignatureData(Map<StringItem, ListElement<?>> value) {
        this.value = Collections.unmodifiableMap(checkKeys(value));
    }

    /**
     * Creates a {@link SignatureData} instance representing the specified {@code Map<StringItem, Item>} value.
     * <p>
     * Note that the {@link Map} implementation that is used here needs to iterate predictably based on insertion order, such as
     * {@link java.util.LinkedHashMap}.
     *
     * @param value a {@code Map<StringItem, Item>} value
     * @return a {@link SignatureData} representing {@code value}.
     */
    public static SignatureData valueOf(Map<StringItem, ListElement<?>> value) {
        return new SignatureData(value);
    }

    @Override
    public Map<StringItem, ListElement<?>> get() {
        return value;
    }

    @Override
    public StringBuilder serializeTo(StringBuilder sb) {
        String separator = "";

        for (Map.Entry<StringItem, ListElement<?>> e : value.entrySet()) {
            sb.append(separator);
            separator = "\n";

            e.getKey().serializeTo(sb);
            ListElement<?> value = e.getValue();

            if (Boolean.TRUE.equals(value.get())) {
                value.getParams().serializeTo(sb);
            } else {
                sb.append(": ");
                value.serializeTo(sb);
            }
        }

        return sb;
    }

    @Override
    public String serialize() {
        return serializeTo(new StringBuilder()).toString();
    }

    private static Map<StringItem, ListElement<?>> checkKeys(Map<StringItem, ListElement<?>> value) {
        for (StringItem item : Objects.requireNonNull(value, "Key item must not be null").keySet()) {
            checkArgument(!item.get().isBlank(),  "Key item value must not be blank");
        }
        return value;
    }

}