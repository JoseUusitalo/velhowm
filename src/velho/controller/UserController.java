package velho.controller;

import java.sql.SQLException;

import javafx.scene.Node;
import velho.model.User;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.view.AddUserView;

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
	public UserController()
	{
		view = new AddUserView(this);
	}

	/**
	 * Attempts to add a new user to the database.
	 *
	 * @param userID user's badge id number
	 * @param userFirstName user's first name
	 * @param userLastName user's last name
	 * @param userRole user's role in the company
	 */

	public void addUser(String badgeID, String userPIN, String userFirstName, String userLastName, String userRoleName)
	{
		// Validate user data.
		try
		{
			if (User.validateUserData(badgeID, userPIN, userFirstName, userLastName, userRoleName))
			{
				try
				{
					System.out.println(badgeID + " " + userFirstName + " " + userLastName + " " + userRoleName);
					int roleID = DatabaseController.isValidRole(userRoleName);
					DatabaseController.addUser(badgeID, userPIN, userFirstName, userLastName, roleID);

					PopupController.info("User added.");
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
		System.out.println(badgeID + " " + userFirstName + " " + userLastName + " " + userRoleName);
	}

	public Node getView()
	{
		return view.getAddUserView();
	}
}
