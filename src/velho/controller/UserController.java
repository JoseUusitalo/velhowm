package velho.controller;

import javafx.scene.Node;
import velho.view.AddUserView;
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

	/**
	 * @param viewi
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
				try
				{
					System.out.println("Badge: " + badgeID + " PIN: " + userPIN + " First Name: " + userFirstName + " Last Name: " + userLastName + " Role: "
							+ userRoleName);
					int roleID = DatabaseController.getRoleID(userRoleName);

					DatabaseController.addUser(badgeID, userPIN, userFirstName, userLastName, roleID);

					PopupController.info("User added.");
					return true;
				}
				catch (NoDatabaseLinkException e)
				{
					e.printStackTrace();
				}
				catch (SQLException e)
				{
					PopupController.warning("Invalid user data.");
					e.printStackTrace();
				}
				PopupController.info("User created.");
			}
			else
			{
				PopupController.info("Warning!");
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
	 * @return a {@link User} object
	 */
	public static User getDebugUser(String userRoleName)
	{
		return new User("Debug", "Account", stringToRole(userRoleName));
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
