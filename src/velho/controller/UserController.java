package velho.controller;

import javafx.scene.Node;
import velho.view.AddUserView;
import velho.view.MainWindow;

import java.sql.SQLException;

import velho.model.Administrator;
import velho.model.Logistician;
import velho.model.Manager;
import velho.model.User;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.model.interfaces.UserRole;

/**
 * Is a controller for {@link AddUserView} view.
 *
 * @author Joona
 *
 */
public class UserController
{
	/**
	 * The add user view.
	 */
	private AddUserView view;

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
				try
				{
					System.out.println("Badge: " + badgeID + " PIN: " + userPIN + " First Name: " + userFirstName + " Last Name: " + userLastName + " Role: "
							+ userRoleName);
					int roleID = DatabaseController.getRoleID(userRoleName);

					DatabaseController.addUser(badgeID, userPIN, userFirstName, userLastName, roleID);

					PopupController.info("User created.");
					return true;
				}
				catch (NoDatabaseLinkException e)
				{
					e.printStackTrace();
				}
				catch (SQLException e)
				{
					PopupController.error("Invalid user data even after verification, please contact an administrator.");
					e.printStackTrace();
				}
				PopupController.info("User created.");
			}
			else
			{
				PopupController.warning("Invalid user data.");
			}
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
		return view.getAddUserView();
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
			return new User("Debug", "Account", stringToRole(userRoleName));
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
