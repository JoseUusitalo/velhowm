package velho.controller;

import javafx.scene.layout.GridPane;
import velho.model.User;
import velho.model.enums.Position;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.view.LoginView;
import velho.view.MainWindow;

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
	private static User currentUser;

	/**
	 * The {@link LoginView}.
	 */
	private static LoginView view;

	/**
	 * The {@link UIController}.
	 */
	private static UIController uiController;

	/**
	 * The {@link DebugController}.
	 */
	private static DebugController debugController;

	/**
	 * Attaches the controllers that the login controller uses.
	 *
	 * @param uiController the {@link UIController}
	 * @param debugController the {@link DebugController}
	 */
	public static void setControllers(final UIController uiController, DebugController debugController)
	{
		LoginController.uiController = uiController;
		LoginController.debugController = debugController;
		view = new LoginView();
	}

	/**
	 * This will "identify" the user and allow access to the system.
	 *
	 * @param authentication
	 * is set at LoginView for authentication
	 * @return as true
	 */
	public static void login(String authenticationString)
	{
		System.out.println("Attempting to log in with '" + authenticationString + "'");
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
			PopupController.warning("Invalid Badge ID or PIN.");
		}
		else
		{
			System.out.println(currentUser.toString() + " logged in.");
			uiController.showMainMenu(currentUser.getRole());

			if (MainWindow.DEBUG_MODE)
			{
				debugController.setLogInButton(false);
				debugController.setLogOutButton(true);
			}
		}
	}

	/**
	 * Logs out the current user.
	 */
	public static void logout()
	{
		if (MainWindow.DEBUG_MODE)
		{
			debugController.setLogInButton(true);
			debugController.setLogOutButton(false);
		}
		System.out.println(currentUser.toString() + " logged out.");
		currentUser = null;
		uiController.destroyViews();
		checkLogin();
	}

	/**
	 * Forcibly logs a user in with the specified role.
	 * Does nothing if {@link MainWindow#DEBUG_MODE} is <code>false</code>.
	 *
	 * @param userRoleName name of the role
	 * @return <code>true</code> if login was successful, or <code>false</code> if role name was invalid
	 * @throws NoDatabaseLinkException
	 */
	public static boolean debugLogin(final String userRoleName) throws NoDatabaseLinkException
	{
		if (MainWindow.DEBUG_MODE)
		{
			if (DatabaseController.getRoleID(userRoleName) == -1)
				return false;
			currentUser = UserController.getDebugUser(userRoleName);
			uiController.showMainMenu(currentUser.getRole());
			System.out.println(currentUser.toString() + " logged in.");
			return true;
		}

		return false;
	}

	/**
	 * Checks if a user is logged in.
	 *
	 * @return <code>true</code> if a user is logged in
	 */
	public static boolean isLoggedIn()
	{
		return currentUser != null;
	}

	/**
	 * Gets the login view.
	 *
	 * @return the login view
	 */
	public static GridPane getView()
	{
		return view.getLoginView();
	}

	/**
	 * Checks if the user is logged in.
	 * If the user is not logged in, shows the login screen.
	 *
	 * @return <code>true</code> if the user is logged in
	 */
	public static boolean checkLogin()
	{
		if (!isLoggedIn())
		{
			uiController.setView(Position.CENTER, view.getLoginView());
			uiController.setView(Position.BOTTOM, null);
			uiController.resetMainMenu();
			System.out.println("Login check failed.");
			return false;
		}
		System.out.println("Login check passed.");
		return true;
	}

	/**
	 * Gets the current logged in user.
	 *
	 * @return the user currently logged in
	 */
	public static User getCurrentUser()
	{
		return currentUser;
	}
}
