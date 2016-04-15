package test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import velho.controller.DatabaseController;
import velho.model.Manifest;
import velho.model.ManifestState;
import velho.model.ProductBox;

/**
 * Tests for the {@link Manifest} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class ManifestTest
{
	private static Manifest newManifest = new Manifest();
	private static Manifest existingManifest = DatabaseController.getManifestByID(2);

	@Test
	public final void testToString()
	{
		Date order = new Date(1234);
		Date rec = new Date(12341234);
		Manifest anotherManifest = new Manifest(new ManifestState(-1, "Whatever"), 123, order, rec);
		assertEquals("[0] State: Whatever Driver: 123 Ordered/Received: " + DatabaseController.getH2DateFormat(order) + "/"
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
		assertTrue(newManifest.getDatabaseID() <= 0);
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
	public final void testGetState()
	{
		assertEquals(DatabaseController.getManifestStateByID(1).getDatabaseID(), existingManifest.getState().getDatabaseID());
	}

	@Test
	public final void testGetState_New()
	{
		assertNull(newManifest.getState());
	}

	@Test
	public final void testSetState_SaveToDatabase()
	{
		final int oldID = existingManifest.getDatabaseID();
		final ManifestState oldState = existingManifest.getState();
		final ManifestState newState = DatabaseController.getManifestStateByID(2);

		// The states are different.
		assertNotEquals(oldState, newState);
		existingManifest.setState(newState);

		// Check that the method worked.
		assertEquals(newState, existingManifest.getState());

		// Save.
		final int saveID = DatabaseController.save(existingManifest);
		assertTrue(saveID > 0);

		// Check that that the object was updated, not inserted.
		assertEquals(saveID, oldID);

		// Database was updated.
		assertEquals(newState, DatabaseController.getManifestByID(saveID).getState());

		// TODO: Figure out a better way to roll back changes.
		existingManifest.setState(oldState);
		DatabaseController.save(existingManifest);
	}

	@Test
	public final void testGetBoxes()
	{
		final List<ProductBox> list = new ArrayList<ProductBox>(
				Arrays.asList(DatabaseController.getProductBoxByID(35), DatabaseController.getProductBoxByID(36), DatabaseController.getProductBoxByID(37)));

		/*
		 * FIXME: This is not transitive and does not when the list is a set.
		 * list.containsAll(existingManifest.getBoxes()) = true
		 * existingManifest.getBoxes().containsAll(list) = false
		 */
		assertTrue(list.containsAll(existingManifest.getBoxes()));
	}
}
