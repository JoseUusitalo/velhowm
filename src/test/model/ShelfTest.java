package test.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import velho.model.Product;
import velho.model.ProductBox;
import velho.model.ProductBrand;
import velho.model.ProductType;
import velho.model.Shelf;

/**
 * Tests for the {@link Shelf} classhelf.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class ShelfTest
{
	private static final int SHELF_LEVELS = 3;
	private static final int SHELF_SLOTS = 20;
	private static final int SHELF_BOXES_PER_SLOT = 32;
	private static final String SHELF_0_LEVEL_1_SLOT_1 = "S0-1-01";
	private static final String SHELF_1_LEVEL_3_SLOT_16 = "S1-3-16";

	private static final int PRODUCT_1_ID = 10045;
	private static final String PRODUCT_1_NAME = "A Test Product";
	private static final Product PRODUCT_1 = new Product(PRODUCT_1_NAME, new Date(0), PRODUCT_1_ID, new ProductBrand("Brand #1"), new ProductType("Type #1"));

	private static final int PRODUCT_2_ID = 299;
	private static final String PRODUCT_2_NAME = "A Test Product";
	private static final Product PRODUCT_2 = new Product(PRODUCT_2_NAME, new Date(0), PRODUCT_2_ID, new ProductBrand("Brand #2"), new ProductType("Type #2"));

	private static final int BOX_1_ID = 11;
	private static final int BOX_1_MAX_SIZE = 10;
	private static final int BOX_1_COUNT = 0;
	private static final ProductBox BOX_1_EMPTY = new ProductBox(BOX_1_ID, BOX_1_MAX_SIZE, PRODUCT_1, BOX_1_COUNT);

	private static final int BOX_2_ID = 22;
	private static final int BOX_2_MAX_SIZE = 35;
	private static final int BOX_2_COUNT = 20;
	private static final ProductBox BOX_2 = new ProductBox(BOX_2_ID, BOX_2_MAX_SIZE, PRODUCT_2, BOX_2_COUNT);

	private static Shelf shelf;

	@Before
	public final void createShelf()
	{
		// Create a new shelf before each test.
		shelf = new Shelf(SHELF_LEVELS, SHELF_SLOTS, SHELF_BOXES_PER_SLOT);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public final void testCreateInvalid()
	{
		Shelf s = new Shelf(-1, 2, 3);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public final void testCreateInvalid2()
	{
		Shelf s = new Shelf(1, -2, 3);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public final void testCreateInvalid3()
	{
		Shelf s = new Shelf(1, 2, -3);
	}

	@Test
	public final void testGetShelfID_Empty()
	{
		assertEquals("S4", shelf.getShelfID());
	}

	@Test
	public final void testGetFreeShelfSlots_Empty()
	{
		assertEquals(shelf.getShelfSlotCount(), shelf.getFreeShelfSlots().size());
	}

	@Test
	public final void testGetLevels_Empty()
	{
		assertEquals(SHELF_LEVELS, shelf.getLevels());
	}

	@Test
	public final void testGetProductBoxCount_Empty()
	{
		assertEquals(0, shelf.getProductBoxCount());
	}

	@Test
	public final void testGetProductCount_Empty()
	{
		assertEquals(0, shelf.getProductCount());
	}

	@Test
	public final void testIsEmpty_Empty()
	{
		assertTrue(shelf.isEmpty());
	}

	@Test
	public final void testHasFreeSpace_Empty()
	{
		assertTrue(shelf.hasFreeSpace());
	}

	@Test
	public final void testSlotIDTokenizer()
	{
		int[] tokens = Shelf.shelfSlotIDTokenizer(SHELF_1_LEVEL_3_SLOT_16);
		List<Integer> expected = new ArrayList<Integer>(Arrays.asList(1, 3, 16));
		List<Integer> actual = new ArrayList<Integer>();

		for (int value : tokens)
			actual.add(value);

		assertTrue(actual.containsAll(expected));
		assertEquals(3, actual.size());
	}

	@Test
	public final void testSlotIDTokenizer2()
	{
		int[] tokens = Shelf.shelfSlotIDTokenizer(SHELF_0_LEVEL_1_SLOT_1);
		List<Integer> expected = new ArrayList<Integer>(Arrays.asList(0, 1, 1));
		List<Integer> actual = new ArrayList<Integer>();

		for (int value : tokens)
			actual.add(value);

		assertTrue(actual.containsAll(expected));
		assertEquals(3, actual.size());
	}

	@Test
	public final void testAddToSlot_EmptyBox()
	{
		final String slotid = shelf.getShelfID() + "-1-03";
		assertTrue(shelf.addToSlot(slotid, BOX_1_EMPTY));
		System.out.println(shelf.getFreeShelfSlots());
		assertTrue(shelf.getFreeShelfSlots().contains(slotid));
	}

	@Test
	public final void testAddToSlot()
	{
		final String slotid = shelf.getShelfID() + "-2-12";
		assertTrue(shelf.addToSlot(slotid, BOX_2));
		assertFalse(shelf.isEmpty());
		assertEquals(1, shelf.getProductBoxCount());
		assertTrue(shelf.getShelfSlotBoxes(slotid).contains(BOX_2));
	}
}
