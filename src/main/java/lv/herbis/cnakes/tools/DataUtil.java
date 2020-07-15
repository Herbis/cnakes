package lv.herbis.cnakes.tools;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Utility class to help manipulate data.
 */
public class DataUtil {
	private static final Logger LOG = LogManager.getLogger(DataUtil.class);

	private DataUtil() {
		// Only static access.
	}

	public static void removeItemsAboveIndex(final List<?> list, final int maxIndex)
	{
		if (list == null || maxIndex < 0) {
			if (list == null) {
				LOG.warn("Can't remove items above index from list, because list is null.");
			} else {
				LOG.warn("Can't remove items above index from list, because max index ({}) is below 0.", maxIndex);
			}
			return;
		}

		final int listLastItemIndex = list.size() - 1;
		if (listLastItemIndex > maxIndex) {
			list.subList(maxIndex + 1, listLastItemIndex + 1).clear();
		}
	}
}
