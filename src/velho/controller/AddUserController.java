package velho.controller;

import velho.model.Database;
import velho.view.AddUserView;

public class AddUserController
{
	private AddUserView view;
	
	public AddUserController (final AddUserView viewi)
	{
		view = viewi; 
	}

	public void addUser(String userID, String userFirstName, String userLastName, Object userRole)
	{
		if (Database.addUser(userID, userFirstName, userLastName, userRole)){
			
			PopupController.info("User created.");		
		}
		else {
			PopupController.info("Warning!");
		}
		System.out.println(userID +  " "  + userFirstName + " " + userLastName + " " +userRole);
	}
}
