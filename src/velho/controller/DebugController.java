package velho.controller;

import javafx.stage.Stage;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.view.DebugWindow;

/**
 * Various debugging features.
 * 
 * @author Edward &amp; Jose Uusitalo
 */
public class DebugController
{
	/**
	 * The view is debugViewn.
	 */
	private DebugWindow view;
	private LoginController loginController;

	/**
	 * @param loginController
	 * @param debugView
	 * @throws NoDatabaseLinkException
	 */
	public DebugController(LoginController loginController) throws NoDatabaseLinkException
	{
		this.loginController = loginController;
		view = new DebugWindow(this, DatabaseController.getUserRoleNames());
	}

	public void createDebugWindow(Stage stage)
	{
		view.start(stage);
	}

	/**
	 * Here the login sets value to the buttons.
	 * @param userRoleName
	 */
	public void login(String userRoleName)
	{
		try
		{
			loginController.debuglogin(userRoleName);
			System.out.println("Logged in as " + userRoleName + ". (DEBUG)");
			view.setLogInButton(false);
			view.setLogOutButton(true);
		}
		catch (NoDatabaseLinkException e)
		{
			PopupController.error("Login failed, no database connection.");
			e.printStackTrace();
		}
	}

	/**
	 * Visually toggles the login/logout buttons in the debug window.
	 */
	public void login()
	{
		view.setLogInButton(false);
		view.setLogOutButton(true);
	}

	/**
	 * Sets the logout sets value to the buttons.
	 */
	public void logout()
	{
		// Only log out if a user is logged in, otherwise just visually toggle the buttons.
		if (loginController.isLoggedIn())
			loginController.logout();
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
