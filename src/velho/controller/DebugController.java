package velho.controller;

import javafx.stage.Stage;
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
	private LoginController loginController;
/**
 * @param loginController 
 * @param debugView
 */
	public DebugController(LoginController loginController)
	{
		this.loginController = loginController;
		view = new DebugWindow(this);
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
		loginController.debuglogin(userRoleName);
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
