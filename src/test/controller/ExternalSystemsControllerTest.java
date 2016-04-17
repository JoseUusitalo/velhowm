package test.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.BeforeClass;
import org.junit.Test;

import velho.controller.DatabaseController;
import velho.controller.ExternalSystemsController;
import velho.model.ProductBox;
import velho.model.Shelf;

/**
 * Tests for the {@link Shelf} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class ExternalSystemsControllerTest
{
	private static final String NEWSHELFID = "S2-1-1";
	private static final int BOX_DBID_1 = 1;
	private static final int BOX_DBID_2 = 2;

	/**
	 * Loads the sample data into the database if it does not yet exist.
	 *
	 * @throws ParseException
	 */
	@BeforeClass
	public static final void loadSampleData() throws ParseException
	{
		DatabaseController.loadSampleData();
	}

	@Test
	public final void testMoveValid()
	{
		final ProductBox box = DatabaseController.getProductBoxByID(BOX_DBID_1);

		final String oldShelfSlot = box.getShelfSlot().getSlotID();
		final String oldShelfIDString = (String) Shelf.tokenizeShelfSlotID(box.getShelfSlot().getSlotID())[0];
		final int oldShelfID = Integer.parseInt(oldShelfIDString.substring(1));
		final Shelf oldShelf = DatabaseController.getShelfByID(oldShelfID);

		assertTrue(oldShelf.getShelfSlot(oldShelfSlot).contains(box));

		final String newShelfIDString = (String) Shelf.tokenizeShelfSlotID(NEWSHELFID)[0];
		final int newShelfID = Integer.parseInt(newShelfIDString.substring(1));
		final Shelf newShelf = DatabaseController.getShelfByID(newShelfID);

		assertTrue(ExternalSystemsController.move(BOX_DBID_1, NEWSHELFID, false));

		final String newShelfSlot = box.getShelfSlot().getSlotID();

		assertFalse(oldShelf.getShelfSlot(oldShelfSlot).contains(box));
		assertTrue(newShelf.getShelfSlot(newShelfSlot).contains(box));
	}

	@Test
	public final void testMoveInValid()
	{
		assertFalse(ExternalSystemsController.move(BOX_DBID_1, "S999-1-1", false));
	}

	@Test
	public final void testMoveFull()
	{

		// assertTrue(ExternalSystemsController.move(BOXDBID, "S1-1-1", false));
	}

	@Test
	public final void testMoveInValidBox()
	{
		assertFalse(ExternalSystemsController.move(99999, "S4-1-1", false));
	}

	@Test
	public final void testMoveValid2()
	{
		System.out.println("Test Move Valid 2");
		final ProductBox box = DatabaseController.getProductBoxByID(BOX_DBID_2);

		final String oldShelfIDString = (String) Shelf.tokenizeShelfSlotID(box.getShelfSlot().getSlotID())[0];
		final int oldShelfID = Integer.parseInt(oldShelfIDString.substring(1));
		final Shelf oldShelf = DatabaseController.getShelfByID(oldShelfID);
		final String oldShelfSlot = box.getShelfSlot().getSlotID();

		// assertTrue(oldShelf.getShelfSlotBoxes(oldShelfSlot).contains(box));
		System.out.println("move");

		assertTrue(ExternalSystemsController.move(BOX_DBID_2, NEWSHELFID, false));
		assertFalse(oldShelf.getShelfSlot(oldShelfSlot).contains(box));

		final String newShelfIDString = (String) Shelf.tokenizeShelfSlotID(NEWSHELFID)[0];
		final int newShelfID = Integer.parseInt(newShelfIDString.substring(1));
		final Shelf newShelf = DatabaseController.getShelfByID(newShelfID);

		assertTrue(newShelf.getShelfSlot(NEWSHELFID).contains(box));
	}

}
