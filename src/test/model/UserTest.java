/**
 *
 */
package test.model;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import velho.controller.DatabaseController;
import velho.model.Manager;
import velho.model.User;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseLinkException;

/**
 * Tests for the {@link velho.model.User} class.
 *
 * @author Jose Uusitalo
 */
public class UserTest
{
	private final String VALID_BADGE_ID_MAX = "99999999";
	private final String VALID_BADGE_ID_MIN = "00000000";
	private final String INVALID_BADGE_ID_SHORT = "12345";
	private final String INVALID_BADGE_ID_LONG = "123456789";

	private final String VALID_PIN_MAX = "999999";
	private final String VALID_PIN_MIN = "000000";
	private final String INVALID_PIN_SHORT = "12";
	private final String INVALID_PIN_LONG = "1234567";

	private final String VALID_NAME = "First-Name";
	private final String INVALID_NAME_LONG = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

	private final String VALID_ROLE_NAME = "Manager";
	private final String INVALID_ROLE_NAME = "Worker";

	private final User user = new User(-1, "f", "l", new Manager());

	@BeforeClass
	public final static void linkDatabase() throws ClassNotFoundException, NoDatabaseLinkException, ExistingDatabaseLinkException
	{
		DatabaseController.link();
		DatabaseController.initializeDatabase();
	}

	@AfterClass
	public final static void unlinkDatabase() throws NoDatabaseLinkException
	{
		DatabaseController.unlink();
	}

	@Test
	public final void testValidation_PIN_Valid() throws NoDatabaseLinkException
	{
		assertTrue(User.validateUserData(null, VALID_PIN_MAX, VALID_NAME, VALID_NAME, VALID_ROLE_NAME));
	}

	@Test
	public final void testValidation_Badge_Valid() throws NoDatabaseLinkException
	{
		assertTrue(User.validateUserData(VALID_BADGE_ID_MAX, "", VALID_NAME, VALID_NAME, VALID_ROLE_NAME));
	}

	@Test
	public final void testValidation_PIN_Valid2() throws NoDatabaseLinkException
	{
		assertTrue(User.validateUserData(null, VALID_PIN_MIN, VALID_NAME, VALID_NAME, VALID_ROLE_NAME));
	}

	@Test
	public final void testValidation_Badge_Valid2() throws NoDatabaseLinkException
	{
		assertTrue(User.validateUserData(VALID_BADGE_ID_MIN, "", VALID_NAME, VALID_NAME, VALID_ROLE_NAME));
	}

	@Test
	public final void testValidation_PinBadge() throws NoDatabaseLinkException
	{
		assertFalse(User.validateUserData(VALID_BADGE_ID_MAX, VALID_PIN_MAX, VALID_NAME, VALID_NAME, VALID_ROLE_NAME));
	}

	@Test
	public final void testValidation_BadgeID_Long() throws NoDatabaseLinkException
	{
		assertFalse(User.validateUserData(INVALID_BADGE_ID_LONG, null, VALID_NAME, VALID_NAME, VALID_ROLE_NAME));
	}

	@Test
	public final void testValidation_BadgeID_Short() throws NoDatabaseLinkException
	{
		assertFalse(User.validateUserData(INVALID_BADGE_ID_SHORT, "", VALID_NAME, VALID_NAME, VALID_ROLE_NAME));
	}

	@Test
	public final void testValidation_PIN_Long() throws NoDatabaseLinkException
	{
		assertFalse(User.validateUserData("", INVALID_PIN_LONG, VALID_NAME, VALID_NAME, VALID_ROLE_NAME));
	}

	@Test
	public final void testValidation_PIN_Short() throws NoDatabaseLinkException
	{
		assertFalse(User.validateUserData(null, INVALID_PIN_SHORT, VALID_NAME, VALID_NAME, VALID_ROLE_NAME));
	}

	@Test
	public final void testValidation_Name_Long() throws NoDatabaseLinkException
	{
		assertFalse(User.validateUserData(VALID_BADGE_ID_MAX, null, VALID_NAME, INVALID_NAME_LONG, VALID_ROLE_NAME));
	}

	@Test
	public final void testValidation_Name_Null() throws NoDatabaseLinkException
	{
		assertFalse(User.validateUserData("", VALID_PIN_MAX, null, VALID_NAME, VALID_ROLE_NAME));
	}

	@Test
	public final void testValidation_Name_Empty() throws NoDatabaseLinkException
	{
		assertFalse(User.validateUserData(VALID_BADGE_ID_MAX, "", VALID_NAME, "", VALID_ROLE_NAME));
	}

	@Test
	public final void testValidation_Role_Invalid() throws NoDatabaseLinkException
	{
		assertFalse(User.validateUserData("", VALID_PIN_MAX, VALID_NAME, VALID_NAME, INVALID_ROLE_NAME));
	}

	@Test
	public final void testGetFirstName()
	{
		assertEquals("f", user.getFirstName());
	}

	@Test
	public final void testGetLastName()
	{
		assertEquals("l", user.getLastName());
	}

	@Test
	public final void testGetFullName()
	{
		assertEquals("f l", user.getFullName());
	}

	@Test
	public final void testGetFullDetails()
	{
		assertEquals("f l (Manager)", user.getFullDetails());
	}

	@Test
	public final void testToString()
	{
		assertEquals("f l [Manager | -1]", user.toString());
	}

	@Test
	public final void testGetRole()
	{
		assertEquals("Manager", user.getRole().getName());
	}

	@Test
	public final void testGetRoleName()
	{
		assertEquals("Manager", user.getRoleName());
	}

	@Test
	public final void testGetDatabaseID()
	{
		assertEquals(-1, user.getDatabaseID());
	}
}
