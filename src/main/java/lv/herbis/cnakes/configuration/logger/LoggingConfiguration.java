package lv.herbis.cnakes.configuration.logger;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.action.Action;
import org.apache.logging.log4j.core.appender.rolling.action.DeleteAction;
import org.apache.logging.log4j.core.appender.rolling.action.IfAccumulatedFileCount;
import org.apache.logging.log4j.core.appender.rolling.action.PathCondition;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.util.zip.Deflater;

public class LoggingConfiguration {

	private static final int MIN_FILES = 5;
	private static final int MAX_FILES = 5;
	private static final String MIN_FILES_S = String.valueOf(MIN_FILES);
	private static final String MAX_FILES_S = String.valueOf(MAX_FILES);
	private static final String LOG_COMPRESSION = String.valueOf(Deflater.NO_COMPRESSION);
	private static final String LOG_PATH = "logs/";
	private static final String LOG_FILE_NAME = "cnakes.log";
	private static final String LOG_ROLLED_FILE_NAME = "cnakes.%d{yyyy-MM-dd-HH-mm}.log";
	private static final String MAX_ROLLING_FILE_SIZE = "5 M";

	private LoggingConfiguration() {
		// Only static access.
	}

	public static void configureLogging(final boolean debug) {
		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
		final Configuration config = ctx.getConfiguration();

		final PathCondition[] pathConditions = new PathCondition[1];
		pathConditions[0] = IfAccumulatedFileCount.createFileCountCondition(MAX_FILES);

		final DeleteAction action = DeleteAction.createDeleteAction(LOG_PATH, true, 1, false, null, pathConditions, null, config);
		final Action[] actions = new Action[1];
		actions[0] = action;

		final DefaultRolloverStrategy strategy = DefaultRolloverStrategy.newBuilder()
				.withMax(MAX_FILES_S)
				.withCustomActions(actions)
				.withMin(MIN_FILES_S)
				.withFileIndex("max")
				.withConfig(config)
				.withCompressionLevelStr(LOG_COMPRESSION)
				.build();

		final PatternLayout pl = PatternLayout.newBuilder().withPattern("%d %p %C - %m%n").build();

		final RollingFileAppender appender = RollingFileAppender.newBuilder()
				.setConfiguration(config)
				.setName("rolling")
				.setLayout(pl)
				.withFileName(LOG_PATH + LOG_FILE_NAME)
				.withFilePattern(LOG_PATH + LOG_ROLLED_FILE_NAME)
				.withPolicy(SizeBasedTriggeringPolicy.createPolicy(MAX_ROLLING_FILE_SIZE))
				.withStrategy(strategy)
				.build();

		appender.start();
		config.addAppender(appender); // NOSONAR

		final LoggerConfig loggerConfig = config.getRootLogger();
		loggerConfig.addAppender(appender, debug ? Level.DEBUG : Level.WARN, null); // NOSONAR
		Configurator.setRootLevel(debug ? Level.DEBUG : Level.WARN); // NOSONAR
	}
}
