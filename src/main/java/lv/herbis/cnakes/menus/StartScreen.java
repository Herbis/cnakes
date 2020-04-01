package lv.herbis.cnakes.menus;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import lv.herbis.cnakes.configuration.ConfigurationException;
import lv.herbis.cnakes.configuration.logger.LoggingConfiguration;
import lv.herbis.cnakes.levels.singleplayer.LevelScreen;
import lv.herbis.cnakes.tools.ConfigurationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;


public class StartScreen {

    private static final Logger LOG = LogManager.getLogger(StartScreen.class);

    public static void main(final String[] args) {
        if (args.length > 0 && Arrays.asList(args).contains("-debug")) {
            LoggingConfiguration.configureLogging(true);
        } else {
            LoggingConfiguration.configureLogging(false);
        }

        LOG.debug("Game launched.");
        try {
            loadGame(ConfigurationUtil.readConfiguration());
        } catch (final ConfigurationException e) {
            LOG.error("Could not launch the game, configuration error.", e);
            System.exit(1);
        }
    }

    public static void loadGame(final CnakesConfiguration configuration) {
        final LevelScreen ls = new LevelScreen(configuration);
        final Thread lsThread = new Thread(ls);
        lsThread.start();
    }
}
