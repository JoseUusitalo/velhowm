package test.controller;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import velho.controller.DatabaseController;
import velho.controller.UserController;
import velho.model.exceptions.NoDatabaseLinkException;

/**
 * Test for the {@link velho.controller.UserController} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class UserControllerTest
{
	private static UserController controller;

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

	@BeforeClass
	public static final void createController() throws NoDatabaseLinkException
	{
		DatabaseController.connectAndInitialize();
		controller = new UserController();
	}

	@Test
	public final void testAddUserValid()
	{
		fail("Do not know how to test GUI controllers yet.");
		/*
		 * DatabaseController.link();
		 * DatabaseController.initializeDatabase();
		 * assertTrue(controller.addUser(VALID_BADGE_ID, null, VALID_NAME, VALID_NAME, VALID_ROLE_NAME));
		 * DatabaseController.unlink();
		 */
	}
}
