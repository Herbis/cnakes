package lv.herbis.cnakes.tools;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ConversionUtil {
	private ConversionUtil() {
		// Static access only
	}

	public static LocalDateTime millisecondsToLocalDateTime(final long ms) {
		return Instant.ofEpochMilli(ms).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
}
