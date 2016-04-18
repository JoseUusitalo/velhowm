package test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import javafx.collections.ObservableList;
import velho.controller.DatabaseController;
import velho.model.HibernateSessionFactory;
import velho.model.ProductBox;
import velho.model.RemovalList;
import velho.model.RemovalListState;

/**
 * Tests for the {@link velho.model.RemovalList} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class RemovalListTest
{
	private static RemovalList newlist;
	private static RemovalList existingRemovalList;
	private static ProductBox box1;

	/**
	 * Loads the sample data into the database if it does not yet exist.
	 *
	 * @throws ParseException
	 */
	@BeforeClass
	public static final void loadSampleData() throws ParseException
	{
		DatabaseController.loadSampleData();
		box1 = DatabaseController.getProductBoxByID(1);
		existingRemovalList = DatabaseController.getRemovalListByID(1);
		newlist = new RemovalList();
	}

	@Test
	public final void testToString()
	{
		assertEquals("[1] Active: 3 boxes", existingRemovalList.toString());
	}

	@Test
	public final void testGetDatabaseID()
	{
		assertEquals(1, existingRemovalList.getDatabaseID());
	}

	@Test
	public final void testGetSize()
	{
		assertEquals(3, existingRemovalList.getSize());
	}

	@Test
	public final void testGetSize_New()
	{
		assertEquals(0, newlist.getSize());
	}

	@Test
	public final void testGetState()
	{
		assertEquals(DatabaseController.getRemovalListStateByID(1).getDatabaseID(), newlist.getState().getDatabaseID());
	}

	@Test
	public final void testSetState()
	{
		final int oldID = existingRemovalList.getDatabaseID();
		final RemovalListState oldState = existingRemovalList.getState();
		final RemovalListState newState = DatabaseController.getRemovalListStateByID(3);

		assertNotEquals(oldState, newState);

		existingRemovalList.setState(newState);

		// Check that the method worked.
		assertEquals(newState, existingRemovalList.getState());

		// Save.
		final int saveID = DatabaseController.saveOrUpdate(existingRemovalList);
		assertTrue(saveID > 0);

		// Check that that the object was updated, not inserted.
		assertEquals(saveID, oldID);

		// Database was updated.
		assertEquals(newState, DatabaseController.getRemovalListByID(saveID).getState());

		// TODO: Figure out a better way to roll back changes.
		existingRemovalList.setState(oldState);
		DatabaseController.saveOrUpdate(existingRemovalList);
	}

	@Test
	public final void testGetObservableBoxes()
	{
		final Set<ProductBox> boxes = new HashSet<ProductBox>(existingRemovalList.getBoxes());

		ObservableList<Object> obsboxes = existingRemovalList.getObservableBoxes();
		ProductBox first = existingRemovalList.getBoxes().iterator().next();

		assertEquals(3, obsboxes.size());
		assertTrue(existingRemovalList.removeProductBox(first));

		assertEquals(2, existingRemovalList.getSize());
		assertEquals(2, obsboxes.size());

		// Rollback.
		assertTrue(existingRemovalList.setBoxes(boxes));
		DatabaseController.saveOrUpdate(existingRemovalList);
	}

	@Test
	public final void testGetBoxes()
	{
		final List<ProductBox> list = new ArrayList<ProductBox>(
				Arrays.asList(DatabaseController.getProductBoxByID(24), DatabaseController.getProductBoxByID(25), DatabaseController.getProductBoxByID(26)));
		assertTrue(list.containsAll(existingRemovalList.getBoxes()));
	}

	@Test
	public final void testSaveToDatabase()
	{
		final RemovalList rl = existingRemovalList = DatabaseController.getRemovalListByID(1);

		System.out.println("RemovalList testSaveToDatabase()");
		System.out.println(rl.getBoxes());
		final ProductBox first = rl.getBoxes().iterator().next();
		System.out.println(first);
		System.out.println();

		assertEquals(3, rl.getSize());
		assertTrue(rl.removeProductBox(first));
		assertFalse(rl.getBoxes().contains(first));

		System.out.println("Removed");
		System.out.println(rl.getBoxes());

		System.out.println("\nAll lists");
		System.out.println(DatabaseController.getAllRemovalLists());

		// Save.
		final int saveID = DatabaseController.saveOrUpdate(rl);
		assertEquals(saveID, rl.getDatabaseID());

		System.out.println("\nAll lists");
		System.out.println(DatabaseController.getAllRemovalLists());

		System.out.println("Evict");
		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		HibernateSessionFactory.getInstance().getCurrentSession().evict(rl);
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();

		HibernateSessionFactory.getInstance().getCurrentSession().beginTransaction();
		HibernateSessionFactory.getInstance().getCurrentSession().clear();
		HibernateSessionFactory.getInstance().getCurrentSession().getTransaction().commit();

		System.out.println("\n\n");
		final RemovalList dblist = DatabaseController.getRemovalListByID(saveID);
		System.out.println("\n\n");

		assertEquals(dblist.getUuid(), rl.getUuid());

		System.out.println("From DB: " + dblist);
		System.out.println("Existing: " + rl.getBoxes());
		System.out.println("DB list: " + dblist.getBoxes());

		// Database was updated.
		assertEquals(2, dblist.getSize());

		/*
		 * Rollback.
		 */
		assertTrue(existingRemovalList.addProductBox(first));
		assertTrue(existingRemovalList.getBoxes().contains(first));
		DatabaseController.saveOrUpdate(existingRemovalList);
	}

	@Test
	public final void testAddProductBox()
	{
		assertEquals(0, newlist.getSize());
		assertTrue(newlist.addProductBox(box1));
		assertTrue(newlist.getBoxes().contains(box1));

		newlist.removeProductBox(box1);
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
	public final void testReset()
	{
		// Remember to make a copy, otherwise this variable will also change on reset!
		final Set<ProductBox> boxes = new HashSet<ProductBox>(existingRemovalList.getBoxes());
		final RemovalListState oldState = existingRemovalList.getState();

		final RemovalListState activeState = DatabaseController.getRemovalListStateByID(1);
		final RemovalListState finishedState = DatabaseController.getRemovalListStateByID(3);

		existingRemovalList.setState(finishedState);

		assertEquals(finishedState, existingRemovalList.getState());
		assertEquals(3, existingRemovalList.getSize());

		existingRemovalList.reset();

		assertEquals(0, existingRemovalList.getSize());
		assertEquals(activeState.getDatabaseID(), existingRemovalList.getState().getDatabaseID());

		// Roll back.
		existingRemovalList.setState(oldState);
		assertTrue(existingRemovalList.setBoxes(boxes));
		DatabaseController.saveOrUpdate(existingRemovalList);
	}
}
