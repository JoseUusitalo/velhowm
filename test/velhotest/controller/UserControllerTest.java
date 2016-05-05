package velhotest.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javafx.collections.ObservableList;
import velho.controller.UserController;
import velho.controller.database.DatabaseController;
import velho.controller.database.LogDatabaseController;
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
	 * Creates the log database if needed and connects to it.
	 * Loads the sample data into the database if it does not yet exist.
	 *
	 * @throws Exception
	 */
	@BeforeClass
	public static final void init() throws Exception
	{
		System.out.println("\n\n---- UserControllerTest BeforeClass ----\n\n");
		LogDatabaseController.connectAndInitialize();
		DatabaseController.link();
		DatabaseController.loadSampleData();
		System.out.println("\n\n---- UserControllerTest Start ----\n\n");
	}

	/**
	 * Unlinks from both databases.
	 */
	@AfterClass
	public static final void unlinkDatabases() throws Exception
	{
		System.out.println("\n\n---- UserControllerTest AfterClass ----\n\n");
		DatabaseController.unlink();
		LogDatabaseController.unlink();
		System.out.println("\n\n---- UserControllerTest Done ----\n\n");
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

		assertTrue(DatabaseController.deleteUser(newUser));
	}

	@Test
	public final void testAddUserDuplicate()
	{
		System.out.println("[UserControllerTest] testAddUserValid()");
		final User newUser = controller.createUser(VALID_BADGE_ID, null, VALID_NAME, VALID_NAME, VALID_ROLE, false);
		assertNotNull(newUser);

		final User newUser2 = controller.createUser(VALID_BADGE_ID, null, VALID_NAME, VALID_NAME, VALID_ROLE, false);

		assertNull(newUser2);

		assertTrue(DatabaseController.deleteUser(newUser));
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
			assertFalse(DatabaseController.deleteUser(newUser));
	}

	@Test
	public final void testAddUser_RemoveUser()
	{
		final int newUserID = controller.createUser("", "000001", "A very UNIQUE! n4m3", "Some text here!", UserRole.MANAGER, false).getDatabaseID();
		final User dbUser = DatabaseController.getUserByID(newUserID);

		assertTrue(dbUser.getFirstName().equals("A very UNIQUE! n4m3"));

		assertTrue(DatabaseController.deleteUser(dbUser));
		assertEquals(null, DatabaseController.getUserByID(newUserID));
	}

}
