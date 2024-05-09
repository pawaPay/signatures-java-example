package com.pawapay.lib.http.signature.util;

import static com.google.common.hash.Hashing.sha256;
import static com.google.common.hash.Hashing.sha512;
import static com.pawapay.lib.http.signature.util.Sequences.filter;
import static com.pawapay.lib.http.signature.util.Sequences.mapToSet;
import static java.lang.String.join;
import static java.util.Map.of;

import com.pawapay.lib.http.signature.model.ContentDigestAlgorithm;
import jakarta.annotation.Nonnull;
import java.util.Arrays;
import lombok.experimental.UtilityClass;
import org.greenbytes.http.sfv.ByteSequenceItem;
import org.greenbytes.http.sfv.Dictionary;
import org.greenbytes.http.sfv.Parser;

@UtilityClass
public class DigestUtils {

    public static String createSerializedDigest(@Nonnull final ContentDigestAlgorithm algorithm, @Nonnull final byte[] content) {
        final var hashItem = ByteSequenceItem.valueOf(calculateDigest(algorithm, content));
        return Dictionary.valueOf(of(algorithm.alias(), hashItem)).serialize();
    }

    public static boolean verifySerializedDigest(@Nonnull final String serializedDigest, @Nonnull final byte[] content) {
        final var dictionary = new Parser(serializedDigest).parseDictionary();
        final var supportedAlgorithmAliases = filter(dictionary.get().keySet(), key -> ContentDigestAlgorithm.byAlias(key).isPresent());
        final var checkedDigestForAliases = filter(supportedAlgorithmAliases,
            alias -> verifyDigest(ContentDigestAlgorithm.requireByAlias(alias), ((ByteSequenceItem) dictionary.get().get(alias)).get().array(), content));
        return !supportedAlgorithmAliases.isEmpty() && supportedAlgorithmAliases.size() == checkedDigestForAliases.size();
    }

    public static String acceptDigestHeaderValue() {
        return join(",", mapToSet(ContentDigestAlgorithm.class, ContentDigestAlgorithm::alias));
    }

    private static boolean verifyDigest(@Nonnull final ContentDigestAlgorithm algorithm, @Nonnull final byte[] digest, @Nonnull final byte[] content) {
        return Arrays.equals(calculateDigest(algorithm, content), digest);
    }

    private static byte[] calculateDigest(@Nonnull final ContentDigestAlgorithm algorithm, @Nonnull final byte[] content) {
        final var hashFunction = switch (algorithm) {
            case SHA256 -> sha256();
            case SHA512 -> sha512();
        };
        return hashFunction.hashBytes(content).asBytes();
    }

}
