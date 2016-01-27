/**
 *
 */
package test.model;

import static org.junit.Assert.*;

import org.junit.Test;

import velho.model.User;

/**
 * Tests for the {@link velho.model.User} class.
 *
 * @author Jose Uusitalo
 */
public class UserTest
{
	private final String VALID_BADGE_ID = "12345678";
	private final String INVALID_BADGE_ID_SHORT = "12345";
	private final String INVALID_BADGE_ID_LONG = "123456789";

	private final String VALID_PIN = "123456";
	private final String INVALID_PIN_SHORT = "12";
	private final String INVALID_PIN_LONG = "1234567";

	private final String VALID_NAME = "First-Name";
	private final String INVALID_NAME_LONG = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
	private final String INVALID_NAME_NULL = null;

	private final String VALID_ROLE_NAME = "Manager";
	private final String INVALID_ROLE_NAME = "Worker";

	@Test
	public final void testValidation_PIN()
	{
		assertEquals(true, User.validateUserData(null, VALID_PIN, VALID_NAME, VALID_NAME, VALID_ROLE_NAME));
	}

	@Test
	public final void testValidation_Badge()
	{
		assertEquals(true, User.validateUserData(VALID_BADGE_ID, "", VALID_NAME, VALID_NAME, VALID_ROLE_NAME));
	}

	@Test
	public final void testValidation_Both()
	{
		assertEquals(false, User.validateUserData(VALID_BADGE_ID, VALID_PIN, VALID_NAME, VALID_NAME, VALID_ROLE_NAME));
	}

	@Test
	public final void testValidation_BadgeID_LONG()
	{
		assertEquals(false, User.validateUserData(INVALID_BADGE_ID_LONG, VALID_PIN, VALID_NAME, VALID_NAME, VALID_ROLE_NAME));
	}

	@Test
	public final void testValidation_BadgeID_SHORT()
	{
		assertEquals(false, User.validateUserData(INVALID_BADGE_ID_SHORT, VALID_PIN, VALID_NAME, VALID_NAME, VALID_ROLE_NAME));
	}

	@Test
	public final void testValidation_PIN_LONG()
	{
		assertEquals(false, User.validateUserData(VALID_BADGE_ID, INVALID_PIN_LONG, VALID_NAME, VALID_NAME, VALID_ROLE_NAME));
	}

	@Test
	public final void testValidation_PIN_SHORT()
	{
		assertEquals(false, User.validateUserData(VALID_BADGE_ID, INVALID_PIN_SHORT, VALID_NAME, VALID_NAME, VALID_ROLE_NAME));
	}

	@Test
	public final void testValidation_Name_Long()
	{
		assertEquals(false, User.validateUserData(VALID_BADGE_ID, VALID_PIN, VALID_NAME, INVALID_NAME_LONG, VALID_ROLE_NAME));
	}

	@Test
	public final void testValidation_Name_NulL()
	{
		assertEquals(false, User.validateUserData(VALID_BADGE_ID, VALID_PIN, INVALID_NAME_NULL, VALID_NAME, VALID_ROLE_NAME));
	}


	@Test
	public final void testValidation_Role()
	{
		fail("Not yet implemented.");
	}
}
