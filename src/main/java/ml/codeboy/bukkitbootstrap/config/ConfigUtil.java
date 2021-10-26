package ml.codeboy.bukkitbootstrap.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        String path = null;
        if (field.isAnnotationPresent(ConfigValue.class))
            path = field.getAnnotation(ConfigValue.class).key();
        if (path == null || path.equals(""))
            path = field.getName();
        return path;
    }

    static void addComments(FileConfiguration config, Class<?> clazz) {
        if (clazz.isAnnotationPresent(Configurable.class)) {
            String comments = clazz.getAnnotation(Configurable.class).comments();
            config.options().header(comments);
            config.options().copyHeader(true);
        }
    }

    static <T> T getValue(FileConfiguration config, String path) {
        return (T) config.get(path);
    }

    static <S,T> HashMap<S,T>getHashMap(FileConfiguration config, String path){
        List<S> keys= (List<S>) config.getList(path+".keys",new ArrayList<S>());
        List<T> values= (List<T>) config.getList(path+".values",new ArrayList<T>());
        if(keys.size()!=values.size())
            throw new IllegalStateException("Tried to load HashMap with different sizes for keys and values");
        HashMap<S,T>map=new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            map.put(keys.get(i),values.get(i));
        }
        return map;
    }

    static <S,T> void saveHashMap(FileConfiguration config, String path, Object hashMap){
        if(!(hashMap instanceof  HashMap))
            throw new IllegalArgumentException();
        HashMap<S,T>map= (HashMap<S, T>) hashMap;
        List<S> keys= new ArrayList<>(map.keySet());
        List<T> values= new ArrayList<>(map.values());
        config.set(path+".keys",keys);
        config.set(path+".values",values);
    }

}
