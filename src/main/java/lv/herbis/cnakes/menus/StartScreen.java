package lv.herbis.cnakes.menus;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import lv.herbis.cnakes.configuration.ConfigurationException;
import lv.herbis.cnakes.configuration.logger.LoggingConfiguration;
import lv.herbis.cnakes.tools.ConfigurationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;


public class StartScreen {

	private static final Logger LOG = LogManager.getLogger(StartScreen.class);

	@SuppressWarnings("java:S4823")
	public static void main(final String[] args) {
		LoggingConfiguration.configureLogging(args.length > 0 && Arrays.asList(args).contains("-debug"));

		LOG.debug("Game launched.");
		try {
			final CnakesConfiguration configuration = ConfigurationUtil.readConfiguration(ConfigurationUtil.LOCAL_CONFIG_FILE_NAME);
			loadMenu(configuration);
		} catch (final ConfigurationException e) {
			LOG.error("Could not launch the game, configuration error.", e);
			System.exit(1);
		}
	}

	public static void loadMenu(final CnakesConfiguration configuration) {
		final MainMenu ls = new MainMenu(configuration);

		final Thread lsThread = new Thread(ls);
		lsThread.start();
	}
}
