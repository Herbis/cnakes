package lv.herbis.cnakes.tools;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConversionUtilTest {

	private static final int NANO_SEC_IN_MS = 1_000_000;

	@Test
	public void testMillisecondsToLocalDateTimeWithCurrentDateTime() {
		final LocalDateTime startDate = LocalDateTime.now();
		final long milliseconds = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		final LocalDateTime output = ConversionUtil.millisecondsToLocalDateTime(milliseconds);
		assertTrue("Difference between dates should not smaller than a milisecond.",
				   Math.abs(ChronoUnit.NANOS.between(startDate, output)) < NANO_SEC_IN_MS);
	}

	@Test
	public void testMillisecondsToLocalDateTime() {
		final int nanoSeconds = 307_763_100;
		final int expectedNanoDifference = nanoSeconds - ((nanoSeconds / NANO_SEC_IN_MS) * NANO_SEC_IN_MS);
		final LocalDateTime startDate = LocalDateTime.of(2020, 7, 13, 16, 20, 15, nanoSeconds);

		final long milliseconds = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		final LocalDateTime output = ConversionUtil.millisecondsToLocalDateTime(milliseconds);
		assertEquals("Difference between dates should be equal to cut nanoseconds.", expectedNanoDifference,
					 Math.abs(ChronoUnit.NANOS.between(startDate, output)));
	}
}