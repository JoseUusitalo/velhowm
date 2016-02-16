package velho.controller;

import javafx.scene.Node;
import velho.view.AddUserView;
import velho.view.MainWindow;

import velho.model.Administrator;
import velho.model.Logistician;
import velho.model.Manager;
import velho.model.User;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.model.interfaces.UserRole;

/**
 * A controller for managing users.
 *
 * @author Jose Uusitalo &amp; Joona
 */
@SuppressWarnings("static-method")
public class UserController
{
	/**
	 * The add user view.
	 */
	private AddUserView view;

	/**
	 * @throws NoDatabaseLinkException
	 */
	public UserController() throws NoDatabaseLinkException
	{
		view = new AddUserView(this, DatabaseController.getUserRoleNames());
	}

	/**
	 * Attempts to add a new user to the database.
	 *
	 * @param userID user's badge id number
	 * @param userFirstName user's first name
	 * @param userLastName user's last name
	 * @param userRole user's role in the company
	 */
	public boolean addUser(final String badgeID, final String userPIN, final String userFirstName, final String userLastName, final String userRoleName)
	{
		// Validate user data.
		try
		{
			if (User.validateUserData(badgeID, userPIN, userFirstName, userLastName, userRoleName))
			{
				System.out.println(
						"Badge: " + badgeID + " PIN: " + userPIN + " First Name: " + userFirstName + " Last Name: " + userLastName + " Role: " + userRoleName);
				int roleID = DatabaseController.getRoleID(userRoleName);

				if (DatabaseController.addUser(badgeID, userPIN, userFirstName, userLastName, roleID))
				{
					PopupController.info("User created.");
					return true;
				}

				PopupController.info("User already exists. Please make sure that the following criteria are met:\n" + "Every Badge ID must be unique.\n"
						+ "People with the same first and last name are allowed if their roles are different.\n"
						+ "The combination of the PIN, first name, and last name must be unique.");
				return false;
			}

			PopupController.warning("Invalid user data.");
		}
		catch (NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}

		return false;
	}

	/**
	 * Attempts to remove the specified user from the database.
	 *
	 * @param user user to remove
	 */
	public boolean removeUser(final User user)
	{
		System.out.println("Attempting to remove: " + user.toString());

		try
		{
			if (LoginController.getCurrentUser().getDatabaseID() == user.getDatabaseID())
			{
				if (PopupController.confirmation(
						"Are you sure you wish the delete your own user account? You will be logged out and be unable to log in again as a result of this action."))
				{
					if (DatabaseController.removeUser(user.getDatabaseID()))
					{
						LoginController.logout();
						PopupController.info("User removed: " + user.getFullDetails());
						return true;
					}

					PopupController.warning("User does not exist in the database.");
					System.out.println("Non-existent user: " + user.toString());
				}

				System.out.println("Not removed.");
				return false;
			}

			DatabaseController.removeUser(user.getDatabaseID());
			PopupController.info("User removed: " + user.getFullDetails());
			return true;
		}
		catch (NoDatabaseLinkException e)
		{
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Gets the view for adding users.
	 *
	 * @return the {@link AddUserView}
	 */
	public Node getView()
	{
		return view.getView();
	}

	/**
	 * Destroys the add user view.
	 */
	public void destroyView()
	{
		view.destroy();
	}

	/**
	 * Creates the temporary debug user for logging in through the debug window.
	 *
	 * @param userRoleName role to create the user as
	 * @return a {@link User} object or <code>null</code> if {@link MainWindow#DEBUG_MODE} is <code>false</code>
	 */
	public static User getDebugUser(String userRoleName)
	{
		if (MainWindow.DEBUG_MODE)
		{
			return new User(-1, "Debug", "Account", stringToRole(userRoleName));
		}

		return null;
	}

	/**
	 * Converts the given string to an object.
	 *
	 * @param userRoleName name of the user role to convert to an object
	 * @return a {@link UserRole} object
	 */
	public static UserRole stringToRole(final String userRoleName)
	{
		switch (userRoleName)
		{
			case "Administrator":
				return new Administrator();
			case "Manager":
				return new Manager();
			case "Logistician":
				return new Logistician();
			default:
				System.out.println("ERROR: Unknown role '" + userRoleName + "'.");
				return null;
		}
	}
}
