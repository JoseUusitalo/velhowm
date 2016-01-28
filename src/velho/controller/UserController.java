package velho.controller;

import java.sql.SQLException;

import velho.model.User;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.view.AddUserView;

public class UserController
{
	private AddUserView view;

	public UserController(final AddUserView viewi)
	{
		view = viewi;
	}

	public void addUser(String badgeID, String userPIN, String userFirstName, String userLastName, String userRoleName)
	{
		// Validate user data.
		try
		{
			if (User.validateUserData(badgeID, userPIN, userFirstName, userLastName, userRoleName))
			{
				try
				{
					int roleID = DatabaseController.isValidRole(userRoleName);
					DatabaseController.addUser(badgeID, userPIN, userFirstName, userLastName, roleID);
				}
				catch (NoDatabaseLinkException e)
				{
					e.printStackTrace();
				}
				catch (SQLException e)
				{
					PopupController.info("Invalid user data.");
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
}
