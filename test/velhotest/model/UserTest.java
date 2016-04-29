package velhotest.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import velho.controller.DatabaseController;
import velho.controller.LogDatabaseController;
import velho.controller.UserController;
import velho.model.User;
import velho.model.enums.UserRole;

/**
 * Tests for the {@link velho.model.User} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
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

	private final UserRole VALID_ROLE = UserRole.MANAGER;
	private final UserRole NULL_ROLE = null;

	private final User user = new User(-1, "f", "l", "000000", null, UserRole.MANAGER);

	/**
	 * Creates the log database if needed and connects to it.
	 * Loads the sample data into the database if it does not yet exist.
	 *
	 * @throws Exception
	 */
	@BeforeClass
	public static final void init() throws Exception
	{
		LogDatabaseController.connectAndInitialize();
		DatabaseController.link();
		DatabaseController.loadSampleData();
	}

	/**
	 * Unlinks from both databases.
	 */
	@AfterClass
	public static final void unlinkDatabases() throws Exception
	{
		DatabaseController.unlink();
		LogDatabaseController.unlink();
	}

	@Test
	public final void testValidation_PIN_Valid()
	{
		assertTrue(UserController.validateUserData(null, VALID_PIN_MAX, VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_Badge_Valid()
	{
		assertTrue(UserController.validateUserData(VALID_BADGE_ID_MAX, "", VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_PIN_Valid2()
	{
		assertTrue(UserController.validateUserData(null, VALID_PIN_MIN, VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_Badge_Valid2()
	{
		assertTrue(UserController.validateUserData(VALID_BADGE_ID_MIN, "", VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_BadgeID_Long()
	{
		assertFalse(UserController.validateUserData(INVALID_BADGE_ID_LONG, null, VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_BadgeID_Short()
	{
		assertFalse(UserController.validateUserData(INVALID_BADGE_ID_SHORT, "", VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_PIN_Long()
	{
		assertFalse(UserController.validateUserData("", INVALID_PIN_LONG, VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_PIN_Short()
	{
		assertFalse(UserController.validateUserData(null, INVALID_PIN_SHORT, VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_PinBadge()
	{
		assertFalse(UserController.validateUserData(VALID_BADGE_ID_MAX, VALID_PIN_MAX, VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_PinBadge_Neither()
	{
		assertFalse(UserController.validateUserData("", "", VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_PinBadge_Neither2()
	{
		assertFalse(UserController.validateUserData(null, null, VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_PinBadge_Both()
	{
		System.out.println("testValidation_PinBadge_Both():");
		System.out.println(UserController.isValidPIN(VALID_PIN_MIN));
		System.out.println(UserController.isValidPIN(VALID_PIN_MAX));
		System.out.println(UserController.isValidBadgeID(VALID_BADGE_ID_MAX));
		System.out.println(UserController.isValidBadgeID(VALID_BADGE_ID_MIN));
		assertFalse(UserController.validateUserData(VALID_BADGE_ID_MIN, VALID_PIN_MAX, VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_Name_Long()
	{
		assertFalse(UserController.validateUserData(VALID_BADGE_ID_MAX, null, INVALID_NAME_LONG, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_Name_Null()
	{
		assertFalse(UserController.validateUserData("", VALID_PIN_MAX, null, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_Name_Empty()
	{
		assertFalse(UserController.validateUserData(VALID_BADGE_ID_MAX, "", "", VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_Name_Long2()
	{
		assertFalse(UserController.validateUserData(VALID_BADGE_ID_MAX, null, VALID_NAME, INVALID_NAME_LONG, VALID_ROLE));
	}

	@Test
	public final void testValidation_Name_Null2()
	{
		assertFalse(UserController.validateUserData("", VALID_PIN_MAX, VALID_NAME, null, VALID_ROLE));
	}

	@Test
	public final void testValidation_Name_Empty2()
	{
		assertFalse(UserController.validateUserData(VALID_BADGE_ID_MAX, "", VALID_NAME, "", VALID_ROLE));
	}

	@Test
	public final void testValidation_Role_Invalid()
	{
		assertFalse(UserController.validateUserData("", VALID_PIN_MAX, VALID_NAME, VALID_NAME, NULL_ROLE));
	}

	@Test
	public final void testIsValidPIN_Invalid()
	{
		assertFalse(UserController.isValidPIN("whatev"));
	}

	@Test
	public final void testIsValidPIN_Negative()
	{
		assertFalse(UserController.isValidPIN("-12345"));
	}

	@Test
	public final void testisValidPIN_Zero()
	{
		assertTrue(UserController.isValidPIN("000000"));
	}

	@Test
	public final void testIsValidBadgeID_Invalid()
	{
		assertFalse(UserController.isValidBadgeID("whatever"));
	}

	@Test
	public final void testIsValidBadgeID_Negative()
	{
		assertFalse(UserController.isValidBadgeID("-10000000"));
	}

	@Test
	public final void testIsValidBadgeID2()
	{
		assertTrue(UserController.isValidBadgeID("99999999"));
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
