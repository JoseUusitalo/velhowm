package velho.controller;

import javafx.scene.layout.GridPane;
import velho.model.User;
import velho.model.enums.Position;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.view.LoginView;

/**
 * Controls logging users in and out.
 * 
 * @author Edward &amp; Jose Uusitalo
 */
public class LoginController
{
	/**
	 * User currently logged in.
	 */
	private User currentUser;

	/**
	 * The {@link LoginView}.
	 */
	private LoginView view;

	/**
	 * The {@link UIController}.
	 */
	private UIController uiController;

	/**
	 * The {@link DebugController}.
	 */
	private DebugController debugController;

	/**
	 * @param uiController
	 */
	public LoginController(final UIController uiController)
	{
		view = new LoginView(this);
		this.uiController = uiController;
	}

	/**
	 * Attaches the debug controller to this controller.
	 * 
	 * @param debugController the {@link DebugController}
	 */
	public void setDebugController(final DebugController debugController)
	{
		this.debugController = debugController;
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
			System.out.println(currentUser.toString() + " logged in.");
			uiController.showMainMenu(currentUser.getRole());
			debugController.login();
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

	/**
	 * Forcibly logs a user in with the specified role.
	 * 
	 * @param userRoleName name of the role
	 * @return <code>true</code> if login was successful, or <code>false</code> if role name was invalid
	 * @throws NoDatabaseLinkException
	 */
	public boolean debugLogin(final String userRoleName) throws NoDatabaseLinkException
	{
		if (DatabaseController.getRoleID(userRoleName) == -1)
			return false;

		currentUser = UserController.getDebugUser(userRoleName);
		uiController.showMainMenu(currentUser.getRole());
		return true;
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

	/**
	 * Gets the login view.
	 * 
	 * @return the login view
	 */
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
			uiController.setView(Position.CENTER, view.getLoginView());
			uiController.setView(Position.BOTTOM, null);
			uiController.resetMainMenu();
			return false;
		}
		System.out.println(true);
		return true;
	}

	/**
	 * Gets the current logged in user.
	 * 
	 * @return the user currently logged in
	 */
	public User getCurrentUser()
	{
		return currentUser;
	}
}
