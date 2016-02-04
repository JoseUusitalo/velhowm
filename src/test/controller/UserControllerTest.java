package test.controller;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import javafx.application.Application;
import velho.controller.DatabaseController;
import velho.controller.PopupController;
import velho.controller.UserController;
import velho.model.JavaFXThreadingRule;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseLinkException;

/**
 * Test for the {@link velho.controller.UserController} class.
 *
 * @author Jose Uusitalo
 */
public class UserControllerTest
{
	@Rule public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

	private static UserController controller;

	private final String VALID_BADGE_ID = "99955999";
	private final String INVALID_BADGE_ID_SHORT = "15";
	private final String INVALID_BADGE_ID_LONG = "1234567891";

	private final String VALID_PIN_MAX = "999999";
	private final String VALID_PIN_MIN = "000000";
	private final String INVALID_PIN_SHORT = "12";
	private final String INVALID_PIN_LONG = "1234567";

	private final String VALID_NAME = "First-Name";
	private final String INVALID_NAME_LONG = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

	private final String VALID_ROLE_NAME = "Manager";
	private final String INVALID_ROLE_NAME = "Worker";

	@BeforeClass
	public static final void createController() throws NoDatabaseLinkException, ClassNotFoundException, ExistingDatabaseLinkException
	{
		DatabaseController.connectAndInitialize();
		controller = new UserController();
	}

	@AfterClass
	public static final void closeDatabase() throws NoDatabaseLinkException
	{
		DatabaseController.unlink();
	}

	@Test
	public final void testAddUserValid()
	{
		assertTrue(controller.addUser(VALID_BADGE_ID, null, VALID_NAME, VALID_NAME, VALID_ROLE_NAME));
	}
}
