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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static lv.herbis.cnakes.constants.CnakesConstants.LOG_STACKTRACE;
import static lv.herbis.cnakes.constants.CnakesConstants.SAVE_FILE_PATH;

public class ConfigurationUtil {
	public static final String LOCAL_CONFIG_FILE_NAME = "configuration.yaml";
	private static final Logger LOG = LogManager.getLogger(ConfigurationUtil.class);

	private ConfigurationUtil() {
		// Only static access
	}

	/**
	 * Read configuration from file if possible. If not, it will create a new configuration instance and save it as well.
	 *
	 * @param configFileName configuration file name. See {@link #LOCAL_CONFIG_FILE_NAME}.
	 * @return CnakesConfiguration that's specific to the user.
	 * @throws ConfigurationException when could not properly read configuration.
	 */
	public static CnakesConfiguration readConfiguration(final String configFileName) throws ConfigurationException {
		final Representer representer = new Representer();
		representer.getPropertyUtils().setSkipMissingProperties(true);

		final Yaml yaml = new Yaml(new Constructor(CnakesConfiguration.class), representer);

		final CnakesConfiguration configuration;
		final InputStream inputStream = getLocalConfiguration(configFileName);

		if (inputStream == null) {
			LOG.warn("Local configuration not found, using default configuration.");
			configuration = new CnakesConfiguration();
		} else {
			try {
				configuration = yaml.loadAs(inputStream, CnakesConfiguration.class);
				configuration.setDefaultConfig(false);
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
		}

		if (configuration.isDefaultConfig()) {
			saveConfiguration(configuration, configFileName);
		}

		return configuration;
	}

	/**
	 * Save configuration to file.
	 *
	 * @param configuration  configuration to save.
	 * @param configFileName configuration file name. See {@link #LOCAL_CONFIG_FILE_NAME}.
	 * @throws ConfigurationException when configuration cannot be saved.
	 */
	public static void saveConfiguration(final CnakesConfiguration configuration,
										 final String configFileName) throws ConfigurationException {
		final DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		options.setPrettyFlow(true);
		final Yaml yaml = new Yaml(options);
		final Path path = Paths.get(SAVE_FILE_PATH.toString(), configFileName);

		try (final FileWriter writer = new FileWriter(path.toString())) {
			yaml.dump(configuration, writer);
		} catch (final IOException e) {
			LOG.fatal("Could not save configuration locally to path: {}", SAVE_FILE_PATH + configFileName);
			throw new ConfigurationException("Could not save configuration locally.");
		}
	}

	/**
	 * Get configuration from the file system.
	 *
	 * @param configFileName configuration file name. See {@link #LOCAL_CONFIG_FILE_NAME}.
	 * @return InputStream of the config file.
	 */
	protected static InputStream getLocalConfiguration(final String configFileName) {
		final File folder = SAVE_FILE_PATH.toFile();
		folder.mkdirs();
		final Path path = Paths.get(SAVE_FILE_PATH.toString(), configFileName);
		try {
			return new FileInputStream(path.toString());
		} catch (final FileNotFoundException e) {
			LOG.warn("Local configuration not found. Path: {}, Reason: {}", SAVE_FILE_PATH + configFileName,
					 e.getMessage());
			LOG.debug(LOG_STACKTRACE, e);

			return null;
		}
	}

	/**
	 * Remove configuration from the file system.
	 *
	 * @param configFileName configuration file name. See {@link #LOCAL_CONFIG_FILE_NAME}.
	 */
	protected static void removeLocalConfiguration(final String configFileName) throws ConfigurationException {
		try {
			if (Files.deleteIfExists(Paths.get(SAVE_FILE_PATH.toString(), configFileName))) {
				LOG.debug("Config file {} successfully removed", configFileName);
			}
		} catch (final IOException e) {
			LOG.debug(LOG_STACKTRACE, e);
			throw new ConfigurationException("Could not remove local configuration because " + e.getMessage());
		}

	}
}
