package test.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import velho.controller.DatabaseController;
import velho.controller.SearchController;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseException;
import velho.model.exceptions.NoDatabaseLinkException;

/**
 * Tests for the {@link SearchController} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class SearchControllerTest
{
	private static final Integer PRODUCT_1 = new Integer(1);
	private static final Integer PRODUCT_2 = new Integer(2);
	private static final Integer PRODUCT_3 = new Integer(3);
	private static final Integer PRODUCT_4 = new Integer(4);
	private static final String PRODUCT_TWO_COLONS = "A Very Long Product Name: With Two: Colons";
	private static final String PRODUCT_WITH_COLON = "Product Name: With a Single Colon";

	private SearchController searchController = new SearchController(null);

	@BeforeClass
	public static final void initializeDatabase() throws NoDatabaseException, ParseException
	{
		DatabaseController.resetDatabase();
	}

	@Before
	public final void linkDatabase() throws ClassNotFoundException, ExistingDatabaseLinkException
	{
		if (!DatabaseController.isLinked())
			DatabaseController.link();
	}

	@Test
	public final void testSearchByProductList() throws NoDatabaseLinkException
	{
		final Map<Integer, Integer> expected = new LinkedHashMap<Integer, Integer>();
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

		final Map<Integer, Integer> actual = searchController.searchByProductList(string);
		DatabaseController.unlink();

		assertEquals(expected, actual);
	}

	@Test
	public final void testSearchByProductList_Duplicates() throws NoDatabaseLinkException
	{
		final Map<Integer, Integer> expected = new LinkedHashMap<Integer, Integer>();
		expected.put(PRODUCT_1, 8);
		expected.put(PRODUCT_2, 2);
		// @formatter:off
		final String string = PRODUCT_1 + "\n"
							+ "2: " + PRODUCT_2 + "           \n"
							+ "3: " + PRODUCT_1 + "\n"
							+ "4: " + PRODUCT_1 + "\n";
		// @formatter:on
		final Map<Integer, Integer> actual = searchController.searchByProductList(string);
		DatabaseController.unlink();

		assertEquals(expected, actual);
	}

	@Test
	public final void testSearchByProductList_Duplicates_Spaces() throws NoDatabaseLinkException
	{
		final Map<Integer, Integer> expected = new LinkedHashMap<Integer, Integer>();
		expected.put(PRODUCT_1, 8);
		expected.put(PRODUCT_2, 2);
		// @formatter:off
		final String string = PRODUCT_1 + "\n"
							+ "2   : " + PRODUCT_2 + "           \n"
							+ "    3: " + PRODUCT_1 + "\n"
							+ "   4   : " + PRODUCT_1 + "\n";
		// @formatter:on
		final Map<Integer, Integer> actual = searchController.searchByProductList(string);
		DatabaseController.unlink();

		assertEquals(expected, actual);
	}

	@Test
	public final void testSearchByProductList_Nulls() throws NoDatabaseLinkException
	{
		final Map<Integer, Integer> expected = new LinkedHashMap<Integer, Integer>();
		expected.put(PRODUCT_1, 1);
		expected.put(PRODUCT_2, 1);
		// @formatter:off
		final String string = "\n"
							+ PRODUCT_1 + "\n"
							+ "\n"
							+ "\n"
							+ PRODUCT_2;
		// @formatter:on
		final Map<Integer, Integer> actual = searchController.searchByProductList(string);
		DatabaseController.unlink();

		assertEquals(expected, actual);
	}

	@Test
	public final void testSearchByProductList_Spaces() throws NoDatabaseLinkException
	{
		final Map<Integer, Integer> expected = new LinkedHashMap<Integer, Integer>();
		expected.put(PRODUCT_1, 1);
		expected.put(PRODUCT_2, 1);
		// @formatter:off
		final String string = "   \n"
							+ PRODUCT_1 + "\n"
							+ "   \n"
							+ " \n"
							+ PRODUCT_2;
		// @formatter:on
		final Map<Integer, Integer> actual = searchController.searchByProductList(string);
		DatabaseController.unlink();

		assertEquals(expected, actual);
	}

	@Test
	public final void testSearchByProductList_Spaces2() throws NoDatabaseLinkException
	{
		final Map<Integer, Integer> expected = new LinkedHashMap<Integer, Integer>();
		expected.put(PRODUCT_1, 1);
		expected.put(PRODUCT_2, 1);
		// @formatter:off
		final String string = "2:   \n"
							+ PRODUCT_1 + "\n"
							+ "3:   \n"
							+ "         12:        \n"
							+ PRODUCT_2;
		// @formatter:on

		final Map<Integer, Integer> actual = searchController.searchByProductList(string);
		DatabaseController.unlink();

		assertEquals(expected, actual);
	}

	@Test
	public final void testSearchByProductList_Spaces3() throws NoDatabaseLinkException
	{
		final Map<Integer, Integer> expected = new LinkedHashMap<Integer, Integer>();
		expected.put(PRODUCT_1, 10);
		expected.put(PRODUCT_2, 2);
		// @formatter:off
		final String string = "2:   \n"
							+ "      10:" + PRODUCT_1 + "      \n"
							+ "3:   \n"
							+ "         12:        \n"
							+ "2: " + PRODUCT_2 + "           ";
		// @formatter:on
		final Map<Integer, Integer> actual = searchController.searchByProductList(string);

		DatabaseController.unlink();

		assertEquals(expected, actual);
	}

	@Test
	public final void testParse_NameWithColon() throws NoDatabaseLinkException
	{
		final Object[] expected = new Object[2];
		expected[0] = 1;
		expected[1] = "Product: A Colon!";

		final Object[] actual = SearchController.parseProductLine("Product: A Colon!    ");
		DatabaseController.unlink();

		System.out.println("Expected:\t" + Arrays.asList(expected));
		System.out.println("Actual:\t\t" + Arrays.asList(actual));
		assertTrue(Arrays.equals(expected, actual));
	}

	@Test
	public final void testParse_NameWithColon2() throws NoDatabaseLinkException
	{
		final Object[] expected = new Object[2];
		expected[0] = 10;
		expected[1] = PRODUCT_WITH_COLON;

		final Object[] actual = SearchController.parseProductLine("   10   :   " + PRODUCT_WITH_COLON);
		DatabaseController.unlink();

		System.out.println("Expected:\t" + Arrays.asList(expected));
		System.out.println("Actual:\t\t" + Arrays.asList(actual));
		assertTrue(Arrays.equals(expected, actual));
	}

	@Test
	public final void testParse_NameWithColon3() throws NoDatabaseLinkException
	{
		final Object[] expected = new Object[2];
		expected[0] = 1;
		expected[1] = PRODUCT_TWO_COLONS;

		final Object[] actual = SearchController.parseProductLine("     " + PRODUCT_TWO_COLONS);
		DatabaseController.unlink();

		System.out.println("Expected:\t" + Arrays.asList(expected));
		System.out.println("Actual:\t\t" + Arrays.asList(actual));
		assertTrue(Arrays.equals(expected, actual));
	}
}
