package lv.herbis.cnakes.tools;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import lv.herbis.cnakes.configuration.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;

import static lv.herbis.cnakes.constants.CnakesConstants.LOG_STACKTRACE;
import static lv.herbis.cnakes.constants.CnakesConstants.SAVE_FILE_PATH;

public class ConfigurationUtil {
	private static final Logger LOG = LogManager.getLogger(ConfigurationUtil.class);

	private static final String DEFAULT_CONFIG_FILE_NAME = "defaultConfiguration.yaml";
	private static final String LOCAL_CONFIG_FILE_NAME = "configuration.yaml";

	private ConfigurationUtil() {
		// Only static access
	}

	public static CnakesConfiguration readConfiguration() throws ConfigurationException {
		final Representer representer = new Representer();
		representer.getPropertyUtils().setSkipMissingProperties(true);

		final Yaml yaml = new Yaml(new Constructor(CnakesConfiguration.class), representer);

		boolean usingDefaultConfig = false;

		InputStream inputStream = getLocalConfiguration();
		if (inputStream == null) {
			usingDefaultConfig = true;
			inputStream = getDefaultConfigurationInputStream();
		}

		if (inputStream == null) {
			throw new ConfigurationException("Configuration could not be read.");
		} else {
			final CnakesConfiguration configuration;
			try {
				configuration = yaml.loadAs(inputStream, CnakesConfiguration.class);
			} catch (final Exception e) {
				LOG.error("Error occurred while loading Configuration.", e);
				throw new ConfigurationException(e.getMessage());
			} finally {
				try {
					inputStream.close();
				} catch (final IOException e) {
					LOG.error("Can't close config input stream. {}", e.getMessage());
					LOG.debug(LOG_STACKTRACE, e);
				}
			}

			if (usingDefaultConfig) {
				saveConfiguration(configuration);
			}

			return configuration;
		}
	}

	public static void saveConfiguration(final CnakesConfiguration configuration) throws ConfigurationException {
		final DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		options.setPrettyFlow(true);
		final Yaml yaml = new Yaml(options);

		try {
			final FileWriter writer = new FileWriter(SAVE_FILE_PATH + LOCAL_CONFIG_FILE_NAME);
			yaml.dump(configuration, writer);
		} catch (final IOException e) {
			LOG.fatal("Could not save configuration locally to path: {}", SAVE_FILE_PATH + LOCAL_CONFIG_FILE_NAME);
			throw new ConfigurationException("Could not save configuration locally.");
		}
	}

	private static InputStream getLocalConfiguration() {
		final File file = new File(SAVE_FILE_PATH);
		file.mkdirs();
		try {
			return new FileInputStream(SAVE_FILE_PATH + LOCAL_CONFIG_FILE_NAME);
		} catch (final FileNotFoundException e) {
			LOG.warn("Local configuration not found. Path: {}, Reason: {}", SAVE_FILE_PATH + LOCAL_CONFIG_FILE_NAME,
					 e.getMessage());
			LOG.debug(LOG_STACKTRACE, e);

			return null;
		}
	}

	private static InputStream getDefaultConfigurationInputStream() {
		return ConfigurationUtil.class.getClassLoader().getResourceAsStream(DEFAULT_CONFIG_FILE_NAME);
	}
}
