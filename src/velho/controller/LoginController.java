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
	 * User Logs in via this command & it prints out user inputed text.
	 * @param string to print out what is inside the string.
	 */
	public void login(String string)
	{
		System.out.println(string);
		view.setLogInButton(true);
		view.setLogOutButton(false);
		// TODO Auto-generated method stub

	}

	/**
	 * Sets the value to "Log Out" and "Log In" when loging out.
	 */
	public void logout()
	{
		System.out.println("Logged out.");
		view.setLogInButton(false);
		view.setLogOutButton(false);
	}

	/**
	 * The parameter for LogInButton is either false or true.
	 *
	 * @param visibility show log in button?
	 */
	public void setLogInButton(boolean visibility)
	{
		view.setLogInButton(visibility);
	}

	/**
	 * The parameter for LogOutButton is either false or true.
	 *
	 * @param visibility show log in button?
	 */
	public void setLogOutButton(boolean visibility)
	{
		view.setLogOutButton(visibility);
	}
}
