package test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import velho.controller.DatabaseController;
import velho.model.Product;
import velho.model.ProductBox;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.model.ProductType;
import velho.model.Shelf;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseLinkException;

/**
 * Tests for the {@link Shelf} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class ShelfTest
{
	private static final int SHELF_ID_0 = 0;
	private static final int SHELF_LEVELS = 3;
	private static final int SHELF_SLOTS = 20;
	private static final int SHELF_BOXES_PER_SLOT = 32;
	private static final String SHELF_0_LEVEL_1_SLOT_1 = "S0-1-01";
	private static final String SHELF_1_LEVEL_3_SLOT_16 = "S1-3-16";
	private static final String INVALID_SHELF_SLOT_ID = "1-1-1";

	private static final int PRODUCT_1_ID = 10045;
	private static final String PRODUCT_1_NAME = "A Test Product";
	private static final Product PRODUCT_1 = new Product(PRODUCT_1_ID, PRODUCT_1_NAME, new Date(0), new ProductBrand(-1, "Brand #1"),
			new ProductCategory(-1, "Type #1", new ProductType(-1, "Regular")), -1);

	private static final int PRODUCT_2_ID = 299;
	private static final String PRODUCT_2_NAME = "A Test Product 2";
	private static final Product PRODUCT_2 = new Product(PRODUCT_2_ID, PRODUCT_2_NAME, new Date(0), new ProductBrand(-1, "Brand #2"),
			new ProductCategory(-2, "Type #2", new ProductType(-1, "Regular")), -1);

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
	public final void createShelf() throws ClassNotFoundException, ExistingDatabaseLinkException, NoDatabaseLinkException
	{
		// Create a new shelf before each test.
		shelf = new Shelf(SHELF_ID_0, SHELF_LEVELS, SHELF_SLOTS, SHELF_BOXES_PER_SLOT);
		assertTrue(DatabaseController.link());
		assertTrue(DatabaseController.initializeDatabase());
	}

	@After
	public final void unlinkDatabase() throws NoDatabaseLinkException
	{
		DatabaseController.unlink();
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testCreateInvalid()
	{
		@SuppressWarnings("unused")
		Shelf s = new Shelf(-1, -1, 2, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testCreateInvalid2()
	{
		@SuppressWarnings("unused")
		Shelf s = new Shelf(1, 1, -2, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testCreateInvalid3()
	{
		@SuppressWarnings("unused")
		Shelf s = new Shelf(0, 1, 2, -3);
	}

	@Test
	public final void testGetFreeShelfSlots_Empty()
	{
		assertEquals(shelf.getShelfSlotCount(), shelf.getFreeShelfSlots().size());
	}

	@Test
	public final void testGetFreeShelfSlots2()
	{
		final String slotid = shelf.getShelfID() + "-3-19";
		assertTrue(shelf.getFreeShelfSlots().contains(slotid));
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
	public final void testTokenizeSlotID()
	{
		Object[] tokens = Shelf.tokenizeShelfSlotID(SHELF_1_LEVEL_3_SLOT_16);
		List<Object> expected = new ArrayList<Object>(Arrays.asList("S1", 3, 16));
		List<Object> actual = new ArrayList<Object>();

		for (Object value : tokens)
			actual.add(value);

		assertTrue(actual.containsAll(expected));
		assertEquals(3, actual.size());
	}

	@Test
	public final void testTokenizeSlotID2()
	{
		Object[] tokens = Shelf.tokenizeShelfSlotID(SHELF_0_LEVEL_1_SLOT_1);
		List<Object> expected = new ArrayList<Object>(Arrays.asList("S0", 1, 1));
		List<Object> actual = new ArrayList<Object>();

		for (Object value : tokens)
			actual.add(value);

		assertTrue(actual.containsAll(expected));
		assertEquals(3, actual.size());
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testTokenizeSlotID_Null()
	{
		Shelf.tokenizeShelfSlotID(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testTokenizeSlotID_Invalid()
	{
		Shelf.tokenizeShelfSlotID("not a shelf slot id");
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testTokenizeSlotID_Malformed()
	{
		Shelf.tokenizeShelfSlotID("SA-1-1");
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testTokenizeSlotID_Malformed2()
	{
		Shelf.tokenizeShelfSlotID("S1-B-1");
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testTokenizeSlotID_Malformed3()
	{
		Shelf.tokenizeShelfSlotID("S1-1-C");
	}

	@Test
	public final void testAddToSlot_EmptyBox() throws IllegalArgumentException, NoDatabaseLinkException
	{
		final String slotid = shelf.getShelfID() + "-1-03";
		assertTrue(shelf.addToSlot(slotid, BOX_1_EMPTY));
		assertEquals(1, shelf.getProductBoxCount());
		assertEquals(0, shelf.getProductCount());
	}

	@Test
	public final void testAddToSlot() throws IllegalArgumentException, NoDatabaseLinkException
	{
		final String slotid = shelf.getShelfID() + "-2-12";
		assertTrue(shelf.addToSlot(slotid, BOX_2));
		assertFalse(shelf.isEmpty());
		assertEquals(1, shelf.getProductBoxCount());
		assertTrue(shelf.getShelfSlotBoxes(slotid).contains(BOX_2));
		assertEquals(BOX_2_COUNT, shelf.getProductCount());
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testAddToSlot_Invalid2() throws IllegalArgumentException, NoDatabaseLinkException
	{
		shelf.addToSlot("S1-NOPE-2", BOX_2);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testAddToSlot_Invalid() throws IllegalArgumentException, NoDatabaseLinkException
	{
		shelf.addToSlot(INVALID_SHELF_SLOT_ID, BOX_2);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testAddToSlot_Invalid_Shelf() throws IllegalArgumentException, NoDatabaseLinkException
	{
		final String slotid = (shelf.getShelfID() + 1) + "-2-12";
		shelf.addToSlot(slotid, BOX_2);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testAddToSlot_Invalid_Level() throws IllegalArgumentException, NoDatabaseLinkException
	{
		final String slotid = shelf.getShelfID() + "-99999-12";
		shelf.addToSlot(slotid, BOX_2);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testAddToSlot_Invalid_Slot() throws IllegalArgumentException, NoDatabaseLinkException
	{
		final String slotid = shelf.getShelfID() + "-2-99999999";
		shelf.addToSlot(slotid, BOX_2);
	}

	@Test
	public final void testRemoveFromSlot() throws IllegalArgumentException, NoDatabaseLinkException
	{
		final String slotid = shelf.getShelfID() + "-2-12";
		assertTrue(shelf.addToSlot(slotid, BOX_2));
		assertTrue(shelf.getShelfSlotBoxes(slotid).contains(BOX_2));

		assertTrue(shelf.removeFromSlot(BOX_2));

		assertFalse(shelf.getShelfSlotBoxes(slotid).contains(BOX_2));
		assertTrue(shelf.isEmpty());
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testRemoveFromSlot_Invalid_Shelf() throws IllegalArgumentException, NoDatabaseLinkException
	{
		shelf.removeFromSlot(BOX_2);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testGetShelfSlotBoxes_Invalid_Slot()
	{
		shelf.getShelfSlotBoxes(shelf.getShelfID() + "-1-999999999");
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testGetShelfSlotBoxes_Invalid_Slot2()
	{
		shelf.getShelfSlotBoxes(shelf.getShelfID() + "-1-qwe");
	}

	@Test
	public final void testHasFreeSpace() throws IllegalArgumentException, NoDatabaseLinkException
	{
		final Shelf fullShelf = new Shelf(123, 1, 1, 1);
		assertTrue(fullShelf.addToSlot(fullShelf.getShelfID() + "-1-0", BOX_2));
		assertFalse(fullShelf.hasFreeSpace());
	}

	@Test
	public final void testToString() throws IllegalArgumentException, NoDatabaseLinkException
	{
		final Shelf fullShelf = new Shelf(123, 1, 1, 1);
		assertTrue(fullShelf.addToSlot(fullShelf.getShelfID() + "-1-0", BOX_2));
		assertEquals("[123] Lvls: 1, Slt/Lvl: 1, Box/Slt: 1, Boxs: 1, Slts: 1, Free: 0", fullShelf.toString());
	}

	@Test
	public final void testCoordinatesToShelfSlotID()
	{
		assertEquals("S1-2-3", Shelf.coordinatesToShelfSlotID(1, 2, 3, true));
	}

	@Test
	public final void testGetDatabaseID()
	{
		assertEquals(SHELF_ID_0, shelf.getDatabaseID());
	}

	@Test
	public final void testAddToSlot_Full() throws IllegalArgumentException, NoDatabaseLinkException
	{
		final Shelf fullShelf = new Shelf(123, 1, 1, 1);
		assertTrue(fullShelf.addToSlot(fullShelf.getShelfID() + "-1-0", BOX_2));
		assertFalse(fullShelf.addToSlot(fullShelf.getShelfID() + "-1-0", BOX_2));
	}

	@Test
	public final void testRemoveFromSlot_null() throws IllegalArgumentException, NoDatabaseLinkException
	{
		final Shelf smallShelf = new Shelf(123, 1, 1, 1);
		assertFalse(smallShelf.removeFromSlot(null));
	}
}
