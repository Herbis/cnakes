package lv.herbis.cnakes.tools;

import lv.herbis.cnakes.configuration.CnakesConfiguration;
import lv.herbis.cnakes.configuration.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class ConfigurationUtilTest {
	private static final Logger LOG = LogManager.getLogger(ConfigurationUtilTest.class);

	private static final String CONFIG_FILE = "test_configuration.yaml";

	@After
	public void tearDown() {
		try {
			ConfigurationUtil.removeLocalConfiguration(CONFIG_FILE);
		} catch (final ConfigurationException e)
		{
			LOG.error("Could not remove configuration.", e);
		}
	}

	@Test
	public void testReadConfigurationWithNoFile() {
		try {
			final CnakesConfiguration config = ConfigurationUtil.readConfiguration(CONFIG_FILE);
			assertNotNull("Configuration should not be null", config);
			assertTrue("Default config should be loaded when no file present.", config.isDefaultConfig());
		} catch (final ConfigurationException e) {
			LOG.error("Could not read configuration.", e);
			Assert.fail(String.format("Reading configuration with no file, should not throw exception. Error: %s",
									  e.getMessage()));
		}
	}

	@Test
	public void testReadConfigurationWithExistingFile() {
		final CnakesConfiguration newConfig = new CnakesConfiguration();
		newConfig.setDefaultConfig(true);
		final int gameSpeed = newConfig.getGameplay().getGameSpeed() + 20;
		newConfig.getGameplay().setGameSpeed(gameSpeed);

		assertThatSaveConfigWithoutExceptions(newConfig);


		try {
			final CnakesConfiguration config = ConfigurationUtil.readConfiguration(CONFIG_FILE);
			assertNotNull("Configuration should not be null", config);
			assertFalse("Default config should not be loaded when no file present.", config.isDefaultConfig());
			assertEquals("Game Speed should match previously set game speed", gameSpeed,
						 config.getGameplay().getGameSpeed());
		} catch (final ConfigurationException e) {
			LOG.error("Could not read configuration with existing file.", e);
			Assert.fail(String.format("Reading configuration with existing file, should not throw exception. Error: %s",
									  e.getMessage()));
		}
	}

	@Test
	public void testSaveConfiguration() {
		assertThatSaveConfigWithoutExceptions(null);
		assertThatSaveConfigWithoutExceptions(new CnakesConfiguration());
	}

	@Test
	public void testGetLocalConfiguration() {
		assertThatLocalConfigurationDoesNotExist();
		assertThatSaveConfigWithoutExceptions(new CnakesConfiguration());
		assertThatLocalConfigurationExists();
	}

	@Test
	public void testRemoveLocalConfigurationWithExistingConfiguration() {
		// create and save
		assertThatSaveConfigWithoutExceptions(new CnakesConfiguration());
		// read
		assertThatLocalConfigurationExists();
		// delete
		assertThatLocalConfigurationRemoveConfigWithoutExceptions();
		// read
		assertThatLocalConfigurationDoesNotExist();
	}

	@Test
	public void testRemoveLocalConfigurationWithNotExistingConfiguration() {
		// read
		assertThatLocalConfigurationDoesNotExist();
		// delete
		assertThatLocalConfigurationRemoveConfigWithoutExceptions();
	}

	private void assertThatLocalConfigurationExists()
	{
		try (final InputStream inputStream = ConfigurationUtil.getLocalConfiguration(CONFIG_FILE)) {
			assertNotNull("Configuration file should exist.", inputStream);
		} catch (final IOException e) {
			Assert.fail(String.format("Could not read configuration file input stream. Error %s", e.getMessage()));
		}
	}

	private void assertThatLocalConfigurationDoesNotExist()
	{
		try (final InputStream inputStream = ConfigurationUtil.getLocalConfiguration(CONFIG_FILE)) {
			assertNull("Local configuration file should not exist.", inputStream);
		} catch (final IOException e) {
			Assert.fail(String.format("Could not read configuration file input stream. Error %s", e.getMessage()));
		}
	}

	private void assertThatSaveConfigWithoutExceptions(final CnakesConfiguration cnakesConfiguration)
	{
		try {
			ConfigurationUtil.saveConfiguration(cnakesConfiguration, CONFIG_FILE);
		} catch (final ConfigurationException e) {
			LOG.error("Could not remove existing file.", e);
			Assert.fail("Could not remove local configuration file.");
		}
	}

	private void assertThatLocalConfigurationRemoveConfigWithoutExceptions()
	{
		try {
			ConfigurationUtil.removeLocalConfiguration(CONFIG_FILE);
		} catch (final ConfigurationException e) {
			LOG.error("Could not remove existing file.", e);
			Assert.fail("Removing not existing file should not throw an error..");
		}
	}
}