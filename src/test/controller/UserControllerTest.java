package test.controller;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javafx.collections.ObservableList;
import velho.controller.DatabaseController;
import velho.controller.UserController;
import velho.model.JavaFXThreadingRule;
import velho.model.User;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseLinkException;

/**
 * Test for the {@link velho.controller.UserController} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class UserControllerTest
{
	@Rule
	public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();

	private static UserController controller;

	private final String VALID_BADGE_ID = "99955999";
	private final String VALID_PIN = "003000";
	private final String VALID_NAME = "First-Name";
	private final String VALID_ROLE_NAME = "Manager";
	private final String INVALID_ROLE_NAME = "Worker";

	@Before
	public final void createController() throws NoDatabaseLinkException, ClassNotFoundException, ExistingDatabaseLinkException
	{
		System.out.println("[UserControllerTest] Relinking database before test.");
		DatabaseController.connectAndInitialize();
		controller = new UserController();
		System.out.println("[UserControllerTest] Begin test.");
	}

	@After
	public final void closeDatabase()
	{
		System.out.println("[UserControllerTest] End test.");
		try
		{
			DatabaseController.unlink();
			System.out.println("[UserControllerTest] Unlinking database after test.");
		}
		catch (NoDatabaseLinkException e)
		{
			// Ignored.
		}
	}

	/*
	 *
	 * IMPORTANT NOTE ABOUT TESTING.
	 *
	 * THE DATABASE MUST BE UNLINKED IMMEDIATELY AFTER YOU HAVE FINISHED USING IT.
	 *
	 * IF A TEST FAILS AND THE DATABASE CONNECTION IS STILL LINKED, ALL FURTHER ATTEMPTS TO LINK AGAIN WILL FAIL.
	 *
	 */

	@Test
	public final void testAddUserValid() throws NoDatabaseLinkException
	{
		System.out.println("[UserControllerTest] testAddUserValid()");
		assertTrue(controller.addUser(VALID_BADGE_ID, null, VALID_NAME, VALID_NAME, VALID_ROLE_NAME));

		boolean exists = false;
		ObservableList<Object> users = DatabaseController.getObservableUsers();
		DatabaseController.unlink();

		for (final Object user : users)
		{
			if (((User) user).getFullDetails().equals(VALID_NAME + " " + VALID_NAME + " (" + VALID_ROLE_NAME + ")"))
			{
				exists = true;
				break;
			}
		}

		if (!exists)
			fail("User was not added to the database.");
	}

	@Test
	public final void testAddUserInvalid() throws NoDatabaseLinkException
	{
		System.out.println("[UserControllerTest] testAddUserInvalid()");
		assertFalse(controller.addUser(null, VALID_PIN, VALID_NAME, VALID_NAME, INVALID_ROLE_NAME));

		boolean exists = false;
		ObservableList<Object> users = DatabaseController.getObservableUsers();
		DatabaseController.unlink();

		for (final Object user : users)
		{
			if (((User) user).getFullDetails().equals(VALID_NAME + " " + VALID_NAME + " (" + INVALID_ROLE_NAME + ")"))
			{
				exists = true;
				break;
			}
		}

		if (exists)
			fail("Invalid user added to the database.");
	}
}
