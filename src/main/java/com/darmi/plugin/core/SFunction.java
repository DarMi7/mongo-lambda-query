package com.darmi.plugin.core;

import java.io.Serializable;
import java.util.function.Function;

/**
 * SFunction
 */
@FunctionalInterface
public interface SFunction<T, R> extends Function<T, R>, Serializable {
}
