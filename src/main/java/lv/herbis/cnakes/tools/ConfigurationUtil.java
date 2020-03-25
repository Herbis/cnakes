package lv.herbis.cnakes.tools;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import lv.herbis.cnakes.configuration.ConfigurationException;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;

public class ConfigurationUtil {
    private static final String DEFAULT_CONFIG_FILE_NAME = "defaultConfiguration.yaml";

    private ConfigurationUtil() {
        // Only static access
    }

    public static CnakesConfiguration readConfiguration() throws ConfigurationException {
        final Yaml yaml = new Yaml();
        final InputStream inputStream = getResourceInputStream(DEFAULT_CONFIG_FILE_NAME);

        if (inputStream == null) {
            throw new ConfigurationException("Configuration could not be read.");
        } else {
            try {
                return yaml.loadAs(inputStream, CnakesConfiguration.class);
            } catch (final Exception e) {
                e.printStackTrace();
                throw new ConfigurationException(e.getMessage());
            } finally {
                try {
                    inputStream.close();
                } catch (final IOException e) {
                    System.out.println("Can't close config input stream. " + e.getMessage());
                }
            }
        }
    }

    private static InputStream getResourceInputStream(final String resourceName) {
        return ConfigurationUtil.class.getClassLoader().getResourceAsStream(resourceName);
    }
}
