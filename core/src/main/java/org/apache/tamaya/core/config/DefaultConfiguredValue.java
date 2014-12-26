package org.apache.tamaya.core.config;

import org.apache.tamaya.ConfiguredValue;
import org.apache.tamaya.annotation.LoadPolicy;

import java.beans.PropertyChangeEvent;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Implementation of a configured value (in progress).
 */
public class DefaultConfiguredValue<T> implements ConfiguredValue<T>{

    private LoadPolicy loadPolicy = LoadPolicy.INITIAL;
    private AtomicLong lastUpdate;
    private AtomicLong lastAccess;
    private Optional<T> value;

    public static final DefaultConfiguredValue EMPTY = new DefaultConfiguredValue(null);

    /**
     * Returns an empty {@code Optional} instance.  No value is present for this
     * Optional.
     *
     * @apiNote Though it may be tempting to do so, avoid testing if an object
     * is empty by comparing with {@code ==} against instances returned by
     * {@code Option.empty()}. There is no guarantee that it is a singleton.
     * Instead, use {@link #isPresent()}.
     *
     * @param <T> Type of the non-existent value
     * @return an empty {@code Optional}
     */
    public static <T> DefaultConfiguredValue<T> empty() {
        DefaultConfiguredValue v = (DefaultConfiguredValue<T>) EMPTY;
        return v;
    }

    private DefaultConfiguredValue(Optional<T> item){
        this.value = item;
    }

    public static <T> ConfiguredValue<T> of(T instance){
        return new DefaultConfiguredValue(Optional.of(instance));
    }

    public static <T> ConfiguredValue<T> ofNullable(T value){
        return value == null ? empty() : of(value);
    }

    @Override
    public LoadPolicy getLoadPolicy() {
        return loadPolicy;
    }

    @Override
    public long getLastAccess() {
        return lastAccess.get();
    }

    @Override
    public long getLastUpdate() {
        return lastUpdate.get();
    }

    @Override
    public boolean isUpdatedSince(long timestamp) {
        return getLastUpdate()>timestamp;
    }

    @Override
    public boolean isAccessedSince(long timestamp) {
        return getLastAccess()>timestamp;
    }

    @Override
    public void addListener(Consumer<PropertyChangeEvent> l) {
// TODO
    }

    @Override
    public void removeListener(Consumer<PropertyChangeEvent> l) {
// TODO
    }

    @Override
    public T get() {
        return value.get();
    }

    @Override
    public void update() {
// TODO
    }

    @Override
    public boolean isPresent() {
        return value.isPresent();
    }

    @Override
    public void ifPresent(Consumer<? super T> consumer) {
        value.ifPresent(consumer);
    }

    @Override
    public ConfiguredValue<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (!isPresent())
            return this;
        else
            return predicate.test(value.get()) ? this : empty();
    }

    @Override
    public <U> ConfiguredValue<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return empty();
        else {
            return DefaultConfiguredValue.ofNullable(mapper.apply(value.get()));
        }
    }

    @Override
    public <U> ConfiguredValue<U> flatMap(Function<? super T, ConfiguredValue<U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent())
            return empty();
        else {
            return Objects.requireNonNull(mapper.apply(value.get()));
        }
    }

    @Override
    public T orElse(T other) {
        return value.orElse(other);
    }

    @Override
    public T orElseGet(Supplier<? extends T> other) {
        return value.orElseGet(other);
    }

    @Override
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return value.orElseThrow(exceptionSupplier);
    }

    public Optional<T> toOptional(){
        return value;
    }
}
