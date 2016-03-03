package test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javafx.collections.ObservableList;
import velho.controller.DatabaseController;
import velho.model.ProductBox;
import velho.model.RemovalList;
import velho.model.RemovalListState;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseLinkException;

/**
 * Tests for the {@link velho.model.RemovalList} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class RemovalListTest
{
	private static RemovalList newlist;
	private static RemovalList existinglist;
	private static ProductBox box1;

	@Before
	public void initDatabase() throws Exception
	{
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

		box1 = DatabaseController.getProductBoxByID(1);
		newlist = new RemovalList();
		existinglist = DatabaseController.getRemovalListByID(1, false);
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
		assertEquals("[1] Active: 3 boxes", existinglist.toString());
	}

	@Test
	public final void testGetDatabaseID()
	{
		assertEquals(1, existinglist.getDatabaseID());
	}

	@Test
	public final void testGetSize()
	{
		assertEquals(3, existinglist.getSize());
	}

	@Test
	public final void testGetSize_New()
	{
		assertEquals(0, newlist.getSize());
	}

	@Test
	public final void testGetState() throws NoDatabaseLinkException
	{
		assertEquals(DatabaseController.getRemovalListStateByID(1).getDatabaseID(), newlist.getState().getDatabaseID());
	}

	@Test
	public final void testSetState() throws NoDatabaseLinkException
	{
		RemovalListState oldState = existinglist.getState();
		RemovalListState newState = DatabaseController.getRemovalListStateByID(3);

		assertNotEquals(oldState, newState);

		existinglist.setState(newState);

		// Check that the method worked.
		assertEquals(newState, existinglist.getState());
		assertTrue(existinglist.saveToDatabase());

		// Cache was updated
		assertEquals(newState, DatabaseController.getRemovalListByID(existinglist.getDatabaseID(), true).getState());

		// Database was updated.
		assertEquals(newState, DatabaseController.getRemovalListByID(existinglist.getDatabaseID(), false).getState());
	}

	@Test
	public final void testGetObservableBoxes()
	{
		ObservableList<Object> obsboxes = existinglist.getObservableBoxes();
		ProductBox first = existinglist.getBoxes().iterator().next();

		assertEquals(3, obsboxes.size());
		assertTrue(existinglist.removeProductBox(first));

		assertEquals(2, existinglist.getSize());
		assertEquals(2, obsboxes.size());
	}

	@Test
	public final void testGetBoxes() throws NoDatabaseLinkException
	{
		final Set<ProductBox> set = new HashSet<ProductBox>(
				Arrays.asList(DatabaseController.getProductBoxByID(24), DatabaseController.getProductBoxByID(25), DatabaseController.getProductBoxByID(26)));
		assertTrue(existinglist.getBoxes().containsAll(set));
	}

	@Test
	public final void testSaveToDatabase() throws NoDatabaseLinkException
	{
		ProductBox first = existinglist.getBoxes().iterator().next();

		assertEquals(3, existinglist.getSize());
		assertTrue(existinglist.removeProductBox(first));
		assertEquals(2, existinglist.getSize());
		assertTrue(existinglist.saveToDatabase());
		assertEquals(2, DatabaseController.getRemovalListByID(existinglist.getDatabaseID(), false).getSize());
	}

	@Test
	public final void testAddProductBox()
	{
		assertEquals(0, newlist.getSize());
		assertTrue(newlist.addProductBox(box1));
		assertTrue(newlist.getBoxes().contains(box1));
	}

	@Test
	public final void testRemoveProductBox()
	{
		assertEquals(0, newlist.getSize());
		assertTrue(newlist.addProductBox(box1));
		assertTrue(newlist.getBoxes().contains(box1));
		assertTrue(newlist.removeProductBox(box1));
		assertFalse(newlist.getBoxes().contains(box1));
	}

	@Test
	public final void testRemoveProductBox_Not_Present()
	{
		assertEquals(0, newlist.getSize());
		assertFalse(newlist.removeProductBox(box1));
	}

	@Test
	public final void testReset() throws NoDatabaseLinkException
	{
		RemovalListState activeState = DatabaseController.getRemovalListStateByID(1);
		RemovalListState finishedState = DatabaseController.getRemovalListStateByID(3);

		existinglist.setState(finishedState);

		assertEquals(finishedState, existinglist.getState());
		assertEquals(3, existinglist.getSize());

		existinglist.reset();

		assertEquals(0, existinglist.getSize());
		assertEquals(activeState.getDatabaseID(), existinglist.getState().getDatabaseID());
	}
}
