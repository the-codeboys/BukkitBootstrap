package ml.codeboy.bukkitbootstrap.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ConfigUtil {
    static String getDefaultFileName(Class<?> config) {
        if (config.isAnnotationPresent(Configurable.class)) {
            String name = config.getAnnotation(Configurable.class).name();
            if (name != null && name.length() > 0)
                return name;
        }
        return config.getSimpleName() + ".yml";
    }

    static boolean shouldBeSerialized(Field field, ConfigScope scope) {
        int modifiers = field.getModifiers();
        if (field.isAnnotationPresent(Ignore.class) || Modifier.isTransient(modifiers))
            return false;
        return scope == ConfigScope.ALL ||
                field.isAnnotationPresent(ConfigValue.class) ||
                Modifier.isPrivate(modifiers) && scope == ConfigScope.PRIVATE ||
                Modifier.isPublic(modifiers) && scope == ConfigScope.PUBLIC;
    }

    static ConfigScope getScope(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Configurable.class))
            return clazz.getAnnotation(Configurable.class).scope();
        return ConfigScope.NONE;
    }

    static String getPath(Field field) {
        String path = field.getAnnotation(ConfigValue.class).key();
        if (path == null || path.equals(""))
            path = field.getName();
        return path;
    }

    static void addComments(FileConfiguration config, Class<?> clazz) {
        if (clazz.isAnnotationPresent(Configurable.class)) {
            String comments = clazz.getAnnotation(Configurable.class).comments();
            config.options().header(comments);
        }
    }

    static <T> T getValue(FileConfiguration config, String path) {
        return (T) config.get(path);
    }

    private static <T> T getList(FileConfiguration config, String path) {
        return (T) config.getList(path);
    }
}
