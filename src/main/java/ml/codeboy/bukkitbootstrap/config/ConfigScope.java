package ml.codeboy.bukkitbootstrap.config;

/**
 * defines which part of a class should be serialized with {@link ConfigReader}
 */
public enum ConfigScope {
    /**
     * only fields marked with {@link ConfigValue} will be serialized
     */
    NONE,
    /**
     * all the fields except for the ones with {@link Ignore} will be serialized
     */
    ALL,
    /**
     * all the public fields will be serialized
     */
    PUBLIC,
    /**
     * all the private fields will be serialized
     */
    PRIVATE
}
