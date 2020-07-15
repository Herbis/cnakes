package lv.herbis.cnakes.tools;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DataUtilTest {

	@Test
	public void testRemoveItemsAboveIndexWithValidSize() {
		final List<String> list = createSmallList();

		final int startListSize = list.size();
		final int maxIndex = 10;
		DataUtil.removeItemsAboveIndex(list, maxIndex);
		assertEquals(String.format("List with maxIndex %d should not change in size.", maxIndex), startListSize,
					 list.size());

		DataUtil.removeItemsAboveIndex(list, list.size());
		assertEquals("List with maxIndex set as list size, should not change in size.", startListSize, list.size());
	}

	@Test
	public void testRemoveItemsAboveIndexWithEmptyList() {

		final int maxIndex = 5;
		DataUtil.removeItemsAboveIndex(null, maxIndex);

		final List<String> list = new ArrayList<>();
		DataUtil.removeItemsAboveIndex(list, maxIndex);

		assertEquals(String.format("Empty List with maxIndex %d should not change in size.", maxIndex), 0, list.size());
	}

	@Test
	public void testRemoveItemsAboveIndexWithInvalidMaxIndex() {
		final List<String> list = createSmallList();
		final int startListSize = list.size();

		final int negativeIndex = -5;
		DataUtil.removeItemsAboveIndex(list, negativeIndex);
		assertEquals("List with negative maxIndex should not change in size.", startListSize, list.size());
	}

	@Test
	public void testRemoveItemsAboveIndexWithListTooBig() {
		final List<String> list = createSmallList();
		final int startListSize = list.size();
		final int maxIndex = startListSize - 2;
		assertTrue("Max index for this test should be above 0.", maxIndex > 0);

		DataUtil.removeItemsAboveIndex(list, maxIndex);
		assertEquals(String.format("List with maxIndex %s should change in size appropriately.", maxIndex),
					 maxIndex + 1L, list.size());

		final List<String> newList = createSmallList();
		DataUtil.removeItemsAboveIndex(newList, 0);
		assertEquals("List with maxIndex zero should have one item left.", 1, newList.size());
	}


	private static List<String> createSmallList() {
		final List<String> list = new ArrayList<>();
		list.add("Test1");
		list.add("Test2");
		list.add("Test3");
		list.add("Test4");

		return list;
	}
}