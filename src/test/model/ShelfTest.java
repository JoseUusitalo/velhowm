package test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import velho.controller.DatabaseController;
import velho.model.ProductBox;
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
	// Made up constants.
	private static final String SHELF_0_LEVEL_1_SLOT_1 = "S0-1-01";
	private static final String SHELF_1_LEVEL_3_SLOT_16 = "S1-3-16";

	// Database constants.
	private static Shelf shelf_FREE_LVL_2;
	private static final int SHELF_FREE_LVL_2_ID = 4;
	private static final int SHELF_FREE_LVL_2_ID_LEVELS = 2;
	private static Shelf emptyShelf_1_0_to_1_1;
	private static final int EMPTYSHELF_1_0_to_1_1_ID = 5;
	private static Shelf fullShelf_LVL_1_SLTIDX_0;
	private static final int FULLSHELF_LVL_1_SLTIDX_0_ID = 1;

	private static ProductBox EMPTY_BOX;
	private ProductBox BOX_1;
	private ProductBox BOX_2;
	private static final int BOX_1_2_PRODUCT_COUNT = 2;

	@Before
	public final void createShelf()
	{
		System.out.println("--Start--");
		// Get shelves before each test.
		try
		{
			assertTrue(DatabaseController.link());
			assertTrue(DatabaseController.initializeDatabase());
		}
		catch (ClassNotFoundException | ExistingDatabaseLinkException e)
		{
			e.printStackTrace();
		}
		catch (NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}

		DatabaseController.loadData();

		try
		{
			shelf_FREE_LVL_2 = DatabaseController.getShelfByID(SHELF_FREE_LVL_2_ID, false);
			fullShelf_LVL_1_SLTIDX_0 = DatabaseController.getShelfByID(FULLSHELF_LVL_1_SLTIDX_0_ID, false);
			emptyShelf_1_0_to_1_1 = DatabaseController.getShelfByID(EMPTYSHELF_1_0_to_1_1_ID, false);
			EMPTY_BOX = DatabaseController.getProductBoxByID(23);
			BOX_1 = DatabaseController.getProductBoxByID(21);
			BOX_2 = DatabaseController.getProductBoxByID(22);

			System.out.println("Initial state");
			System.out.println(shelf_FREE_LVL_2);
			System.out.println(fullShelf_LVL_1_SLTIDX_0);
			System.out.println(emptyShelf_1_0_to_1_1);
			System.out.println("--Test--");
		}
		catch (NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}
	}

	@After
	public final void unlinkDatabase() throws NoDatabaseLinkException
	{
		DatabaseController.unlink();
		System.out.println("--Done--");
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testCreateInvalid()
	{
		@SuppressWarnings("unused")
		final Shelf s = new Shelf(-1, -1, 2, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testCreateInvalid2()
	{
		@SuppressWarnings("unused")
		final Shelf s = new Shelf(1, 1, -2, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testCreateInvalid3()
	{
		@SuppressWarnings("unused")
		final Shelf s = new Shelf(0, 1, 2, -3);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testTokenizeSlotID_Null()
	{
		Shelf.tokenizeShelfSlotID(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testTokenizeSlotID_Empty()
	{
		Shelf.tokenizeShelfSlotID("");
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testTokenizeSlotID_Invalid()
	{
		Shelf.tokenizeShelfSlotID("not a shelf slot id");
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testTokenizeSlotID_Invalid_String1()
	{
		Shelf.tokenizeShelfSlotID("SA-1-1");
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testTokenizeSlotID_Invalid_String2()
	{
		Shelf.tokenizeShelfSlotID("S1-B-1");
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testTokenizeSlotID_Invalid_String3()
	{
		Shelf.tokenizeShelfSlotID("S1-1-C");
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testAddToSlot_Large_Level() throws IllegalArgumentException, NoDatabaseLinkException
	{
		shelf_FREE_LVL_2.addToSlot(shelf_FREE_LVL_2.getShelfID() + "-99999-12", BOX_2);
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testAddToSlot_Large_Slot() throws IllegalArgumentException, NoDatabaseLinkException
	{
		shelf_FREE_LVL_2.addToSlot(shelf_FREE_LVL_2.getShelfID() + "-2-999999", BOX_2);
	}

	// MORE TESTS HERE

	@Test
	public final void testCoordinatesToShelfSlotID()
	{
		assertEquals("S1-2-3", Shelf.coordinatesToShelfSlotID(1, 2, 3, true));
	}

	@Test
	public final void testGetFreeShelfSlots_Empty()
	{
		assertEquals(emptyShelf_1_0_to_1_1.getShelfSlotCount(), emptyShelf_1_0_to_1_1.getFreeShelfSlots().size());
	}

	@Test
	public final void testGetFreeShelfSlots2()
	{
		final String slotid = shelf_FREE_LVL_2.getShelfID() + "-2-01";
		assertTrue(shelf_FREE_LVL_2.getFreeShelfSlots().contains(slotid));
	}

	@Test
	public final void testGetLevels()
	{
		assertEquals(SHELF_FREE_LVL_2_ID_LEVELS, shelf_FREE_LVL_2.getLevelCount());
	}

	@Test
	public final void testGetProductBoxCount_Empty()
	{
		assertEquals(0, emptyShelf_1_0_to_1_1.getProductBoxCount());
	}

	@Test
	public final void testGetProductCount_Empty()
	{
		assertEquals(0, emptyShelf_1_0_to_1_1.getProductCount());
	}

	@Test
	public final void testIsEmpty_Empty()
	{
		assertTrue(emptyShelf_1_0_to_1_1.isEmpty());
	}

	@Test
	public final void testHasFreeSpace_Empty()
	{
		assertTrue(emptyShelf_1_0_to_1_1.hasFreeSpace());
	}

	@Test
	public final void testTokenizeSlotID()
	{
		final Object[] tokens = Shelf.tokenizeShelfSlotID(SHELF_1_LEVEL_3_SLOT_16);
		final List<Object> expected = new ArrayList<Object>(Arrays.asList("S1", 3, 16));
		final List<Object> actual = new ArrayList<Object>();

		for (final Object value : tokens)
			actual.add(value);

		assertTrue(actual.containsAll(expected));
		assertEquals(3, actual.size());
	}

	@Test
	public final void testTokenizeSlotID2()
	{
		final Object[] tokens = Shelf.tokenizeShelfSlotID(SHELF_0_LEVEL_1_SLOT_1);
		final List<Object> expected = new ArrayList<Object>(Arrays.asList("S0", 1, 1));
		final List<Object> actual = new ArrayList<Object>();

		for (final Object value : tokens)
			actual.add(value);

		assertTrue(actual.containsAll(expected));
		assertEquals(3, actual.size());
	}

	@Test
	public final void testAddToSlot_EmptyBox_GetCount() throws IllegalArgumentException, NoDatabaseLinkException
	{
		final int oldBoxCount = shelf_FREE_LVL_2.getProductBoxCount();
		final int oldProductCount = shelf_FREE_LVL_2.getProductCount();
		final String slotid = shelf_FREE_LVL_2.getShelfID() + "-2-03";

		assertTrue(shelf_FREE_LVL_2.addToSlot(slotid, EMPTY_BOX));
		assertEquals(oldBoxCount + 1, shelf_FREE_LVL_2.getProductBoxCount());
		assertEquals(oldProductCount, shelf_FREE_LVL_2.getProductCount());
	}

	@Test
	public final void testAddToSlot() throws IllegalArgumentException, NoDatabaseLinkException
	{
		final String slotid = shelf_FREE_LVL_2.getShelfID() + "-2-12";
		final int oldBoxCount = shelf_FREE_LVL_2.getProductBoxCount();
		final int oldProductCount = shelf_FREE_LVL_2.getProductCount();

		assertTrue(shelf_FREE_LVL_2.addToSlot(slotid, BOX_1));
		assertEquals(oldBoxCount + 1, shelf_FREE_LVL_2.getProductBoxCount());
		assertTrue(shelf_FREE_LVL_2.getShelfSlotBoxes(slotid).contains(BOX_1));
		assertEquals(oldProductCount + BOX_1_2_PRODUCT_COUNT, shelf_FREE_LVL_2.getProductCount());
	}

	@Test
	public final void testRemoveFromSlot() throws IllegalArgumentException, NoDatabaseLinkException
	{
		final String slotid = shelf_FREE_LVL_2.getShelfID() + "-2-10";
		assertTrue(shelf_FREE_LVL_2.addToSlot(slotid, BOX_2));
		assertTrue(shelf_FREE_LVL_2.getShelfSlotBoxes(slotid).contains(BOX_2));

		final int oldBoxCount = shelf_FREE_LVL_2.getProductBoxCount();
		final int oldProductCount = shelf_FREE_LVL_2.getProductCount();

		assertTrue(shelf_FREE_LVL_2.removeFromSlot(BOX_2));

		assertFalse(shelf_FREE_LVL_2.getShelfSlotBoxes(slotid).contains(BOX_2));
		assertEquals(oldBoxCount - 1, shelf_FREE_LVL_2.getProductBoxCount());
		assertEquals(oldProductCount - BOX_1_2_PRODUCT_COUNT, shelf_FREE_LVL_2.getProductCount());
	}

	@Test
	public final void testHasFreeSpace() throws IllegalArgumentException, NoDatabaseLinkException
	{
		assertTrue(emptyShelf_1_0_to_1_1.hasFreeSpace());
		assertTrue(emptyShelf_1_0_to_1_1.isEmpty());

		assertTrue(emptyShelf_1_0_to_1_1.addToSlot(emptyShelf_1_0_to_1_1.getShelfID() + "-1-0", BOX_1));

		assertTrue(emptyShelf_1_0_to_1_1.hasFreeSpace());
		assertFalse(emptyShelf_1_0_to_1_1.isEmpty());

		assertTrue(emptyShelf_1_0_to_1_1.addToSlot(emptyShelf_1_0_to_1_1.getShelfID() + "-1-1", BOX_2));

		assertFalse(emptyShelf_1_0_to_1_1.hasFreeSpace());
	}

	@Test
	public final void testToString() throws IllegalArgumentException
	{
		assertEquals("[1] Lvls: 1, Slt/Lvl: 4, Box/Slt: 1, Boxs: 4, Slts: 4, Free: 0", fullShelf_LVL_1_SLTIDX_0.toString());
	}

	@Test
	public final void testGetDatabaseID()
	{
		assertEquals(SHELF_FREE_LVL_2_ID, shelf_FREE_LVL_2.getDatabaseID());
	}

	@Test
	public final void testAddToSlot_Full() throws IllegalArgumentException, NoDatabaseLinkException
	{
		assertTrue(emptyShelf_1_0_to_1_1.addToSlot(emptyShelf_1_0_to_1_1.getShelfID() + "-1-0", BOX_1));
		assertFalse(emptyShelf_1_0_to_1_1.addToSlot(emptyShelf_1_0_to_1_1.getShelfID() + "-1-0", BOX_2));
	}
}
