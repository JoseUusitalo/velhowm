package test.controller;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import velho.controller.ListController;

/**
 * Tests for the {@link ListController} class.
 *
 * @author Jose Uusitalo
 */
public class ListControllerTest
{
	private static final String PRODUCT_1 = "Product 1";
	private static final String PRODUCT_2 = "Product 2";
	private static final String PRODUCT_3 = "Product 3";
	private static final String PRODUCT_4 = "Product 4";
	private static final String PRODUCT_TWO_COLONS = "A Very Long Product Name: With Two: Colons";
	private static final String PRODUCT_WITH_COLON = "Product Name: With a Single Colon";

	private ListController listController = new ListController(null);

	@Test
	public final void testSearchByProductList()
	{
		final Map<String, Integer> expected = new LinkedHashMap<String, Integer>();
		expected.put(PRODUCT_1, 1);
		expected.put(PRODUCT_2, 2);
		expected.put(PRODUCT_3, 3);
		expected.put(PRODUCT_4, 1);
		// @formatter:off
		final String string = "1: " + PRODUCT_1 + "\n"
							+ "2: " + PRODUCT_2 + "           \n"
							+ "3: " + PRODUCT_3 + "\n"
							+ PRODUCT_4 + "\n";
		// @formatter:on

		assertEquals(expected, listController.searchByProductList(string));
	}

	@Test
	public final void testSearchByProductList_Duplicates()
	{
		final Map<String, Integer> expected = new LinkedHashMap<String, Integer>();
		expected.put(PRODUCT_1, 8);
		expected.put(PRODUCT_2, 2);
		// @formatter:off
		final String string = PRODUCT_1 + "\n"
							+ "2: " + PRODUCT_2 + "           \n"
							+ "3: " + PRODUCT_1 + "\n"
							+ "4: " + PRODUCT_1 + "\n";
		// @formatter:on
		assertEquals(expected, listController.searchByProductList(string));
	}

	@Test
	public final void testSearchByProductList_Duplicates_Spaces()
	{
		final Map<String, Integer> expected = new LinkedHashMap<String, Integer>();
		expected.put(PRODUCT_1, 8);
		expected.put(PRODUCT_2, 2);
		// @formatter:off
		final String string = PRODUCT_1 + "\n"
							+ "2   : " + PRODUCT_2 + "           \n"
							+ "    3: " + PRODUCT_1 + "\n"
							+ "   4   : " + PRODUCT_1 + "\n";
		// @formatter:on
		assertEquals(expected, listController.searchByProductList(string));
	}

	@Test
	public final void testSearchByProductList_Nulls()
	{
		final Map<String, Integer> expected = new LinkedHashMap<String, Integer>();
		expected.put(PRODUCT_1, 1);
		expected.put(PRODUCT_2, 1);
		// @formatter:off
		final String string = "\n"
							+ PRODUCT_1 + "\n"
							+ "\n"
							+ "\n"
							+ PRODUCT_2;
		// @formatter:on
		assertEquals(expected, listController.searchByProductList(string));
	}

	@Test
	public final void testSearchByProductList_Spaces()
	{
		final Map<String, Integer> expected = new LinkedHashMap<String, Integer>();
		expected.put(PRODUCT_1, 1);
		expected.put(PRODUCT_2, 1);
		// @formatter:off
		final String string = "   \n"
							+ PRODUCT_1 + "\n"
							+ "   \n"
							+ " \n"
							+ PRODUCT_2;
		// @formatter:on
		assertEquals(expected, listController.searchByProductList(string));
	}

	@Test
	public final void testSearchByProductList_Spaces2()
	{
		final Map<String, Integer> expected = new LinkedHashMap<String, Integer>();
		expected.put(PRODUCT_1, 1);
		expected.put(PRODUCT_2, 1);
		// @formatter:off
		final String string = "2:   \n"
							+ PRODUCT_1 + "\n"
							+ "3:   \n"
							+ "         12:        \n"
							+ PRODUCT_2;
		// @formatter:on
		assertEquals(expected, listController.searchByProductList(string));
	}

	@Test
	public final void testSearchByProductList_Spaces3()
	{
		final Map<String, Integer> expected = new LinkedHashMap<String, Integer>();
		expected.put(PRODUCT_1, 10);
		expected.put(PRODUCT_2, 2);
		// @formatter:off
		final String string = "2:   \n"
							+ "      10:" + PRODUCT_1 + "      \n"
							+ "3:   \n"
							+ "         12:        \n"
							+ "2: " + PRODUCT_2 + "           ";
		// @formatter:on
		assertEquals(expected, listController.searchByProductList(string));
	}

	@Test
	public final void testSearchByProductList_NameWithColon()
	{
		final Map<String, Integer> expected = new LinkedHashMap<String, Integer>();
		expected.put("Product: A Colon!", 1);
		expected.put(PRODUCT_WITH_COLON, 10);
		expected.put(PRODUCT_TWO_COLONS, 1);

		// @formatter:off
		final String string = "Product: A Colon!" + "\n"
							+ "10: " + PRODUCT_WITH_COLON + "\n"
							+ PRODUCT_TWO_COLONS + "\n";
		// @formatter:on

		assertEquals(expected, listController.searchByProductList(string));
	}
}
