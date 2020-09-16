package lv.herbis.cnakes.configuration;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigurationExceptionTest {

	@Test
	public void testConfigurationExceptionInitializationSimpleMessage() {
		final String message = "Simple Message";
		final ConfigurationException exception = new ConfigurationException("Simple Message");

		assertEquals("Message should remain the same as it was set.", message, exception.getMessage());
	}

}