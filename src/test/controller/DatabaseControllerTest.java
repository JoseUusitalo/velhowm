package test.controller;

import static org.junit.Assert.*;

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
	public final void testDatabaseConnect() throws ClassNotFoundException, NoDatabaseLinkException, ExistingDatabaseLinkException
	{
		assertEquals(true, DatabaseController.link());
		DatabaseController.unlink();
	}

	@Test
	public final void testDatabaseConnection() throws ClassNotFoundException, NoDatabaseLinkException, ExistingDatabaseLinkException
	{
		assertEquals(false, DatabaseController.isLinked());
		DatabaseController.link();
		assertEquals(true, DatabaseController.isLinked());
		DatabaseController.unlink();
	}

	@Test(expected=NoDatabaseLinkException.class)
	public final void testFailInitialization() throws NoDatabaseLinkException
	{
		assertEquals(false, DatabaseController.isLinked());
		DatabaseController.initializeDatabase();
	}

	@Test
	public final void testInitialization() throws NoDatabaseLinkException, ClassNotFoundException, ExistingDatabaseLinkException
	{
		DatabaseController.link();
		DatabaseController.initializeDatabase();
		assertNotEquals(-1, DatabaseController.getRoleID("Manager"));
		DatabaseController.unlink();
	}

	@Test
	public final void testGetUserRoleID() throws NoDatabaseLinkException, ClassNotFoundException, ExistingDatabaseLinkException
	{
		DatabaseController.link();
		DatabaseController.initializeDatabase();
		assertNotEquals(-1, DatabaseController.getRoleID("Logistician"));
		DatabaseController.unlink();
	}

	@Test
	public final void testGetUserRoleNames() throws NoDatabaseLinkException, ClassNotFoundException, ExistingDatabaseLinkException
	{
		Set<String> names = new HashSet<String>(Arrays.asList("Manager","Logistician","Administrator"));

		DatabaseController.link();
		DatabaseController.initializeDatabase();
		assertTrue(DatabaseController.getUserRoleNames().containsAll(names));
		DatabaseController.unlink();
	}

	@Test
	public final void testAuthenticate_ValidPin() throws NoDatabaseLinkException, ClassNotFoundException, ExistingDatabaseLinkException
	{
		DatabaseController.link();
		DatabaseController.initializeDatabase();
		User user = DatabaseController.authenticate("111111");
		assertEquals("Admin", user.getFirstName());
		assertEquals("Test", user.getLastName());
		DatabaseController.unlink();
	}
}
