package dev.maxc.os.bootup.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dev.maxc.App;
import dev.maxc.os.io.log.Logger;
import dev.maxc.os.bootup.DynamicComponentLoader;
import dev.maxc.os.io.log.Status;
import dev.maxc.os.system.api.SystemAPI;

/**
 * @author Max Carter
 * @since 10/04/2020
 */
public class ConfigurationReader {
    private static final String CONFIG_FILE_NAME = "config.txt";

    private final SystemAPI system;
    private final DynamicComponentLoader componentLoader;

    /**
     * Creates a system reader
     */
    public ConfigurationReader(SystemAPI system, DynamicComponentLoader componentLoader) {
        this.system = system;
        this.componentLoader = componentLoader;
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
                String fieldName = field.getAnnotation(Configurable.class).value().toLowerCase();
                if (fieldName.equalsIgnoreCase(Configurable.FIELD_MATCHES_CONFIG_VAR)) {
                    fieldName = field.getName();
                }
                if (config.getKey().equalsIgnoreCase(fieldName)) {
                    componentLoader.componentLoaded("Parsing " + field.getName().toLowerCase().replace("_", " ") + " config option.");
                    try {
                        field.set(system, getDeducedType(config.getValue()));
                    } catch (IllegalAccessException | NullPointerException ex) {
                        Logger.log(Status.WARN, this, "Unable to configure value for: " + config.getKey().toUpperCase());
                        ex.printStackTrace();
                    }
                    componentLoader.componentLoaded("Parsed " + field.getName().toLowerCase().replace("_", " ") + ".");
                    Logger.log(this, "Set config option [" + config.getKey().toUpperCase() + "] to [" + config.getValue().toLowerCase() + "]");
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
            Logger.log(Status.WARN, this, "There are missing attributes in the config file.");
        }
        Logger.log(this, "Successfully configured the SystemAPI.");
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
            Logger.log(this, "Gathering config file into map...");
            HashMap<String, String> configMap = new HashMap<>();
            String line;
            try {
                String file = App.class.getResource(CONFIG_FILE_NAME).getFile();
                BufferedReader re = new BufferedReader(new FileReader(file));
                while ((line = re.readLine()) != null) {
                    if (!line.trim().startsWith(COMMENT_CHARACTER) && !line.trim().isBlank()) {
                        String[] content = line.trim().split("=");
                        if (content.length == 0) {
                            Logger.log(Status.ERROR, this, "Unknown error in the config file, a config option name is null.");
                        } else if (content.length == 1) {
                            Logger.log(Status.ERROR, this, "Config option [" + content[0] + "] does not have a value");
                        } else {
                            configMap.put(content[0], content[1]);
                        }
                    }
                }
            } catch (IOException ex) {
                Logger.log(Status.CRIT, this, "IOException occurred during config file reading.");
                ex.printStackTrace();
            } catch (IndexOutOfBoundsException ex) {
                Logger.log(Status.CRIT, this, "Config file formatted incorrectly.");
                ex.printStackTrace();
            }

            Logger.log(this, "Gathered config file and parsed to map successfully.");
            return configMap;
        }
    }
}
