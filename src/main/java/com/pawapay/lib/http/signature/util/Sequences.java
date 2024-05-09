package com.pawapay.lib.http.signature.util;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Sequences {

    public static <E> boolean isEmpty(@Nullable final Collection<E> collection) {
        return collection == null || collection.isEmpty();
    }

    @Nonnull
    public static <E> Set<E> filter(@Nullable final Set<E> set, @Nonnull final Predicate<? super E> filter) {
        return filterToSet(set, filter);
    }


    @Nonnull
    public static <E> Iterable<E> filter(@Nullable final Iterable<E> iterable, @Nonnull final Predicate<? super E> filter) {
        return filterToStream(iterable, filter)::iterator;
    }

    @Nonnull
    public static <E> Set<E> filterToSet(@Nullable final Iterable<E> sequence, @Nonnull final Predicate<? super E> filter) {
        return filterToStream(sequence, filter).collect(toSet());
    }

    @Nonnull
    public static <E, R> Set<R> mapToSet(@Nullable final Iterable<E> sequence, @Nonnull final Function<? super E, ? extends R> mapper) {
        return mapToStream(sequence, mapper).collect(toSet());
    }

    @Nonnull
    public static <E, R> List<R> map(@Nullable final List<E> list, @Nonnull final Function<? super E, ? extends R> mapper) {
        return mapToList(list, mapper);
    }

    @Nonnull
    public static <E, R> Set<R> map(@Nullable final Set<E> set, @Nonnull final Function<? super E, ? extends R> mapper) {
        return mapToSet(set, mapper);
    }

    @Nonnull
    public static <E, R> List<R> mapToList(@Nullable final Iterable<E> sequence, @Nonnull final Function<? super E, ? extends R> mapper) {
        return mapToStream(sequence, mapper).collect(toList());
    }

    @Nonnull
    public static <E, R> Stream<R> mapToStream(@Nullable final Iterable<E> sequence, @Nonnull final Function<? super E, ? extends R> mapper) {
        return stream(sequence).map(mapper);
    }

    @Nonnull
    public static <E extends Enum<E>, R> Set<R> mapToSet(@Nonnull final Class<E> enumClass, @Nonnull final Function<? super E, ? extends R> mapper) {
        return map(EnumSet.allOf(enumClass), mapper);
    }

    @Nonnull
    public static <E, R> Set<R> mapToSet(@Nonnull final Stream<E> stream, @Nonnull final Function<? super E, ? extends R> mapper) {
        return stream.map(mapper).collect(toSet());
    }

    @Nonnull
    public static <E> Optional<E> first(@Nullable final List<E> list) {
        return isEmpty(list) ? Optional.empty() : Optional.ofNullable(list.get(0));
    }

    @Nonnull
    public static <E> Optional<E> first(@Nullable final Iterable<E> iterable) {
        final Iterator<E> iterator = iterable.iterator();
        return iterator.hasNext() ? Optional.ofNullable(iterator.next()) : Optional.empty();
    }

    @Nonnull
    public static <E> Optional<E> findFirst(@Nullable final Iterable<E> sequence, @Nonnull final Predicate<? super E> filter) {
        return filterToStream(sequence, filter).findFirst();
    }

    @Nonnull
    public static <E> Optional<E> findFirst(@Nullable final E[] array, @Nonnull final Predicate<? super E> filter) {
        final Stream<E> stream = array == null ? Stream.empty() : Stream.of(array);
        return stream.filter(filter).findFirst();
    }

    @Nonnull
    public static <E> Stream<E> filterToStream(@Nullable final Iterable<E> sequence, @Nonnull final Predicate<? super E> filter) {
        return stream(sequence).filter(filter);
    }

    @Nonnull
    public static <E> Stream<E> stream(@Nullable final Iterable<E> iterable) {
        if (iterable == null) {
            return Stream.empty();
        }
        return (iterable instanceof Collection)
            ? ((Collection<E>) iterable).stream()
            : StreamSupport.stream(iterable.spliterator(), false);
    }

}
