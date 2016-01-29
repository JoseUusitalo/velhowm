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

	public boolean addUser(String badgeID, String userPIN, String userFirstName, String userLastName, String userRoleName)
	{
		// Validate user data.
		try
		{
			if (User.validateUserData(badgeID, userPIN, userFirstName, userLastName, userRoleName))
			{
				try
				{
					System.out.println(badgeID + " " + userFirstName + " " + userLastName + " " + userRoleName);
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

	public Node getView()
	{
		return view.getAddUserView();
	}

	public static User getDebugUser(String userRoleName)
	{
		return new User(0, "Debug", "Account", stringToRole(userRoleName));
	}

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
