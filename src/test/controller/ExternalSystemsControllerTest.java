package test.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import velho.controller.DatabaseController;
import velho.controller.ExternalSystemsController;
import velho.model.ProductBox;
import velho.model.Shelf;
import velho.model.enums.DatabaseFileState;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseLinkException;

/**
 * Tests for the {@link Shelf} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class ExternalSystemsControllerTest
{
	private static final String NEWSHELFID = "S2-1-1";
	private static final int BOXDBID = 1;
	private static final int BOXDBID2 = 2;

	@BeforeClass
	public final static void connectAndInitializeDatabase() throws ClassNotFoundException, NoDatabaseLinkException, ExistingDatabaseLinkException
	{
		assertTrue(DatabaseController.link() != DatabaseFileState.DOES_NOT_EXIST);
		assertTrue(DatabaseController.initializeDatabase());
		DatabaseController.loadData();
	}

	@AfterClass
	public final static void unlink() throws NoDatabaseLinkException
	{
		DatabaseController.unlink();
	}

	@Test
	public final void testMoveValid() throws NoDatabaseLinkException
	{
		final ProductBox box = DatabaseController.getProductBoxByID(BOXDBID);

		final String oldShelfSlot = box.getShelfSlot();
		final String oldShelfIDString = (String) Shelf.tokenizeShelfSlotID(box.getShelfSlot())[0];
		final int oldShelfID = Integer.parseInt(oldShelfIDString.substring(1));
		final Shelf oldShelf = DatabaseController.getShelfByID(oldShelfID, true);

		assertTrue(oldShelf.getShelfSlotBoxes(oldShelfSlot).contains(box));

		final String newShelfIDString = (String) Shelf.tokenizeShelfSlotID(NEWSHELFID)[0];
		final int newShelfID = Integer.parseInt(newShelfIDString.substring(1));
		final Shelf newShelf = DatabaseController.getShelfByID(newShelfID, true);

		assertTrue(ExternalSystemsController.move(BOXDBID, NEWSHELFID, false));

		final String newShelfSlot = box.getShelfSlot();

		assertFalse(oldShelf.getShelfSlotBoxes(oldShelfSlot).contains(box));
		assertTrue(newShelf.getShelfSlotBoxes(newShelfSlot).contains(box));
	}

	@Test
	public final void testMoveInValid()
	{
		assertFalse(ExternalSystemsController.move(BOXDBID, "S999-1-1", false));
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
	public final void testMoveValid2() throws NoDatabaseLinkException
	{
		System.out.println("Test Move Valid 2");
		final ProductBox box = DatabaseController.getProductBoxByID(BOXDBID2);

		final String oldShelfIDString = (String) Shelf.tokenizeShelfSlotID(box.getShelfSlot())[0];
		final int oldShelfID = Integer.parseInt(oldShelfIDString.substring(1));
		final Shelf oldShelf = DatabaseController.getShelfByID(oldShelfID, false);
		final String oldShelfSlot = box.getShelfSlot();

		// assertTrue(oldShelf.getShelfSlotBoxes(oldShelfSlot).contains(box));
		System.out.println("move");

		assertTrue(ExternalSystemsController.move(BOXDBID2, NEWSHELFID, false));
		assertFalse(oldShelf.getShelfSlotBoxes(oldShelfSlot).contains(box));

		final String newShelfIDString = (String) Shelf.tokenizeShelfSlotID(NEWSHELFID)[0];
		final int newShelfID = Integer.parseInt(newShelfIDString.substring(1));
		final Shelf newShelf = DatabaseController.getShelfByID(newShelfID, false);

		assertTrue(newShelf.getShelfSlotBoxes(NEWSHELFID).contains(box));
	}

}
