package dev.maxc.sim.bootup.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Max Carter
 * @since 10/04/2020
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Configurable {
    String FIELD_MATCHES_CONFIG_VAR = "field-matches";
    String NO_DOCUMENTATION = "No documentation available.";

    String value() default FIELD_MATCHES_CONFIG_VAR;
    String docs() default NO_DOCUMENTATION;
}
