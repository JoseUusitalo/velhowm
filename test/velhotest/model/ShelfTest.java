package velhotest.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import velho.controller.database.DatabaseController;
import velho.controller.database.LogDatabaseController;
import velho.model.ProductBox;
import velho.model.Shelf;

/**
 * Tests for the {@link Shelf} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class ShelfTest
{
	/**
	 * A non-existent shelf slot.
	 */
	private static final String SHELF_1_LEVEL_3_SLOT_16 = "S1-3-16";

	// Database constants.
	private static Shelf shelf_FREE_LVL_2;
	private static final int SHELF_FREE_LVL_2_ID = 4;
	private static final int SHELF_FREE_LVL_2_ID_LEVELS = 2;

	private static Shelf emptyShelf_1_1_to_1_2;
	private static final int EMPTYSHELF_1_1_to_1_2_ID = 5;

	private static Shelf fullShelf_LVL_1_SLTPOS_1;
	private static final int FULLSHELF_LVL_1_SLTPOS_1_ID = 1;

	private static ProductBox EMPTY_BOX;
	private static ProductBox BOX_ID_21;
	private static ProductBox BOX_ID_22;
	private static final int BOX_ID_21_PRODUCT_COUNT = 2;

	/**
	 * Creates the log database if needed and connects to it.
	 * Loads the sample data into the database if it does not yet exist.
	 *
	 * @throws Exception
	 */
	@BeforeClass
	public static final void init() throws Exception
	{
		LogDatabaseController.getInstance().connectAndInitialize();
		DatabaseController.getInstance().link();
		DatabaseController.getInstance().loadSampleData();

		fullShelf_LVL_1_SLTPOS_1 = DatabaseController.getInstance().getShelfByID(FULLSHELF_LVL_1_SLTPOS_1_ID);
		shelf_FREE_LVL_2 = DatabaseController.getInstance().getShelfByID(SHELF_FREE_LVL_2_ID);
		emptyShelf_1_1_to_1_2 = DatabaseController.getInstance().getShelfByID(EMPTYSHELF_1_1_to_1_2_ID);
		EMPTY_BOX = DatabaseController.getInstance().getProductBoxByID(23);
		BOX_ID_21 = DatabaseController.getInstance().getProductBoxByID(21);
		BOX_ID_22 = DatabaseController.getInstance().getProductBoxByID(22);

		System.out.println("--Start--");
		System.out.println("Initial state");
		System.out.println(fullShelf_LVL_1_SLTPOS_1);
		System.out.println(shelf_FREE_LVL_2);
		System.out.println(emptyShelf_1_1_to_1_2);
		System.out.println("------beforeclass----------\n\n\n");
	}

	/**
	 * Unlinks from both databases.
	 */
	@AfterClass
	public static final void unlinkDatabases() throws Exception
	{
		DatabaseController.getInstance().unlink();
		LogDatabaseController.getInstance().unlink();
	}

	/**
	 * Tests that shelf requires level count to be positive and non-zero.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testCreateInvalid_ShelfLevelCount_Negative()
	{
		@SuppressWarnings("unused")
		final Shelf s = new Shelf(0, -1);
	}

	/**
	 * Tests that shelf requires level count to be positive and non-zero.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testCreateInvalid_ShelfLevelCount_Zero()
	{
		@SuppressWarnings("unused")
		final Shelf s = new Shelf(0, 0);
	}

	/**
	 * Tests that <code>null</code> is not a valid shelf slot ID string.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testTokenizeSlotID_Null()
	{
		Shelf.tokenizeShelfSlotID(null);
	}

	/**
	 * Tests that an empty string is not a valid shelf slot ID string.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testTokenizeSlotID_Empty()
	{
		Shelf.tokenizeShelfSlotID("");
	}

	/**
	 * Tests that a string that does not follow the shelf slot ID format is not a valid shelf slot ID string.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testTokenizeSlotID_InvalidFormat()
	{
		Shelf.tokenizeShelfSlotID("not a shelf slot id");
	}

	/**
	 * Tests that a letters are not a valid shelf ID.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testTokenizeSlotID_InvalidShelfNumber()
	{
		Shelf.tokenizeShelfSlotID("SA-1-1");
	}

	/**
	 * Tests that a letters are not a valid shelf level ID.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testTokenizeSlotID_InvalidLevelNumber()
	{
		Shelf.tokenizeShelfSlotID("S1-B-1");
	}

	/**
	 * Tests that a letters are not a valid shelf slot ID.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testTokenizeSlotID_InvalidSlotNumber()
	{
		Shelf.tokenizeShelfSlotID("S1-1-C");
	}

	/**
	 * Tests that a valid shelf slot ID string is not valid when the shelf does not have the specified level.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testAddToSlot_Large_Level() throws IllegalArgumentException
	{
		shelf_FREE_LVL_2.addToSlot(shelf_FREE_LVL_2.getShelfID() + "-99999-12", BOX_ID_22);
	}

	/**
	 * Tests that a valid shelf slot ID string is not valid when the shelf does not have the specified slot.
	 */
	@Test(expected = IllegalArgumentException.class)
	public final void testAddToSlot_Large_Slot() throws IllegalArgumentException
	{
		shelf_FREE_LVL_2.addToSlot(shelf_FREE_LVL_2.getShelfID() + "-2-999999", BOX_ID_22);
	}

	/**
	 * Tests that valid integer coordinates are correctly converted to a valid shelf slot ID string.
	 */
	@Test
	public final void testCoordinatesToShelfSlotID()
	{
		assertEquals("S1-2-3", Shelf.coordinatesToShelfSlotID(1, 2, 3, true));
	}

	/**
	 * Tests that an empty shelf has the same number of free shelf slots as the shelf has slots in total.
	 */
	@Test
	public final void testGetFreeShelfSlots_Empty()
	{
		assertEquals(emptyShelf_1_1_to_1_2.getShelfSlotCount(), emptyShelf_1_1_to_1_2.getFreeShelfSlots().size());
	}

	/**
	 * Tests that a shelf whose second level is empty, has a free slot.
	 */
	@Test
	public final void testGetFreeShelfSlots2()
	{
		final String slotid = shelf_FREE_LVL_2.getShelfID() + "-2-1";

		System.out.println(shelf_FREE_LVL_2.getFreeShelfSlots());

		assertTrue(shelf_FREE_LVL_2.getFreeShelfSlots().contains(shelf_FREE_LVL_2.getShelfSlot(slotid)));
	}

	/**
	 * Tests that a specific shelf has the same number of levels are defined by the SQL sample data.
	 */
	@Test
	public final void testGetLevels()
	{
		assertEquals(SHELF_FREE_LVL_2_ID_LEVELS, shelf_FREE_LVL_2.getLevelCount());
	}

	/**
	 * Tests that an empty shelf returns an empty list of product boxes.
	 */
	@Test
	public final void testGetProductBoxCount_Empty()
	{
		assertEquals(0, emptyShelf_1_1_to_1_2.getProductBoxes().size());
	}

	/**
	 * Tests that an empty shelf has no products in it.
	 */
	@Test
	public final void testGetProductCount_Empty()
	{
		assertEquals(0, emptyShelf_1_1_to_1_2.getProductCountInBoxes());
	}

	/**
	 * Tests that an empty shelf is empty.
	 */
	@Test
	public final void testIsEmpty_Empty()
	{
		assertTrue(emptyShelf_1_1_to_1_2.isEmpty());
	}

	/**
	 * Tests that an empty shelf has free space in shelf slots.
	 */
	@Test
	public final void testHasFreeSpace_Empty()
	{
		assertTrue(emptyShelf_1_1_to_1_2.hasFreeSpace());
	}

	/**
	 * Tests that a shelf slot ID string is correctly tokenized.
	 */
	@Test
	public final void testTokenizeSlotID()
	{
		final Object[] tokens = Shelf.tokenizeShelfSlotID(SHELF_1_LEVEL_3_SLOT_16);
		final List<Object> expected = new ArrayList<Object>(Arrays.asList("S1", 3, 16));

		assertEquals(3, tokens.length);

		for (int i = 0; i < tokens.length; i++)
			assertTrue(expected.get(i).equals(tokens[i]));
	}

	/**
	 * Tests that adding an empty product box into a shelf increases the number of product boxes in the shelf but not
	 * the number of products.
	 *
	 * @throws IllegalArgumentException
	 */
	@Test
	public final void testAddToSlot_EmptyBox_GetCount() throws IllegalArgumentException
	{
		final int oldBoxCount = shelf_FREE_LVL_2.getProductBoxes().size();
		final int oldProductCount = shelf_FREE_LVL_2.getProductCountInBoxes();
		final String slotid = shelf_FREE_LVL_2.getShelfID() + "-2-3";

		assertTrue(shelf_FREE_LVL_2.addToSlot(slotid, EMPTY_BOX));
		assertEquals(oldBoxCount + 1, shelf_FREE_LVL_2.getProductBoxes().size());
		assertEquals(oldProductCount, shelf_FREE_LVL_2.getProductCountInBoxes());

		/*
		 * Rollback.
		 */

		// TODO: Refactor removing a box. This is silly.
		assertTrue(EMPTY_BOX.getShelfSlot().removeBox(EMPTY_BOX));
	}

	/**
	 * Tests that adding a product box to a shelf works as intended.
	 *
	 * @throws IllegalArgumentException
	 */
	@Test
	public final void testAddToSlot() throws IllegalArgumentException
	{
		final String slotid = shelf_FREE_LVL_2.getShelfID() + "-2-12";
		final int oldBoxCount = shelf_FREE_LVL_2.getProductBoxes().size();
		final int oldProductCount = shelf_FREE_LVL_2.getProductCountInBoxes();

		System.out.println("before " + shelf_FREE_LVL_2.getShelfSlot(slotid).getProductBoxes());
		System.out.println(shelf_FREE_LVL_2.getProductBoxes());
		System.out.println("add " + BOX_ID_21);
		System.out.println(shelf_FREE_LVL_2);

		// Add to the shelf.
		assertTrue(shelf_FREE_LVL_2.addToSlot(slotid, BOX_ID_21));

		System.out.println("after " + shelf_FREE_LVL_2.getShelfSlot(slotid).getProductBoxes());
		System.out.println(shelf_FREE_LVL_2.getProductBoxes());
		System.out.println(shelf_FREE_LVL_2);

		// Product box is in the shelf.
		assertTrue(shelf_FREE_LVL_2.getProductBoxes().contains(BOX_ID_21));

		// Product box is in the slot.
		assertTrue(shelf_FREE_LVL_2.getShelfSlot(slotid).contains(BOX_ID_21));

		// Number of product boxes in the shelf has increased by one.
		assertEquals(oldBoxCount + 1, shelf_FREE_LVL_2.getProductBoxes().size());

		// Number of products in the shelf has increased.
		assertEquals(oldProductCount + BOX_ID_21_PRODUCT_COUNT, shelf_FREE_LVL_2.getProductCountInBoxes());

		// -- Save to database --
		DatabaseController.getInstance().saveOrUpdate(shelf_FREE_LVL_2);

		// Check that database has been updated.
		shelf_FREE_LVL_2 = DatabaseController.getInstance().getShelfByID(SHELF_FREE_LVL_2_ID);

		System.out.println("From database");
		System.out.println(shelf_FREE_LVL_2);

		// Product box is in the shelf.
		assertTrue(shelf_FREE_LVL_2.getProductBoxes().contains(BOX_ID_21));

		// Product box is in the slot.
		assertTrue(shelf_FREE_LVL_2.getShelfSlot(slotid).contains(BOX_ID_21));

		// Number of product boxes in the shelf has increased by one.
		assertEquals(oldBoxCount + 1, shelf_FREE_LVL_2.getProductBoxes().size());

		// Number of products in the shelf has increased.
		assertEquals(oldProductCount + BOX_ID_21_PRODUCT_COUNT, shelf_FREE_LVL_2.getProductCountInBoxes());

		/*
		 * Manual rollback.
		 */
		System.out.println("-- ROLLBACK --");
		System.out.println(shelf_FREE_LVL_2);
		System.out.println(shelf_FREE_LVL_2.getShelfSlot(slotid).getProductBoxes());

		// Remove.
		assertTrue(BOX_ID_21.getShelfSlot().removeBox(BOX_ID_21));

		// Update database.
		DatabaseController.getInstance().saveOrUpdate(BOX_ID_21);

		// Fetch the updated shelf.
		shelf_FREE_LVL_2 = DatabaseController.getInstance().getShelfByID(SHELF_FREE_LVL_2_ID);

		// Check.
		assertFalse(shelf_FREE_LVL_2.getShelfSlot(slotid).contains(BOX_ID_21));

		// Save.
		DatabaseController.getInstance().saveOrUpdate(shelf_FREE_LVL_2);
		System.out.println(shelf_FREE_LVL_2);
		System.out.println(shelf_FREE_LVL_2.getShelfSlot(slotid).getProductBoxes());
	}

	/**
	 * Tests that removing product boxes from shelf slots works as intended.
	 *
	 * @throws IllegalArgumentException
	 */
	@Test
	public final void testRemoveFromSlot() throws IllegalArgumentException
	{
		final String slotid = shelf_FREE_LVL_2.getShelfID() + "-2-10";
		int oldBoxCount = shelf_FREE_LVL_2.getProductBoxes().size();
		int oldProductCount = shelf_FREE_LVL_2.getProductCountInBoxes();

		// Box is not in a slot.
		assertEquals(BOX_ID_21.getShelfSlot(), null);

		// Add a box to the shelf.
		assertTrue(shelf_FREE_LVL_2.addToSlot(slotid, BOX_ID_21));

		// Box is now in the correct slot.
		assertEquals(BOX_ID_21.getShelfSlot(), shelf_FREE_LVL_2.getShelfSlot(slotid));

		// Save shelf to database.
		DatabaseController.getInstance().saveOrUpdate(shelf_FREE_LVL_2);

		// Product box is in the shelf.
		assertTrue(shelf_FREE_LVL_2.getProductBoxes().contains(BOX_ID_21));

		// Product box is in the slot.
		assertTrue(shelf_FREE_LVL_2.getShelfSlot(slotid).contains(BOX_ID_21));

		// Number of product boxes in the shelf has increased by one.
		assertEquals(oldBoxCount + 1, shelf_FREE_LVL_2.getProductBoxes().size());

		// Number of products in the shelf has increased.
		assertEquals(oldProductCount + BOX_ID_21_PRODUCT_COUNT, shelf_FREE_LVL_2.getProductCountInBoxes());

		// Removal from box is a success.
		assertTrue(BOX_ID_21.getShelfSlot().removeBox(BOX_ID_21));

		/*
		 * HIBERNATE NOTICE
		 *
		 * Doing this does not update the shelf.
		 * Shelf must be fetched from database again to get the updated version.
		 */

		// Product box is not in the shelf.
		assertFalse(shelf_FREE_LVL_2.getProductBoxes().contains(BOX_ID_21));

		// Product box is not in the slot.
		assertFalse(shelf_FREE_LVL_2.getShelfSlot(slotid).contains(BOX_ID_21));

		// Number of product boxes in the shelf is back to the old one.
		assertEquals(oldBoxCount, shelf_FREE_LVL_2.getProductBoxes().size());

		// Number of products in the shelf is back to the old one.
		assertEquals(oldProductCount, shelf_FREE_LVL_2.getProductCountInBoxes());

		// -- Save to database --

		/*
		 *
		 * HIBERNATE NOTICE
		 *
		 * For some unknown reason my collections mappings do not work as intended.
		 * The cascading does not work correctly.
		 * When a child element in a collection is changed YOU MUST SAVE THE CHILD AND NOT THE PARENT SET.
		 * Saving the parent set simply does not update the database EVEN IF THE PARENT OBJECT INSTANCE WAS CHANGED.
		 *
		 */

		// I.e. save the box, not the shelf(slot/level).
		DatabaseController.getInstance().saveOrUpdate(BOX_ID_21);

		// Check that database has been updated.
		shelf_FREE_LVL_2 = DatabaseController.getInstance().getShelfByID(SHELF_FREE_LVL_2_ID);
		BOX_ID_21 = DatabaseController.getInstance().getProductBoxByID(21);

		// Product box is not in the shelf.
		assertNull(BOX_ID_21.getShelfSlot());
		assertFalse(shelf_FREE_LVL_2.getProductBoxes().contains(BOX_ID_21));

		// Product box is not in the slot.
		assertFalse(shelf_FREE_LVL_2.getShelfSlot(slotid).contains(BOX_ID_21));

		// Number of product boxes in the shelf is back to the old one.
		assertEquals(oldBoxCount, shelf_FREE_LVL_2.getProductBoxes().size());

		// Number of products in the shelf is back to the old one.
		assertEquals(oldProductCount, shelf_FREE_LVL_2.getProductCountInBoxes());

		/*
		 * No rollback required.
		 */
	}

	@Test
	public final void testHasFreeSpace() throws IllegalArgumentException
	{
		assertTrue(emptyShelf_1_1_to_1_2.hasFreeSpace());
		assertTrue(emptyShelf_1_1_to_1_2.isEmpty());

		assertTrue(emptyShelf_1_1_to_1_2.addToSlot(emptyShelf_1_1_to_1_2.getShelfID() + "-1-1", BOX_ID_21));

		assertTrue(emptyShelf_1_1_to_1_2.hasFreeSpace());
		assertFalse(emptyShelf_1_1_to_1_2.isEmpty());

		/*
		 * Rollback.
		 */

		assertTrue(BOX_ID_21.getShelfSlot().removeBox(BOX_ID_21));
	}

	@Test
	public final void testToString() throws IllegalArgumentException
	{
		assertEquals("[1] Lvls: 1, Boxs: 4, Slts: 4, Free: 0", fullShelf_LVL_1_SLTPOS_1.toString());
	}

	@Test
	public final void testGetDatabaseID()
	{
		assertEquals(SHELF_FREE_LVL_2_ID, shelf_FREE_LVL_2.getDatabaseID());
	}

	@Test
	public final void testAddToSlot_Full() throws IllegalArgumentException
	{
		assertTrue(emptyShelf_1_1_to_1_2.addToSlot(emptyShelf_1_1_to_1_2.getShelfID() + "-1-1", BOX_ID_21));
		assertFalse(emptyShelf_1_1_to_1_2.addToSlot(emptyShelf_1_1_to_1_2.getShelfID() + "-1-1", BOX_ID_22));

		/*
		 * Rollback.
		 */

		assertTrue(BOX_ID_21.getShelfSlot().removeBox(BOX_ID_21));
	}
}
