package test.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.text.ParseException;

import org.junit.BeforeClass;
import org.junit.Test;

import javafx.collections.ObservableList;
import velho.controller.DatabaseController;
import velho.controller.UserController;
import velho.model.User;
import velho.model.enums.UserRole;

/**
 * Test for the {@link velho.controller.UserController} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class UserControllerTest
{
	private static UserController controller = new UserController();

	private final String VALID_BADGE_ID = "99955999";
	private final String VALID_PIN = "003000";
	private final String VALID_NAME = "First-Name";
	private final UserRole VALID_ROLE = UserRole.MANAGER;
	private final UserRole NULL_ROLE = null;

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
	public final void testAddUserValid()
	{
		System.out.println("[UserControllerTest] testAddUserValid()");
		final User newUser = controller.createUser(VALID_BADGE_ID, null, VALID_NAME, VALID_NAME, VALID_ROLE, false);
		assertNotNull(newUser);

		boolean exists = false;
		ObservableList<Object> users = DatabaseController.getAllUsers();

		for (final Object user : users)
		{
			if (((User) user).equals(newUser))
			{
				exists = true;
				break;
			}
		}

		if (!exists)
			fail("User was not added to the database.");

		DatabaseController.deleteUser(newUser);
	}

	@Test
	public final void testAddUserDuplicate()
	{
		System.out.println("[UserControllerTest] testAddUserValid()");
		final User newUser = controller.createUser(VALID_BADGE_ID, null, VALID_NAME, VALID_NAME, VALID_ROLE, false);
		assertNotNull(newUser);

		final User newUser2 = controller.createUser(VALID_BADGE_ID, null, VALID_NAME, VALID_NAME, VALID_ROLE, false);
		assertNull(newUser2);

		DatabaseController.deleteUser(newUser);
	}

	@Test
	public final void testAddUserInvalid()
	{
		System.out.println("[UserControllerTest] testAddUserInvalid()");
		final User newUser = controller.createUser(null, VALID_PIN, VALID_NAME, VALID_NAME, NULL_ROLE, false);
		assertNull(newUser);

		boolean exists = false;
		ObservableList<Object> users = DatabaseController.getAllUsers();

		for (final Object user : users)
		{
			if (((User) user).getFullDetails().equals(VALID_NAME + " " + VALID_NAME + " (" + NULL_ROLE + ")"))
			{
				exists = true;
				break;
			}
		}

		if (exists)
		{
			DatabaseController.deleteUser(newUser);
			fail("Invalid user added to the database.");
		}
	}

	@Test
	public final void testAddUser_RemoveUser()
	{
		final int newUserID = controller.createUser("", "000001", "A very UNIQUE! n4m3", "Some text here!", UserRole.MANAGER, false).getDatabaseID();
		final User dbUser = DatabaseController.getUserByID(newUserID);

		assertTrue(dbUser.getFirstName().equals("A very UNIQUE! n4m3"));

		DatabaseController.deleteUser(dbUser);
		assertEquals(null, DatabaseController.getUserByID(newUserID));
	}

}
