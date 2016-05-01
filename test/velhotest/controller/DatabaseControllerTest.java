package velhotest.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javafx.collections.ObservableList;
import velho.controller.DatabaseController;
import velho.controller.LocalizationController;
import velho.controller.LogDatabaseController;
import velho.model.ProductBox;
import velho.model.RemovalList;
import velho.model.User;
import velho.model.enums.UserRole;

/**
 * Tests for the {@link DatabaseController} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class DatabaseControllerTest
{
	/**
	 * Creates the log database if needed and connects to it.
	 * Loads the sample data into the database if it does not yet exist.
	 *
	 * @throws Exception
	 */
	@BeforeClass
	public static final void init() throws Exception
	{
		System.out.println("\n\n---- DatabaseControllerTest BeforeClass ----\n\n");
		LogDatabaseController.connectAndInitialize();
		DatabaseController.link();
		DatabaseController.loadSampleData();
		LocalizationController.initializeBundle();
		System.out.println("\n\n---- DatabaseControllerTest Start ----\n\n");
	}

	/**
	 * Unlinks from both databases.
	 */
	@AfterClass
	public static final void unlinkDatabases() throws Exception
	{
		System.out.println("\n\n---- DatabaseControllerTest AfterClass ----\n\n");
		DatabaseController.unlink();
		LogDatabaseController.unlink();
		System.out.println("\n\n---- DatabaseControllerTest Done ----\n\n");
	}

	@Test
	public final void testIsLinked()
	{
		assertTrue(DatabaseController.isLinked());
	}

	@Test
	public final void testGetUserRoleNames()
	{
		final Set<String> names = new HashSet<String>(Arrays.asList("Manager", "Guest", "Logistician", "Administrator"));

		assertTrue(DatabaseController.getUserRoleNames().containsAll(names));
	}

	@Test
	public final void testAuthenticate_ValidPin()
	{
		System.out.println(DatabaseController.getAllUsers());
		final User user = DatabaseController.authenticatePIN("Admin", "Test", "111111");
		assertEquals("Admin", user.getFirstName());
		assertEquals("Test", user.getLastName());
	}

	@Test
	public final void testAuthenticate_InvalidPinLong()
	{
		assertEquals(null, DatabaseController.authenticateBadgeID("1111112"));
	}

	@Test
	public final void testAuthenticate_InvalidPinShort()
	{
		assertEquals(null, DatabaseController.authenticateBadgeID("0"));
	}

	@Test
	public final void testAuthenticate_InvalidString()
	{
		assertEquals(null, DatabaseController.authenticateBadgeID("this is NOT a valid pin or badge number"));
	}

	@Test
	public final void testAuthenticate_ValidBadge()
	{
		final User user = DatabaseController.authenticateBadgeID("12345678");
		assertEquals("Badger", user.getFirstName());
		assertEquals("Testaccount", user.getLastName());
	}

	@Test
	public final void testAuthenticate_InvalidBadgeLong()
	{
		assertEquals(null, DatabaseController.authenticateBadgeID("100000000"));
	}

	@Test
	public final void testAuthenticate_InvalidBadgeShort()
	{
		assertEquals(null, DatabaseController.authenticateBadgeID("2222222"));
	}

	@Test
	public final void testGetProductCodeList()
	{
		final List<Integer> list = DatabaseController.getProductCodeList();
		assertEquals(12, list.size());
		assertTrue(list.containsAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)));
	}

	@Test
	public final void testGetPublicUserDataColumns()
	{
		assertTrue(DatabaseController.getPublicUserDataColumns(false).values().contains("First Name"));
		assertFalse(DatabaseController.getPublicUserDataColumns(false).values().contains("Delete"));

		assertTrue(DatabaseController.getPublicUserDataColumns(true).values().contains("Role"));
	}

	@Test
	public final void testGetUserByID()
	{
		assertEquals(null, DatabaseController.getUserByID(-128));
	}

	/**
	 * Tests that a non-existent user cannot be deleted.
	 */
	@Test
	public final void testDelete_Invalid()
	{
		assertFalse(DatabaseController.deleteUser(new User(-123, "A", "B", "000000", null, UserRole.ADMINISTRATOR)));
	}

	@Test(expected = IllegalArgumentException.class)
	public final void testDelete_Null()
	{
		DatabaseController.deleteRemovalList(null);
	}

	@Test
	public final void testDeleteUser1()
	{
		final ObservableList<Object> users = DatabaseController.getAllUsers();
		final User user = DatabaseController.getUserByID(1);
		assertTrue(users.contains(user));
		assertTrue(DatabaseController.deleteUser(user));
		assertFalse(users.contains(user));

		DatabaseController.save(user);
	}

	@Test
	public final void testGetPublicProductDataColumns()
	{
		// This test is worthless but it exists to improve coverage.

		final LinkedHashMap<String, String> cols = new LinkedHashMap<String, String>();
		cols.put("databaseID", "ID");
		cols.put("name", "Name");
		cols.put("brand", "Brand");
		cols.put("category", "Category");
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
		cols.put("databaseID", "ID");
		cols.put("name", "Name");
		cols.put("brand", "Brand");
		cols.put("category", "Category");
		cols.put("viewButton", "");

		assertEquals(cols, DatabaseController.getProductDataColumns(true, true));
	}

	@Test
	public final void testGetProductSearchDataColumns()
	{
		// This test is worthless but it exists to improve coverage.

		final LinkedHashMap<String, String> cols = new LinkedHashMap<String, String>();
		cols.put("databaseID", "ID");
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
		cols.put("databaseID", "ID");
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
	public final void testAuthenticatePIN_Invalid()
	{
		assertEquals(null, DatabaseController.authenticatePIN("Admin", "Test", "-1"));
	}

	@Test
	public final void testGetProductBoxByID_Invalid()
	{
		assertEquals(null, DatabaseController.getProductBoxByID(-1));
	}

	@Test
	public final void testgetProductIDFromName_Invalid()
	{
		assertEquals(null, DatabaseController.getProductByName("just some random text here not a product name"));
	}

	@Test
	public final void testGetRemovalListByID()
	{
		assertEquals("[1] Active: 3 boxes", DatabaseController.getRemovalListByID(1).toString());
	}

	@Test
	public final void testLoadData()
	{
		// Make sure that a removal list was loaded and the product boxes were placed on it.
		assertEquals(0, DatabaseController.getRemovalListByID(5).getSize());
	}

	@Test
	public final void testInsertRemovalList()
	{
		final ProductBox box = DatabaseController.getProductBoxByID(1);
		RemovalList list = new RemovalList(DatabaseController.getRemovalListStateByID(1));
		assertTrue(list.addProductBox(box));

		assertTrue(list.getBoxes().contains(box));

		final int newid = DatabaseController.saveOrUpdate(list);

		assertTrue(newid > 0);

		list = DatabaseController.getRemovalListByID(newid);

		assertTrue(list.getBoxes().contains(box));

		/*
		 * Rollback.
		 */
		DatabaseController.deleteRemovalList(list);
	}
}
