package com.pawapay.lib.http.signature.model;

import jakarta.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import org.greenbytes.http.sfv.StringItem;

public record SignatureInput(@Nonnull String name, @Nonnull LinkedList<StringItem> components, @Nonnull LinkedHashMap<String, Object> metadata) {

}
