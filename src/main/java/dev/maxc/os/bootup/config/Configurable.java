package dev.maxc.os.bootup.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Max Carter
 * @since  10/04/2020
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Configurable {
    String FIELD_MATCHES_CONFIG_VAR = "field-matches";
    String NO_DOCUMENTATION = "No documentation available.";
    int NO_NUMBER_CONSTRAINT = -999999999;

    String value() default FIELD_MATCHES_CONFIG_VAR;
    String docs() default NO_DOCUMENTATION;
    int min() default NO_NUMBER_CONSTRAINT;
    int max() default NO_NUMBER_CONSTRAINT;

    /*
        this is the default value, if no default is entered, the system
        will not be able to enable the config and will shutdown.
     */
    int recommended() default NO_NUMBER_CONSTRAINT;
}
