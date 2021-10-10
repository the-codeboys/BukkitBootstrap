package ml.codeboy.bukkitbootstrap.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigValue {
    /**
     * @return the key to use when saving. If this is empty the name of the field will be used
     */
    String key() default "";
}
