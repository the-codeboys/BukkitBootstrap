package ml.codeboy.bukkitbootstrap.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

import static ml.codeboy.bukkitbootstrap.config.ConfigUtil.getConfigurableFields;

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
        saveOrLoadConfig(saveTo, instance, file, false);
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
        saveOrLoadConfig(saveFrom, instance, file, true);
    }


    public static void saveOrLoadConfig(Class<?> clazz, Object instance, File file, boolean save) {
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

        boolean modified = false;

        for (Field field : getConfigurableFields(clazz)) {
            field.setAccessible(true);

            String path = ConfigUtil.getPath(field);

            try {
                if (save || !config.contains(path)) {
                    if (HashMap.class.isAssignableFrom(field.getType()))
                        ConfigUtil.saveHashMap(config, path, field.get(instance));
                    else
                        config.set(path, field.get(instance));
                    modified = true;
                } else {
                    if (HashMap.class.isAssignableFrom(field.getType()))
                        field.set(instance, ConfigUtil.getHashMap(config, path));
                    else
                        field.set(instance, ConfigUtil.getValue(config, path,field.getType()));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if (save || modified) {
            ConfigUtil.addComments(config, clazz);
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Translates color codes in all the configurable Fields in a class.
     * This method is meant to be used only for classes which don´t get saved with {@link ConfigReader#saveConfig(Class)}
     *
     * @param clazz     The class
     * @param instance  The instance of the class. Use null to only affect static fields
     * @param colorChar The color char to use e.g. '&'
     */
    public static void translateColorCodes(Class<?> clazz, Object instance, char colorChar) {
        for (Field field : getConfigurableFields(clazz)) {
            if (field.getType().equals(String.class)) {
                field.setAccessible(true);
                try {
                    Object value = field.get(instance);
                    if (value instanceof String) {
                        String string = (String) value;
                        string = ChatColor.translateAlternateColorCodes(colorChar, string);
                        value = string;
                        field.set(instance, value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
