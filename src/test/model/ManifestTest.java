package test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import velho.controller.DatabaseController;
import velho.model.Manifest;
import velho.model.ManifestState;
import velho.model.ProductBox;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseLinkException;

/**
 * Tests for the {@link Manifest} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class ManifestTest
{
	private static Manifest newManifest;
	private static Manifest existingManifest;

	@Before
	public void initDatabase() throws Exception
	{
		try
		{
			if (!DatabaseController.isLinked())
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

		newManifest = new Manifest();
		existingManifest = DatabaseController.getManifestByID(2, false);
	}

	@After
	public void unlinkDatabase() throws Exception
	{
		try
		{
			DatabaseController.unlink();
		}
		catch (NoDatabaseLinkException e)
		{
			// Ignore.
		}
	}

	@Test
	public final void testToString()
	{
		Date order = new Date(1234);
		Date rec = new Date(12341234);
		Manifest anotherManifest = new Manifest(new ManifestState(-1, "Whatever"), 123, order, rec);
		assertEquals("[-1] State: Whatever Driver: 123 Ordered/Received: " + DatabaseController.getH2DateFormat(order) + "/"
				+ DatabaseController.getH2DateFormat(rec) + " (0)", anotherManifest.toString());
	}

	@Test
	public final void testGetDatabaseID()
	{
		assertEquals(2, existingManifest.getDatabaseID());
	}

	@Test
	public final void testGetDatabaseID_New()
	{
		assertEquals(-1, newManifest.getDatabaseID());
	}

	@Test
	public final void testGetSize()
	{
		assertEquals(3, existingManifest.getSize());
		assertEquals(3, existingManifest.getObservableBoxes().size());
	}

	@Test
	public final void testGetSize_New()
	{
		assertEquals(0, newManifest.getSize());
		assertEquals(0, newManifest.getObservableBoxes().size());
	}

	@Test
	public final void testGetState() throws NoDatabaseLinkException
	{
		assertEquals(DatabaseController.getManifestStateByID(1).getDatabaseID(), existingManifest.getState().getDatabaseID());
	}

	@Test
	public final void testGetState_New() throws NoDatabaseLinkException
	{
		assertEquals(DatabaseController.getManifestStateByID(3).getDatabaseID(), newManifest.getState().getDatabaseID());
	}

	@Test
	public final void testSetState_SaveToDatabase() throws NoDatabaseLinkException
	{
		ManifestState oldState = existingManifest.getState();
		ManifestState newState = DatabaseController.getManifestStateByID(2);

		assertNotEquals(oldState, newState);

		existingManifest.setState(newState);

		// Check that the method worked.
		assertEquals(newState, existingManifest.getState());
		assertTrue(DatabaseController.save(existingManifest) > 0);

		// Cache was updated
		assertEquals(newState, DatabaseController.getManifestByID(existingManifest.getDatabaseID(), true).getState());

		// Database was updated.
		assertEquals(newState, DatabaseController.getManifestByID(existingManifest.getDatabaseID(), false).getState());
	}

	@Test
	public final void testGetBoxes() throws NoDatabaseLinkException
	{
		final Set<ProductBox> set = new HashSet<ProductBox>(
				Arrays.asList(DatabaseController.getProductBoxByID(35), DatabaseController.getProductBoxByID(36), DatabaseController.getProductBoxByID(37)));
		assertTrue(existingManifest.getBoxes().containsAll(set));
	}
}
