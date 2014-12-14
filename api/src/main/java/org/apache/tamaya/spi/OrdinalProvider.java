package org.apache.tamaya.spi;

import java.util.OptionalInt;

/**
 * The ordinal provider is an optional component that provides an abstraction for ordering/prioritizing
 * services loaded. This can be used to determine, which SPI should be used, if multiple instances are
 * available, or for ordering chain of services.
 * @see ServiceContext
 */
public interface OrdinalProvider {
    /**
     * Evaluate the ordinal number for the given type.
     * @param type the target type, not null.
     * @return the ordinal, if not defined, 0 should be returned.
     */
     OptionalInt getOrdinal(Class<?> type);

}
