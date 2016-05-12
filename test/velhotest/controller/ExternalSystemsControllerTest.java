package velhotest.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import velho.controller.ExternalSystemsController;
import velho.controller.database.DatabaseController;
import velho.controller.database.LogDatabaseController;
import velho.model.ProductBox;
import velho.model.Shelf;

/**
 * Tests for the {@link ExternalSystemsController} class.
 *
 * @author Jose Uusitalo &amp; Edward Puustinen
 */
@SuppressWarnings("static-method")
public class ExternalSystemsControllerTest
{
	private static final String NEW_SHELFSLOT_ID = "S5-1-1";
	private static final int BOX_DBID_1 = 1;
	private static final int BOX_NOT_IN_SLOT = 21;

	/**
	 * Creates the log database if needed and connects to it.
	 * Loads the sample data into the database if it does not yet exist.
	 *
	 * @throws Exception
	 */
	@BeforeClass
	public static final void init() throws Exception
	{
		System.out.println("\n\n---- ExternalSystemsControllerTest BeforeClass ----\n\n");
		LogDatabaseController.getInstance().connectAndInitialize();
		DatabaseController.getInstance().link();
		DatabaseController.getInstance().loadSampleData();
		System.out.println("\n\n---- ExternalSystemsControllerTest Start ----\n\n");
	}

	/**
	 * Unlinks from both databases.
	 */
	@AfterClass
	public static final void unlinkDatabases() throws Exception
	{
		System.out.println("\n\n---- ExternalSystemsControllerTest AfterClass ----\n\n");
		DatabaseController.getInstance().unlink();
		LogDatabaseController.getInstance().unlink();
		System.out.println("\n\n---- ExternalSystemsControllerTest Done ----\n\n");
	}

	@Test
	public final void testMoveValid()
	{
		ProductBox box = DatabaseController.getInstance().getProductBoxByID(BOX_DBID_1);

		final String oldShelfSlot = box.getShelfSlot().getSlotID();
		Shelf oldShelf = box.getShelfSlot().getParentShelfLevel().getParentShelf();
		Shelf newShelf = DatabaseController.getInstance().getShelfByID(Shelf.shelfSlotIDToShelfDatabaseID(NEW_SHELFSLOT_ID));

		// Box is in the old shelf.
		assertTrue(oldShelf.getProductBoxes().contains(box));

		// Box is not in the new shelf.
		assertFalse(newShelf.getProductBoxes().contains(box));

		// Move is a success.
		assertTrue(ExternalSystemsController.getInstance().move(box.getDatabaseID(), NEW_SHELFSLOT_ID, false));

		// TODO: Refresh private method
		oldShelf = DatabaseController.getInstance().getShelfByID(oldShelf.getDatabaseID());
		newShelf = DatabaseController.getInstance().getShelfByID(newShelf.getDatabaseID());
		box = DatabaseController.getInstance().getProductBoxByID(BOX_DBID_1);

		// Box is not in the old shelf.
		assertFalse(oldShelf.getProductBoxes().contains(box));

		// Box is in the new shelf.
		assertTrue(newShelf.getProductBoxes().contains(box));

		// Box has been updated.
		assertEquals(box.getShelfSlot(), newShelf.getShelfSlot(NEW_SHELFSLOT_ID));

		/*
		 * Rollback.
		 */

		assertTrue(ExternalSystemsController.getInstance().move(box.getDatabaseID(), oldShelfSlot, false));

		// TODO: Refresh private method
		oldShelf = DatabaseController.getInstance().getShelfByID(oldShelf.getDatabaseID());
		newShelf = DatabaseController.getInstance().getShelfByID(newShelf.getDatabaseID());
		box = DatabaseController.getInstance().getProductBoxByID(BOX_DBID_1);

		// Box is in the old shelf.
		assertTrue(oldShelf.getProductBoxes().contains(box));

		// Box is not in the new shelf.
		assertFalse(newShelf.getProductBoxes().contains(box));
	}

	@Test
	public final void testMoveToFullSlot()
	{
		assertFalse(ExternalSystemsController.getInstance().move(BOX_NOT_IN_SLOT, "S1-1-1", false));
	}

	@Test
	public final void testMoveBackToSameSlot()
	{
		assertFalse(ExternalSystemsController.getInstance().move(BOX_DBID_1, "S1-1-1", false));
	}

	@Test
	public final void testMoveInvalidBox()
	{
		assertFalse(ExternalSystemsController.getInstance().move(99999, "S4-1-1", false));
	}

	@Test
	public final void testMoveValid_NotInSlot()
	{
		ProductBox box = DatabaseController.getInstance().getProductBoxByID(BOX_NOT_IN_SLOT);

		Shelf newShelf = DatabaseController.getInstance().getShelfByID(Shelf.shelfSlotIDToShelfDatabaseID(NEW_SHELFSLOT_ID));

		// Box is not in the new shelf.
		assertFalse(newShelf.getProductBoxes().contains(box));

		// Move is a success.
		assertTrue(ExternalSystemsController.getInstance().move(box.getDatabaseID(), NEW_SHELFSLOT_ID, false));

		// TODO: Refresh private method
		newShelf = DatabaseController.getInstance().getShelfByID(newShelf.getDatabaseID());
		box = DatabaseController.getInstance().getProductBoxByID(BOX_NOT_IN_SLOT);

		// Box is in the new shelf.
		assertTrue(newShelf.getProductBoxes().contains(box));

		// Box has been updated.
		assertEquals(box.getShelfSlot(), newShelf.getShelfSlot(NEW_SHELFSLOT_ID));

		/*
		 * Rollback.
		 */

		assertTrue(ExternalSystemsController.getInstance().move(box.getDatabaseID(), null, false));

		// TODO: Refresh private method
		newShelf = DatabaseController.getInstance().getShelfByID(newShelf.getDatabaseID());
		box = DatabaseController.getInstance().getProductBoxByID(BOX_NOT_IN_SLOT);

		// Box is not in the new shelf.
		assertFalse(newShelf.getProductBoxes().contains(box));
	}

}
