package velho.controller;

import velho.view.LoginView;

/**
 * Controlls the viewing of the log ins to the Application.
 * @author Edward
 *
 */
public class LoginController
{
	private LoginView view;

	public LoginController(LoginView loginview)
	{
		view = loginview;
	}

	/**
	 * This will "identify" the user and allow access to the system.
	 *
	 * @param authentication
	 *            is set at LoginView for authentication
	 * @return as true
	 */
	public void login(String authenticationString)
	{
		Object currentUser = DatabaseController.authenticate(authenticationString);
		System.out.println(authenticationString);
		if (currentUser == null){
			PopupController.warning("INVALID BADGE ID OR PASSWORD!");
		}
	}

	/**
	 * Sets the value to "Log Out" and "Log In" when loging out.
	 */
	public void logout()
	{
		System.out.println("Logged out.");
	}

}
