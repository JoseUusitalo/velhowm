package test.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javafx.collections.ObservableList;
import velho.controller.DatabaseController;
import velho.model.ProductBox;
import velho.model.ProductBoxSearchResultRow;
import velho.model.RemovalList;
import velho.model.User;
import velho.model.enums.DatabaseFileState;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseLinkException;

/**
 * Tests for the {@link DatabaseController} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class DatabaseControllerTest
{
	@BeforeClass
	public final static void connectAndInitializeDatabase() throws ClassNotFoundException, NoDatabaseLinkException, ExistingDatabaseLinkException
	{
		assertTrue(DatabaseController.link() != DatabaseFileState.DOES_NOT_EXIST);
		assertTrue(DatabaseController.initializeDatabase());
	}

	@AfterClass
	public final static void unlink() throws NoDatabaseLinkException
	{
		DatabaseController.unlink();
	}

	@Test
	public final void testIsLinked()
	{
		assertTrue(DatabaseController.isLinked());
	}

	@Test
	public final void testFailInitialization() throws ClassNotFoundException, ExistingDatabaseLinkException
	{
		try
		{
			DatabaseController.unlink();
			assertFalse(DatabaseController.isLinked());
		}
		catch (final NoDatabaseLinkException e)
		{
			fail(e.toString());
		}

		try
		{
			assertFalse(DatabaseController.initializeDatabase());
		}
		catch (final NoDatabaseLinkException e)
		{
			try
			{
				connectAndInitializeDatabase();
			}
			catch (final NoDatabaseLinkException e1)
			{
				fail(e1.toString());
			}
		}
	}

	@Test
	public final void testGetRoleID() throws NoDatabaseLinkException
	{
		assertNotEquals(-1, DatabaseController.getRoleID("Logistician"));
	}

	@Test
	public final void testGetUserRoleNames()
	{
		final Set<String> names = new HashSet<String>(Arrays.asList("Manager", "Guest", "Logistician", "Administrator"));

		assertTrue(DatabaseController.getUserRoleNames().containsAll(names));
	}

	@Test
	public final void testAuthenticate_ValidPin() throws NoDatabaseLinkException
	{
		final User user = DatabaseController.authenticatePIN("Admin", "Test", "111111");
		assertEquals("Admin", user.getFirstName());
		assertEquals("Test", user.getLastName());
	}

	@Test
	public final void testAuthenticate_InvalidPinLong() throws NoDatabaseLinkException
	{
		assertEquals(null, DatabaseController.authenticateBadgeID("1111112"));
	}

	@Test
	public final void testAuthenticate_InvalidPinShort() throws NoDatabaseLinkException
	{
		assertEquals(null, DatabaseController.authenticateBadgeID("0"));
	}

	@Test
	public final void testAuthenticate_InvalidString() throws NoDatabaseLinkException
	{
		assertEquals(null, DatabaseController.authenticateBadgeID("this is NOT a valid pin or badge number"));
	}

	@Test
	public final void testAuthenticate_ValidBadge() throws NoDatabaseLinkException
	{
		final User user = DatabaseController.authenticateBadgeID("12345678");
		assertEquals("Badger", user.getFirstName());
		assertEquals("Testaccount", user.getLastName());
	}

	@Test
	public final void testAuthenticate_InvalidBadgeLong() throws NoDatabaseLinkException
	{
		assertEquals(null, DatabaseController.authenticateBadgeID("100000000"));
	}

	@Test
	public final void testAuthenticate_InvalidBadgeShort() throws NoDatabaseLinkException
	{
		assertEquals(null, DatabaseController.authenticateBadgeID("2222222"));
	}

	@Test
	public final void testGetProductCodeList() throws NoDatabaseLinkException
	{
		final List<Integer> list = DatabaseController.getProductCodeList();
		assertEquals(12, list.size());
		assertTrue(list.containsAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)));
	}

	@Test
	public final void testGetPublicUserDataList() throws NoDatabaseLinkException
	{
		assertEquals(4, DatabaseController.getObservableUsers().size());
	}

	@Test
	public final void testGetPublicUserDataColumns()
	{
		assertTrue(DatabaseController.getPublicUserDataColumns(false).values().contains("First Name"));
		assertFalse(DatabaseController.getPublicUserDataColumns(false).values().contains("Delete"));

		assertTrue(DatabaseController.getPublicUserDataColumns(true).values().contains("Role"));
	}

	@Test
	public final void testAddUser_RemoveUser() throws NoDatabaseLinkException
	{
		// The method does not check for data validity.
		DatabaseController.insertUser("", "000001", "A very UNIQUE! n4m3", "Some text here!", 1);

		assertTrue(DatabaseController.getUserByID(5).getFirstName().equals("A very UNIQUE! n4m3"));
		assertNotEquals(null, DatabaseController.getUserByID(5));

		DatabaseController.deleteUser(5);
		assertEquals(null, DatabaseController.getUserByID(5));

		assertTrue(DatabaseController.initializeDatabase());
	}

	@Test
	public final void testGetUserByID()
	{
		assertEquals(null, DatabaseController.getUserByID(-128));
	}

	@Test
	public final void testRemoveUser_Invalid() throws NoDatabaseLinkException
	{
		assertFalse(DatabaseController.deleteUser(-123));
		assertFalse(DatabaseController.deleteUser(Integer.MAX_VALUE));
	}

	@Test
	public final void testRemoveUser() throws NoDatabaseLinkException
	{
		DatabaseController.loadData();
		assertTrue(DatabaseController.deleteUser(1));

		assertTrue(DatabaseController.initializeDatabase());
	}

	@Test
	public final void testGetPublicProductDataColumns()
	{
		// This test is worthless but it exists to improve coverage.

		final LinkedHashMap<String, String> cols = new LinkedHashMap<String, String>();
		cols.put("productID", "ID");
		cols.put("name", "Name");
		cols.put("brand", "Brand");
		cols.put("category", "Category");
		// cols.put("popularity", "Popularity");
		cols.put("viewButton", "");

		assertEquals(cols, DatabaseController.getProductDataColumns(false, false));
	}

	@Test
	public final void testGetPublicProductDataColumns2()
	{
		// This test is worthless but it exists to improve coverage.

		final LinkedHashMap<String, String> cols = new LinkedHashMap<String, String>();
		cols.put("addButton", "Add");
		cols.put("deleteButton", "Delete");
		cols.put("productID", "ID");
		cols.put("name", "Name");
		cols.put("brand", "Brand");
		cols.put("category", "Category");
		// cols.put("popularity", "Popularity");
		cols.put("viewButton", "");

		assertEquals(cols, DatabaseController.getProductDataColumns(true, true));
	}

	@Test
	public final void testGetProductSearchDataColumns()
	{
		// This test is worhtless but it exists to improve coverage.

		final LinkedHashMap<String, String> cols = new LinkedHashMap<String, String>();
		cols.put("productID", "ID");
		cols.put("productName", "Name");
		cols.put("productBrand", "Brand");
		cols.put("productCategory", "Category");
		cols.put("expirationDate", "Expires");
		cols.put("boxID", "Box ID");
		cols.put("boxShelfSlot", "Shelf Slot");
		cols.put("boxProductCount", "Amount");

		assertEquals(cols, DatabaseController.getProductSearchDataColumns(false, false));
	}

	@Test
	public final void testGetProductSearchDataColumns2()
	{
		// This test is worhtless but it exists to improve coverage.

		final LinkedHashMap<String, String> cols = new LinkedHashMap<String, String>();
		cols.put("addButton", "Add");
		cols.put("removeButton", "Remove");
		cols.put("productID", "ID");
		cols.put("productName", "Name");
		cols.put("productBrand", "Brand");
		cols.put("productCategory", "Category");
		cols.put("expirationDate", "Expires");
		cols.put("boxID", "Box ID");
		cols.put("boxShelfSlot", "Shelf Slot");
		cols.put("boxProductCount", "Amount");

		assertEquals(cols, DatabaseController.getProductSearchDataColumns(true, true));
	}

	@Test
	public final void testGetRoleID_Invalid() throws NoDatabaseLinkException
	{
		assertEquals(-1, DatabaseController.getRoleID("This is DEFINITELY! not a role name..."));
	}

	@Test
	public final void testAuthenticatePIN_Invalid() throws NoDatabaseLinkException
	{
		assertEquals(null, DatabaseController.authenticatePIN(null, "", "-1"));
	}

	@Test
	public final void testGetProductBoxByID_Invalid()
	{
		assertEquals(null, DatabaseController.getProductBoxByID(-1));
	}

	@Test
	public final void testgetProductIDFromName_Invalid() throws NoDatabaseLinkException
	{
		assertEquals(-1, DatabaseController.getProductIDFromName("just some random text here not a product name"));
	}

	@Test
	public final void testGetRemovalListByID_Invalid_Cached()
	{
		assertEquals(null, DatabaseController.getRemovalListByID(-1));
	}

	@Test
	public final void testGetRemovalListByID_Invalid_ForceLoad()
	{
		assertEquals(null, DatabaseController.getRemovalListByID(-1));
	}

	@Test
	public final void testGetRemovalListByID_ForceLoad()
	{
		assertEquals("[1] Active: 3 boxes", DatabaseController.getRemovalListByID(1).toString());
	}

	@Test
	public final void testGetRemovalListByID_Cached()
	{
		assertEquals("[1] Active: 3 boxes", DatabaseController.getRemovalListByID(1).toString());
	}

	@Test
	public final void testGetRemovalListByID_LoadAndCache()
	{
		DatabaseController.clearAllCaches();
		assertEquals("[1] Active: 3 boxes", DatabaseController.getRemovalListByID(1).toString());
	}

	@Test
	public final void testGetRemovalListsViewList() throws NoDatabaseLinkException
	{
		// This test is useless but improves coverage.
		assertTrue(DatabaseController.getObservableRemovalLists().containsAll(DatabaseController.getCachedRemovalLists().values()));
	}

	@Test
	public final void testLoadData() throws NoDatabaseLinkException
	{
		DatabaseController.clearAllCaches();
		DatabaseController.loadData();

		// Make sure that a removal list was loaded and the product boxes were placed on it.
		assertEquals(0, DatabaseController.getRemovalListByID(5).getSize());
	}

	@Test
	public final void testInsertRemovalList() throws NoDatabaseLinkException
	{
		DatabaseController.clearAllCaches();
		DatabaseController.loadData();

		final RemovalList list = new RemovalList();
		final ProductBox box = DatabaseController.getProductBoxByID(1);
		list.addProductBox(box);

		assertTrue(DatabaseController.save(list) > 0);
		final ObservableList<Object> obsboxes = DatabaseController.getRemovalListByID(6).getObservableBoxes();
		final List<ProductBox> boxes = new ArrayList<ProductBox>();
		final Iterator<Object> it = obsboxes.iterator();

		while (it.hasNext())
			boxes.add(((ProductBoxSearchResultRow) it.next()).getBox());

		assertTrue(boxes.contains(box));
	}
}
