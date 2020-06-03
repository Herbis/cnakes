package lv.herbis.cnakes.tools;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import lv.herbis.cnakes.configuration.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.IOException;
import java.io.InputStream;

public class ConfigurationUtil {
	private static final Logger LOG = LogManager.getLogger(ConfigurationUtil.class);

	private static final String DEFAULT_CONFIG_FILE_NAME = "defaultConfiguration.yaml";

	private ConfigurationUtil() {
		// Only static access
	}

	public static CnakesConfiguration readConfiguration() throws ConfigurationException {
		final Representer representer = new Representer();
		representer.getPropertyUtils().setSkipMissingProperties(true);

		final Yaml yaml = new Yaml(new Constructor(CnakesConfiguration.class), representer);

		final InputStream inputStream = getResourceInputStream(DEFAULT_CONFIG_FILE_NAME);

		if (inputStream == null) {
			throw new ConfigurationException("Configuration could not be read.");
		} else {
			try {
				return yaml.loadAs(inputStream, CnakesConfiguration.class);
			} catch (final Exception e) {
				LOG.error("Error occurred while loading Configuration.", e);
				throw new ConfigurationException(e.getMessage());
			} finally {
				try {
					inputStream.close();
				} catch (final IOException e) {
					LOG.error("Can't close config input stream. {}", e.getMessage());
					LOG.debug("Stacktrace: ", e);
				}
			}
		}
	}

	private static InputStream getResourceInputStream(final String resourceName) {
		return ConfigurationUtil.class.getClassLoader().getResourceAsStream(resourceName);
	}
}
