package ml.codeboy.bukkitbootstrap.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;

public class ConfigReader {

    public static void readConfig(Class<?> saveTo) {
        readConfig(saveTo, ConfigUtil.getDefaultFileName(saveTo));
    }

    public static void readConfig(Class<?> saveTo, String fileName) {
        File file = new File(JavaPlugin.getProvidingPlugin(saveTo).getDataFolder(), fileName);
        readConfig(saveTo, file);
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

        ConfigScope scope = ConfigUtil.getScope(saveTo);

        for (Field field : saveTo.getDeclaredFields()) {
            if (ConfigUtil.shouldBeSerialized(field, scope)) {
                field.setAccessible(true);

                String path = ConfigUtil.getPath(field);

                try {
                    if (config.contains(path)) {
                        if (HashMap.class.isAssignableFrom(field.getType()))
                            field.set(instance, ConfigUtil.getHashMap(config, path));
                        else if (UUID.class.isAssignableFrom(field.getType()))
                            field.set(instance, ConfigUtil.getUUID(config, path));
                        else
                            field.set(instance, ConfigUtil.getValue(config, path));
                    } else {
                        if (HashMap.class.isAssignableFrom(field.getType()))
                            ConfigUtil.saveHashMap(config, path, field.get(instance));
                        else if (UUID.class.isAssignableFrom(field.getType()))
                            ConfigUtil.saveUUID(config, path, field.get(instance));
                        else
                            config.set(path, field.get(instance));
                        changed = true;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        if (changed) {
            ConfigUtil.addComments(config, saveTo);
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveConfig(Class<?> saveFrom) {
        readConfig(saveFrom, ConfigUtil.getDefaultFileName(saveFrom));
    }

    public static void saveConfig(Class<?> saveFrom, String name) {
        File file = new File(JavaPlugin.getProvidingPlugin(saveFrom).getDataFolder(), name);
        saveConfig(saveFrom, file);
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

        ConfigScope scope = ConfigUtil.getScope(saveFrom);

        for (Field field : saveFrom.getDeclaredFields()) {
            if (ConfigUtil.shouldBeSerialized(field, scope)) {
                field.setAccessible(true);

                String path = ConfigUtil.getPath(field);

                try {
                    if (HashMap.class.isAssignableFrom(field.getType()))
                        ConfigUtil.saveHashMap(config, path, field.get(instance));
                    else
                        config.set(path, field.get(instance));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        ConfigUtil.addComments(config, saveFrom);
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
