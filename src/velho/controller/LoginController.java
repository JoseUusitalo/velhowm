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
	private DebugController debugController;

	public LoginController(final UIController uiController)
	{
		view = new LoginView(this);
		this.uiController = uiController;
	}
	
	/**
	 * Attaches the debug controller to this class.
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
			uiController.showMainMenu();
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

	public boolean debuglogin(String userRoleName) throws NoDatabaseLinkException
	{
		if (DatabaseController.getRoleID(userRoleName) == -1)
			return false;
		
		currentUser = UserController.getDebugUser(userRoleName);
		uiController.showMainMenu();
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
			return false;
		}
		System.out.println(true);
		return true;
	}

	public User getCurrentUser()
	{
		return currentUser;
	}
}
