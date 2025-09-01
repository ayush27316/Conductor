package com.conductor.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as an Adapter.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Adapter {

    /**
     * A Name of the adapter. A name must be
     * unique otherwise a {@code AdapterRegistrationFailedException} will
     * be thrown
     * */
    String name();
}
