package test.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import velho.controller.DatabaseController;
import velho.controller.ExternalSystemsController;
import velho.model.ProductBox;
import velho.model.Shelf;
import velho.model.enums.DatabaseFileState;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseException;
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
	public final static void connectAndInitializeDatabase() throws ClassNotFoundException, ParseException, ExistingDatabaseLinkException, NoDatabaseException
	{
		assertTrue(DatabaseController.link() != DatabaseFileState.DOES_NOT_EXIST);
		assertTrue(DatabaseController.resetDatabase());
	}

	@AfterClass
	public final static void unlink() throws NoDatabaseLinkException
	{
		DatabaseController.unlink();
	}

	@Test
	public final void testMoveValid()
	{
		final ProductBox box = DatabaseController.getProductBoxByID(BOXDBID);

		final String oldShelfSlot = box.getShelfSlot().getSlotID();
		final String oldShelfIDString = (String) Shelf.tokenizeShelfSlotID(box.getShelfSlot().getSlotID())[0];
		final int oldShelfID = Integer.parseInt(oldShelfIDString.substring(1));
		final Shelf oldShelf = DatabaseController.getShelfByID(oldShelfID);

		assertTrue(oldShelf.getShelfSlot(oldShelfSlot).contains(box));

		final String newShelfIDString = (String) Shelf.tokenizeShelfSlotID(NEWSHELFID)[0];
		final int newShelfID = Integer.parseInt(newShelfIDString.substring(1));
		final Shelf newShelf = DatabaseController.getShelfByID(newShelfID);

		assertTrue(ExternalSystemsController.move(BOXDBID, NEWSHELFID, false));

		final String newShelfSlot = box.getShelfSlot().getSlotID();

		assertFalse(oldShelf.getShelfSlot(oldShelfSlot).contains(box));
		assertTrue(newShelf.getShelfSlot(newShelfSlot).contains(box));
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
	public final void testMoveValid2()
	{
		System.out.println("Test Move Valid 2");
		final ProductBox box = DatabaseController.getProductBoxByID(BOXDBID2);

		final String oldShelfIDString = (String) Shelf.tokenizeShelfSlotID(box.getShelfSlot().getSlotID())[0];
		final int oldShelfID = Integer.parseInt(oldShelfIDString.substring(1));
		final Shelf oldShelf = DatabaseController.getShelfByID(oldShelfID);
		final String oldShelfSlot = box.getShelfSlot().getSlotID();

		// assertTrue(oldShelf.getShelfSlotBoxes(oldShelfSlot).contains(box));
		System.out.println("move");

		assertTrue(ExternalSystemsController.move(BOXDBID2, NEWSHELFID, false));
		assertFalse(oldShelf.getShelfSlot(oldShelfSlot).contains(box));

		final String newShelfIDString = (String) Shelf.tokenizeShelfSlotID(NEWSHELFID)[0];
		final int newShelfID = Integer.parseInt(newShelfIDString.substring(1));
		final Shelf newShelf = DatabaseController.getShelfByID(newShelfID);

		assertTrue(newShelf.getShelfSlot(NEWSHELFID).contains(box));
	}

}
