package velho.controller;

import velho.view.AddUserView;
/**
 * Is a controller for {@link AddUserView} view.
 * 
 * @author Joona
 *
 */
public class AddUserController
{
	/**
	 * The add user view.
	 */
	private AddUserView view;

	
	/**
	 * @param viewi
	 */
	public AddUserController (final AddUserView viewi)
	{
		view = viewi;
	}

	/**
	 * Attempts to add a new user to the database.
	 * 
	 * @param userID user's badge id number
	 * @param userFirstName user's first name
	 * @param userLastName user's last name
	 * @param userRole user's role in the company
	 */
	public void addUser(String userID, String userFirstName, String userLastName, Object userRole)
	{
		if (DatabaseController.addUser(userID, userFirstName, userLastName, userRole)){

			PopupController.info("User created.");
		}
		else {
			PopupController.warning("Warning!");
		}
		System.out.println(userID +  " "  + userFirstName + " " + userLastName + " " +userRole);
	}
}
