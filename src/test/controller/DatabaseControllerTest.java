package test.controller;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
	@Test
	public final void testDatabaseLink() throws ClassNotFoundException, NoDatabaseLinkException, ExistingDatabaseLinkException
	{
		assertEquals(true, DatabaseController.link());
		DatabaseController.unlink();
	}

	@Test
	public final void testIsLinked() throws ClassNotFoundException, NoDatabaseLinkException, ExistingDatabaseLinkException
	{
		assertEquals(false, DatabaseController.isLinked());
		DatabaseController.link();
		assertEquals(true, DatabaseController.isLinked());
		DatabaseController.unlink();
	}

	@Test(expected = NoDatabaseLinkException.class)
	public final void testFailInitialization() throws NoDatabaseLinkException
	{
		assertEquals(false, DatabaseController.isLinked());
		DatabaseController.initializeDatabase();
		DatabaseController.unlink();
	}

	@Test
	public final void testInitialization() throws NoDatabaseLinkException, ClassNotFoundException, ExistingDatabaseLinkException
	{
		DatabaseController.connectAndInitialize();
		assertNotEquals(-1, DatabaseController.getRoleID("Manager"));
		DatabaseController.unlink();
	}

	@Test
	public final void testGetRoleID() throws NoDatabaseLinkException, ClassNotFoundException, ExistingDatabaseLinkException
	{
		assertFalse(DatabaseController.isLinked());
		DatabaseController.connectAndInitialize();
		assertNotEquals(-1, DatabaseController.getRoleID("Logistician"));
		DatabaseController.unlink();
	}

	@Test
	public final void testGetUserRoleNames() throws NoDatabaseLinkException, ClassNotFoundException, ExistingDatabaseLinkException
	{
		Set<String> names = new HashSet<String>(Arrays.asList("Manager", "Logistician", "Administrator"));

		DatabaseController.connectAndInitialize();
		assertTrue(DatabaseController.getUserRoleNames().containsAll(names));
		DatabaseController.unlink();
	}

	@Test
	public final void testAuthenticate_ValidPin() throws NoDatabaseLinkException, ClassNotFoundException, ExistingDatabaseLinkException
	{
		DatabaseController.connectAndInitialize();
		User user = DatabaseController.authenticate("111111");
		assertEquals("Admin", user.getFirstName());
		assertEquals("Test", user.getLastName());
		DatabaseController.unlink();
	}

	@Test
	public final void testAuthenticate_InvalidPinLong() throws NoDatabaseLinkException, ClassNotFoundException, ExistingDatabaseLinkException
	{
		DatabaseController.connectAndInitialize();
		assertEquals(null, DatabaseController.authenticate("1111112"));
		DatabaseController.unlink();
	}

	@Test
	public final void testAuthenticate_InvalidPinShort() throws NoDatabaseLinkException, ClassNotFoundException, ExistingDatabaseLinkException
	{
		DatabaseController.connectAndInitialize();
		assertEquals(null, DatabaseController.authenticate("0"));
		DatabaseController.unlink();
	}

	@Test
	public final void testAuthenticate_InvalidString() throws NoDatabaseLinkException, ClassNotFoundException, ExistingDatabaseLinkException
	{
		DatabaseController.connectAndInitialize();
		assertEquals(null, DatabaseController.authenticate("this is NOT a valid pin or badge number"));
		DatabaseController.unlink();
	}

	@Test
	public final void testAuthenticate_ValidBadge() throws NoDatabaseLinkException, ClassNotFoundException, ExistingDatabaseLinkException
	{
		DatabaseController.connectAndInitialize();
		User user = DatabaseController.authenticate("12345678");
		assertEquals("Badger", user.getFirstName());
		assertEquals("Testaccount", user.getLastName());
		DatabaseController.unlink();
	}

	@Test
	public final void testAuthenticate_InvalidBadgeLong() throws NoDatabaseLinkException, ClassNotFoundException, ExistingDatabaseLinkException
	{
		DatabaseController.connectAndInitialize();
		assertEquals(null, DatabaseController.authenticate("100000000"));
		DatabaseController.unlink();
	}

	@Test
	public final void testAuthenticate_InvalidBadgeShort() throws NoDatabaseLinkException, ClassNotFoundException, ExistingDatabaseLinkException
	{
		DatabaseController.connectAndInitialize();
		assertEquals(null, DatabaseController.authenticate("2222222"));
		DatabaseController.unlink();
	}

	@Test
	public final void testGetProductCodeList()
	{
		fail("Not yet implemented");
	}

	@Test
	public final void testGetPublicUserDataList() throws NoDatabaseLinkException, ClassNotFoundException, ExistingDatabaseLinkException
	{
		DatabaseController.connectAndInitialize();
		assertEquals(4, DatabaseController.getPublicUserDataList().size());
		DatabaseController.unlink();
	}

	@Test
	public final void testGetPublicUserDataColumns()
	{
		assertTrue(DatabaseController.getPublicUserDataColumns().values().contains("First Name"));
	}

	@Test
	public final void testAddUser_DeleteUser() throws NoDatabaseLinkException, ClassNotFoundException, ExistingDatabaseLinkException, SQLException
	{
		DatabaseController.connectAndInitialize();
		// The method does not check for data validity.
		DatabaseController.addUser("", "0", "My PIN Is Practically Invalid", "But Technically Valid", 1);
		assertTrue(DatabaseController.getUserByID(5).getFirstName().equals("My PIN Is Practically Invalid"));
		assertNotEquals(null, DatabaseController.getUserByID(5));
		DatabaseController.deleteUser(5);
		assertEquals(null, DatabaseController.getUserByID(5));
		DatabaseController.unlink();
	}
}
