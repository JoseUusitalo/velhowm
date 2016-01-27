package velho.controller;

import velho.view.DebugWindow;

/**
 * Controlls logins and log outs.
 * @author Edward
 *
 */
public class DebugController
{
	/**
	 * The view is debugViewn.
	 */
	private DebugWindow view;
/**
 * @param debugView
 */
	public DebugController(DebugWindow debugView)
	{
		view = debugView;
	}

	/**
	 * Here the login sets value to the buttons.
	 */
	public void login()
	{
		System.out.println("Logged in.");
		view.setLogInButton(false);
		view.setLogOutButton(true);
		// TODO Auto-generated method stub

	}

	/**
	 * Sets the logout sets value to the buttons.
	 */
	public void logout()
	{
		System.out.println("Logged out.");
		view.setLogInButton(true);
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
	 * The parameter for LogInButton is either false or true.
	 * @param visibility show log in button?
	 */
	public void setLogOutButton(boolean visibility)
	{
		view.setLogOutButton(visibility);
	}
}
