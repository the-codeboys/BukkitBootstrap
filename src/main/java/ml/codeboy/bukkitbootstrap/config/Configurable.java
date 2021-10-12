package ml.codeboy.bukkitbootstrap.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that this class is being serialized by {@link ConfigReader}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Configurable {
    /**
     * @return the name of the file this class should be serialized as by default
     */
    String name() default "";

    /**
     * @return the comments to be added to the start of the yml file
     */
    String comments() default "";

    /**
     * @return which fields should be part of the config
     */
    ConfigScope scope() default ConfigScope.ALL;
}
