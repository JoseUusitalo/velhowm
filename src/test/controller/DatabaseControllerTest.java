package test.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import velho.controller.DatabaseController;
import velho.model.User;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseLinkException;

/**
 * Tests for the {@link velho.controller.Database} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class DatabaseControllerTest
{
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
		catch (NoDatabaseLinkException e)
		{
			fail(e.toString());
		}

		try
		{
			assertFalse(DatabaseController.initializeDatabase());
		}
		catch (NoDatabaseLinkException e)
		{
			try
			{
				connectAndInitializeDatabase();
			}
			catch (NoDatabaseLinkException e1)
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
	public final void testGetUserRoleNames() throws NoDatabaseLinkException
	{
		Set<String> names = new HashSet<String>(Arrays.asList("Manager", "Logistician", "Administrator"));

		assertTrue(DatabaseController.getUserRoleNames().containsAll(names));
	}

	@Test
	public final void testAuthenticate_ValidPin() throws NoDatabaseLinkException
	{
		User user = DatabaseController.authenticatePIN("Admin", "Test", "111111");
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
		User user = DatabaseController.authenticateBadgeID("12345678");
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
		List<Integer> list = DatabaseController.getProductCodeList();
		assertEquals(12, list.size());
		assertTrue(list.containsAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)));
	}

	@Test
	public final void testGetPublicUserDataList() throws NoDatabaseLinkException
	{
		assertEquals(4, DatabaseController.getPublicUserDataList().size());
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
		DatabaseController.addUser("", "000001", "A very UNIQUE! n4m3", "Some text here!", 1);

		assertTrue(DatabaseController.getUserByID(5).getFirstName().equals("A very UNIQUE! n4m3"));
		assertNotEquals(null, DatabaseController.getUserByID(5));

		DatabaseController.removeUser(5);
		assertEquals(null, DatabaseController.getUserByID(5));

		assertTrue(DatabaseController.initializeDatabase());
	}

	@Test
	public final void testGetUserByID() throws NoDatabaseLinkException
	{
		assertEquals(null, DatabaseController.getUserByID(-128));
	}

	@Test
	public final void testRemoveUser_Invalid() throws NoDatabaseLinkException
	{
		assertFalse(DatabaseController.removeUser(-123));
		assertFalse(DatabaseController.removeUser(Integer.MAX_VALUE));
	}

	@Test
	public final void testRemoveUser() throws NoDatabaseLinkException
	{
		assertTrue(DatabaseController.removeUser(1));

		assertTrue(DatabaseController.initializeDatabase());
	}
}
