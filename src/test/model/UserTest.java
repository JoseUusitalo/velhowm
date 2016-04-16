package test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.BeforeClass;
import org.junit.Test;

import velho.controller.DatabaseController;
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

	private final User user = new User(-1, "f", "l", UserRole.MANAGER);

	/**
	 * Loads the sample data into the database if it does not yet exist.
	 *
	 * @throws ParseException
	 */
	@BeforeClass
	public static final void loadSampleData() throws ParseException
	{
		DatabaseController.loadSampleData();
	}

	@Test
	public final void testValidation_PIN_Valid()
	{
		assertTrue(User.validateUserData(null, VALID_PIN_MAX, VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_Badge_Valid()
	{
		assertTrue(User.validateUserData(VALID_BADGE_ID_MAX, "", VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_PIN_Valid2()
	{
		assertTrue(User.validateUserData(null, VALID_PIN_MIN, VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_Badge_Valid2()
	{
		assertTrue(User.validateUserData(VALID_BADGE_ID_MIN, "", VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_BadgeID_Long()
	{
		assertFalse(User.validateUserData(INVALID_BADGE_ID_LONG, null, VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_BadgeID_Short()
	{
		assertFalse(User.validateUserData(INVALID_BADGE_ID_SHORT, "", VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_PIN_Long()
	{
		assertFalse(User.validateUserData("", INVALID_PIN_LONG, VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_PIN_Short()
	{
		assertFalse(User.validateUserData(null, INVALID_PIN_SHORT, VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_PinBadge()
	{
		assertFalse(User.validateUserData(VALID_BADGE_ID_MAX, VALID_PIN_MAX, VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_PinBadge_Neither()
	{
		assertFalse(User.validateUserData("", "", VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_PinBadge_Neither2()
	{
		assertFalse(User.validateUserData(null, null, VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_PinBadge_Both()
	{
		System.out.println("testValidation_PinBadge_Both():");
		System.out.println(User.isValidPIN(VALID_PIN_MIN));
		System.out.println(User.isValidPIN(VALID_PIN_MAX));
		System.out.println(User.isValidBadgeID(VALID_BADGE_ID_MAX));
		System.out.println(User.isValidBadgeID(VALID_BADGE_ID_MIN));
		assertFalse(User.validateUserData(VALID_BADGE_ID_MIN, VALID_PIN_MAX, VALID_NAME, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_Name_Long()
	{
		assertFalse(User.validateUserData(VALID_BADGE_ID_MAX, null, INVALID_NAME_LONG, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_Name_Null()
	{
		assertFalse(User.validateUserData("", VALID_PIN_MAX, null, VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_Name_Empty()
	{
		assertFalse(User.validateUserData(VALID_BADGE_ID_MAX, "", "", VALID_NAME, VALID_ROLE));
	}

	@Test
	public final void testValidation_Name_Long2()
	{
		assertFalse(User.validateUserData(VALID_BADGE_ID_MAX, null, VALID_NAME, INVALID_NAME_LONG, VALID_ROLE));
	}

	@Test
	public final void testValidation_Name_Null2()
	{
		assertFalse(User.validateUserData("", VALID_PIN_MAX, VALID_NAME, null, VALID_ROLE));
	}

	@Test
	public final void testValidation_Name_Empty2()
	{
		assertFalse(User.validateUserData(VALID_BADGE_ID_MAX, "", VALID_NAME, "", VALID_ROLE));
	}

	@Test
	public final void testValidation_Role_Invalid()
	{
		assertFalse(User.validateUserData("", VALID_PIN_MAX, VALID_NAME, VALID_NAME, NULL_ROLE));
	}

	@Test
	public final void testIsValidPIN_Invalid()
	{
		assertFalse(User.isValidPIN("whatev"));
	}

	@Test
	public final void testIsValidPIN_Negative()
	{
		assertFalse(User.isValidPIN("-12345"));
	}

	@Test
	public final void testisValidPIN_Zero()
	{
		assertTrue(User.isValidPIN("000000"));
	}

	@Test
	public final void testIsValidBadgeID_Invalid()
	{
		assertFalse(User.isValidBadgeID("whatever"));
	}

	@Test
	public final void testIsValidBadgeID_Negative()
	{
		assertFalse(User.isValidBadgeID("-10000000"));
	}

	@Test
	public final void testIsValidBadgeID2()
	{
		assertTrue(User.isValidBadgeID("99999999"));
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
