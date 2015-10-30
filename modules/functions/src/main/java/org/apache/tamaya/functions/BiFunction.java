package org.apache.tamaya.functions;

/**
 * Created by atsticks on 30.10.15.
 */
//@FunctionalInterface
public interface BiFunction<R, T1, T2> {
    R apply(T1 param1, T2 param2);
}
