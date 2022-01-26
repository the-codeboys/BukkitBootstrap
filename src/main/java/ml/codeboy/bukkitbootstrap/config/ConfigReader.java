package ml.codeboy.bukkitbootstrap.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import static ml.codeboy.bukkitbootstrap.config.ConfigUtil.getConfigurableFields;
import static ml.codeboy.bukkitbootstrap.config.ConfigUtil.readValue;

public class ConfigReader {

    /**
     * Applies the values from a config to the static fields of the specified class.
     * If the config file does not exist or has missing values it will be created.
     *
     * @param saveTo the class to save the config values to
     */
    public static void readConfig(Class<?> saveTo) {
        readConfig(saveTo, ConfigUtil.getDefaultFileName(saveTo));
    }

    /**
     * Applies the values from a config to the static fields of the specified class.
     * If the config file does not exist or has missing values it will be created.
     *
     * @param saveTo   the class to save the config values to
     * @param fileName the name of the config file
     */
    public static void readConfig(Class<?> saveTo, String fileName) {
        File file = new File(JavaPlugin.getProvidingPlugin(saveTo).getDataFolder(), fileName);
        readConfig(saveTo, file);
    }

    /**
     * Applies the values from a config to the static fields of the specified class.
     * If the config file does not exist or has missing values it will be created.
     *
     * @param saveTo the class to save the config values to
     * @param file   the file where the config is located (doesn't have to exist)
     */
    public static void readConfig(Class<?> saveTo, File file) {
        readConfig(saveTo, null, file);
    }

    /**
     * Applies the values from a config to the fields instance of the specified class.
     * If the config file does not exist or has missing values it will be created.
     *
     * @param saveTo   the class to save the config values to
     * @param file     the file where the config is located (doesn't have to exist)
     * @param instance the instance of saveTo to save the values to
     */
    public static void readConfig(Class<?> saveTo, Object instance, File file) {
        saveOrLoadConfig(saveTo, instance, file, false);
    }

    /**
     * Saves the static fields of the specified class to a config
     *
     * @param saveFrom the class to read the values from
     */
    public static void saveConfig(Class<?> saveFrom) {
        saveConfig(saveFrom, ConfigUtil.getDefaultFileName(saveFrom));
    }

    /**
     * Saves the static fields of the specified class to a config
     *
     * @param saveFrom the class to read the values from
     * @param name     the name of the config file
     */
    public static void saveConfig(Class<?> saveFrom, String name) {
        File file = new File(JavaPlugin.getProvidingPlugin(saveFrom).getDataFolder(), name);
        saveConfig(saveFrom, file);
    }

    /**
     * Saves the static fields of the specified class to a config
     *
     * @param saveFrom the class to read the values from
     * @param file     the file to save to (will be overridden)
     */
    public static void saveConfig(Class<?> saveFrom, File file) {
        saveConfig(saveFrom, null, file);
    }

    /**
     * Saves the fields of the specified object to a config
     *
     * @param saveFrom the class to read the values from
     * @param file     the file to save to (will be overridden)
     * @param instance the instance of saveFrom to read the values from
     */
    public static void saveConfig(Class<?> saveFrom, Object instance, File file) {
        saveOrLoadConfig(saveFrom, instance, file, true);
    }


    /**
     * For internal use only. Use {@link ConfigReader#saveConfig(Class, Object, File)} or {@link ConfigReader#readConfig(Class, Object, File)}
     */
    public static void saveOrLoadConfig(Class<?> clazz, Object instance, File file, boolean save) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();//create parent of file
            try {
                file.createNewFile();//create config
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(file);//load config
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        boolean modified = false;//if the config was modified

        //loop through all the fields
        for (Field field : getConfigurableFields(clazz)) {
            field.setAccessible(true);

            String path = ConfigUtil.getPath(field);//the path where the config value should be

            try {
                if (save) {
                    ConfigUtil.saveValue(config, path, field, instance);
                } else {
                    if (config.contains(path)) {
                        readValue(config, path, field, instance);
                    } else {
                        for (String altPath : ConfigUtil.getAlternativePaths(field)) {
                            if (config.contains(altPath)) {
                                readValue(config, altPath, field, instance);// values might be read multiple times however it is highly unlikely, so I will ignore it
                                config.set(altPath, null);
                            }
                        }
                        ConfigUtil.saveValue(config, path, field, instance);
                        modified = true;
                    }
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
