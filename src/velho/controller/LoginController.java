package velho.controller;

import javafx.scene.layout.GridPane;
import velho.model.User;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.view.LoginView;

/**
 * Controlls the viewing of the log ins to the Application.
 * @author Edward
 *
 */
public class LoginController
{
	private User currentUser;
	private LoginView view;
	private UIController uiController;

	public LoginController(final UIController uiController)
	{
		view = new LoginView(this);
		this.uiController = uiController;
	}

	/**
	 * This will "identify" the user and allow access to the system.
	 *
	 * @param authentication
	 * is set at LoginView for authentication
	 * @return as true
	 */
	public void login(String authenticationString)
	{
		try
		{
			currentUser = DatabaseController.authenticate(authenticationString);
		}
		catch (NoDatabaseLinkException e)
		{
			PopupController.error("Database connection lost.");
			e.printStackTrace();
		}

		if (currentUser == null)
		{
			PopupController.warning("INVALID BADGE ID OR PASSWORD!");
		}
		else
		{
			PopupController.info("Welcome " + currentUser.toString() + "!");
			uiController.showMainMenu();
		}
	}

	/**
	 * Logs out the current user.
	 */
	public void logout()
	{
		System.out.println("Logged out.");
		currentUser = null;
		checkLogin();
	}

	public void debuglogin(String userRoleName)
	{
		System.out.println("debug " + userRoleName);

	}

	/**
	 * Checks if a user is logged in.
	 *
	 * @return <code>true</code> if a user is logged in
	 */
	public boolean isLoggedIn()
	{
		return currentUser != null;
	}

	public GridPane getView()
	{
		return view.getLoginView();
	}

	/**
	 * Checks if the user is logged in.
	 * If the user is not logged in, shows the login screen.
	 *
	 * @return <code>true</code> if the user is logged in
	 */
	public boolean checkLogin()
	{
		System.out.print("User is logged in: ");
		if (!isLoggedIn())
		{
			System.out.println(false);
			uiController.setView(view.getLoginView());
			return true;
		}
		System.out.println(true);
		return false;
	}

}
