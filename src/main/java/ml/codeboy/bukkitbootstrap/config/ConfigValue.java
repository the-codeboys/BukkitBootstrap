package ml.codeboy.bukkitbootstrap.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that this field should be serialized by {@link ConfigReader}.
 * Transient fields or fields with {@link Ignore} will never be serialized
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigValue {
    /**
     * @return the key to use when saving. If this is empty the name of the field will be used
     */
    String key() default "";
}
