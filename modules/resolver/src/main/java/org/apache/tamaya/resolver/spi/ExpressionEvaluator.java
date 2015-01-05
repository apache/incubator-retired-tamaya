package org.apache.tamaya.resolver.spi;

import java.util.function.Function;

/**
 * Created by Anatole on 05.01.2015.
 */
public interface ExpressionEvaluator {
    /**
     * Evaluates the current expression.
     * @param key the key, not null.
     * @param valueToBeFiltered the value to be filtered/evaluated.
     * @param propertyValueProvider the provider for looking up additional keys.
     * @return the filtered/evaluated value, including null.
     */
    String filterProperty(String key, String valueToBeFiltered, Function<String,String> propertyValueProvider);
}
