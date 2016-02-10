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

	private static final String BOXSLOTID = "S1-1-0";
	private static final String NEWSHELFID = "S2-1-1";
	private static final int BOXDBID = 1;

	@BeforeClass
	public final static void connectAndInitializeDatabase() throws ClassNotFoundException, NoDatabaseLinkException, ExistingDatabaseLinkException
	{
		assertTrue(DatabaseController.link());
		assertTrue(DatabaseController.initializeDatabase());
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
		final String oldShelfIDString = (String) Shelf.tokenizeShelfSlotID(box.getShelfSlot())[0];
		final int oldShelfID = Integer.parseInt(oldShelfIDString.substring(1));
		final Shelf oldShelf = DatabaseController.getShelfByID(oldShelfID);

		String newShelfIDString = (String) Shelf.tokenizeShelfSlotID(NEWSHELFID)[0];
		int newShelfID = Integer.parseInt(newShelfIDString.substring(1));
		final Shelf newShelf = DatabaseController.getShelfByID(newShelfID);

		assertTrue(ExternalSystemsController.move(BOXDBID, NEWSHELFID));
	}

	@Test
	public final void testMoveInValid() throws NoDatabaseLinkException
	{

		assertFalse(ExternalSystemsController.move(BOXDBID, "S999-1-1"));
	}

	@Test
	public final void testMoveFull() throws NoDatabaseLinkException
	{

		assertFalse(ExternalSystemsController.move(BOXDBID, "S1-1-1"));
	}

}
