package dev.maxc.sim.bootup.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dev.maxc.App;
import dev.maxc.sim.logs.Logger;
import dev.maxc.sim.system.SystemAPI;

/**
 * @author Max Carter
 * @since 10/04/2020
 */
public class ConfigurationReader {
    private static final String CONFIG_FILE_NAME = "config.txt";

    private final SystemAPI system;

    /**
     * Creates a system reader
     */
    public ConfigurationReader(SystemAPI system) {
        this.system = system;
    }

    /**
     * Reads the config file and updates the attributes in the config object
     */
    public void configure() {
        ConfigurationFileReader configurationFileReader = new ConfigurationFileReader();
        ArrayList<Field> fields = getConfigurationFields();
        Field found = null;

        for (Map.Entry<String, String> config : configurationFileReader.getConfigFileMap().entrySet()) {
            for (Field field : fields) {
                if (config.getKey().equalsIgnoreCase(field.getName())) {
                    try {
                        field.set(system, getDeducedType(config.getValue()));
                    } catch (IllegalAccessException | NullPointerException ex) {
                        Logger.log("Config", "Unable to configure value for: " + config.getKey().toUpperCase());
                        ex.printStackTrace();
                    }
                    Logger.log("Config", "Set config option [" + config.getKey().toUpperCase() + "] to [" + config.getValue().toLowerCase() + "]");
                    found = field;
                    break;
                }
            }
            //removes found field from list of fields to reduce loops.
            if (found != null) {
                fields.remove(found);
                found = null;
            }
        }

        if (!fields.isEmpty()) {
            Logger.log("Config", "There are missing attributes in the config file.");
        }
        Logger.log("Config", "Successfully configured the SystemAPI.");
    }

    /**
     * Deduces the data type from a string. The type should only
     * either be a boolean or an integer, so a NumberFormatException
     * is thrown if the value isn't a boolean or an integer.
     */
    private Object getDeducedType(String value) throws NumberFormatException {
        if (value.equalsIgnoreCase("true")) {
            return true;
        } else if (value.equalsIgnoreCase("false")) {
            return false;
        } else {
            return Integer.parseInt(value);
        }
    }

    /**
     * Gets the list of attributes in the configuration class.
     */
    private ArrayList<Field> getConfigurationFields() {
        ArrayList<Field> fields = new ArrayList<>();
        for (Field field : system.getClass().getDeclaredFields()) {
            if (field.getAnnotation(Configurable.class) != null && Modifier.isPublic(field.getModifiers())) {
                fields.add(field);
            }
        }
        return fields;
    }

    /**
     * Dedicated nested class for reading the file of the config file.
     */
    protected static final class ConfigurationFileReader {
        private static final String COMMENT_CHARACTER = "#";

        /**
         * Returns a map of the config attributes and their values in raw form.
         * An exception is logged and thrown if the config file is incorrect or
         * there is an internal error with the file reader.
         */
        protected HashMap<String, String> getConfigFileMap() {
            Logger.log("Gathering config file into map...");
            HashMap<String, String> configMap = new HashMap<>();
            String line;
            try {
                String file = App.class.getResource(CONFIG_FILE_NAME).getFile();
                BufferedReader re = new BufferedReader(new FileReader(file));
                while ((line = re.readLine()) != null) {
                    if (!line.startsWith(COMMENT_CHARACTER) && !line.isBlank()) {
                        String[] content = line.trim().split("=");
                        configMap.put(content[0], content[1]);
                    }
                }
            } catch (IOException ex) {
                Logger.log("IOException occurred during config file reading.");
                ex.printStackTrace();
            } catch (IndexOutOfBoundsException ex) {
                Logger.log("Config file formatted incorrectly.");
                ex.printStackTrace();
            }

            Logger.log("Gathered config file and parsed to map successfully.");
            return configMap;
        }
    }
}
