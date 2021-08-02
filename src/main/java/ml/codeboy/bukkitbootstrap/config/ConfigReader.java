
package ml.codeboy.bukkitbootstrap.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class ConfigReader {

    public static void readConfig(Class<?> saveTo) {
        File file= new File(JavaPlugin.getProvidingPlugin(saveTo).getDataFolder(),saveTo.getSimpleName()+".yml");
        readConfig(saveTo,file);
    }

    public static void readConfig(Class<?> saveTo, File file) {
        readConfig(saveTo, null, file);
    }

    public static void readConfig(Class<?> saveTo, Object instance, File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        boolean changed = false;

        for (Field field : saveTo.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigValue.class)) {
                field.setAccessible(true);

                String path = field.getAnnotation(ConfigValue.class).key();
                if (path.equals(""))
                    path = field.getName();

                try {
                    if (config.contains(path)) {
                        field.set(instance, getValue(config, path));
                    }
                    else {
                        config.set(path, field.get(instance));
                        changed = true;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        if (changed) {
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveConfig(Class<?> saveFrom) {
        File file= new File(JavaPlugin.getProvidingPlugin(saveFrom).getDataFolder(),saveFrom.getName()+".yml");
        saveConfig(saveFrom,file);
    }

    public static void saveConfig(Class<?> saveFrom, File file) {
        saveConfig(saveFrom, null, file);
    }

    public static void saveConfig(Class<?> saveFrom, Object instance, File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        for (Field field : saveFrom.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigValue.class)) {
                field.setAccessible(true);

                String path = field.getAnnotation(ConfigValue.class).key();
                if (path.equals(""))
                    path = field.getName();

                try {
                    config.set(path, field.get(instance));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static <T> T getValue(FileConfiguration config, String path) {
        return (T) config.get(path);
    }

    private static <T> T getList(FileConfiguration config, String path) {
        return (T) config.getList(path);
    }
}
