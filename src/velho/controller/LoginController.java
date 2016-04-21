package velho.controller;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

import javafx.scene.layout.VBox;
import velho.model.User;
import velho.model.enums.Position;
import velho.model.enums.UserRole;
import velho.view.LoginView;
import velho.view.MainWindow;

/**
 * Controls logging users in and out.
 *
 * @author Jose Uusitalo &amp; Edward Puustinen
 */
public class LoginController
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(LoginController.class.getName());

	/**
	 * Apache log4j logger: User.
	 */
	private static final Logger USRLOG = Logger.getLogger("userLogger");

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
	 * Destroys the login view.
	 */
	private static void destroyView()
	{
		view.reCreate();
		view = null;
	}

	/**
	 * Attaches the controllers that the login controller uses.
	 *
	 * @param uiController the {@link UIController}
	 * @param debugController the {@link DebugController}
	 */
	public static void setControllers(final UIController uiController, final DebugController debugController)
	{
		LoginController.uiController = uiController;
		LoginController.debugController = debugController;
	}

	/**
	 * Validates the badge ID and logs the user into the system.
	 *
	 * @param badgeString use RFID badge identification number
	 * @return <code>true</code> if login was successful, or <code>false</code> if debug mode was disabled
	 */
	public static boolean login(final String badgeString)
	{
		SYSLOG.info("Attempting to log in with: " + badgeString);

		if (UserController.isValidBadgeID(badgeString))
		{
			currentUser = DatabaseController.authenticateBadgeID(badgeString);

			// Valid credentials.
			if (currentUser != null)
			{
				// Put the user database ID into the MDC thing for log4j.
				MDC.put("user_id", currentUser.getDatabaseID());

				USRLOG.info(currentUser.toString() + " logged in with a badge.");
				uiController.showMainMenu(currentUser.getRole());
				destroyView();

				if (MainWindow.DEBUG_MODE)
					debugController.setLogInButtonVisiblity(false);

				return true;
			}

			SYSLOG.debug("Incorrect Badge ID.");
			PopupController.warning("Incorrect Badge ID.");
		}
		else
		{
			SYSLOG.debug("Invalid Badge ID.");
			PopupController.warning("Invalid Badge ID.");
		}

		return false;
	}

	/**
	 * Validates the given authentication data and logs the user into the system.
	 *
	 * @param firstName the first name of the user
	 * @param lastName the last name of the user
	 * @param pin login PIN string
	 */
	public static boolean login(final String firstName, final String lastName, final String authenticationString)
	{
		if (firstName.isEmpty() || lastName.isEmpty())
			return login(authenticationString);

		SYSLOG.info("Attempting to log in with: " + firstName + " " + lastName + " : " + authenticationString);

		if (UserController.isValidPIN(authenticationString))
		{
			currentUser = DatabaseController.authenticatePIN(firstName, lastName, authenticationString);

			// Valid credentials.
			if (currentUser != null)
			{
				// Put the user database ID into the MDC thing for log4j.
				MDC.put("user_id", currentUser.getDatabaseID());

				USRLOG.info(currentUser.toString() + " logged in with PIN.");
				uiController.showMainMenu(currentUser.getRole());
				destroyView();

				if (MainWindow.DEBUG_MODE)
					debugController.setLogInButtonVisiblity(false);

				return true;
			}

			SYSLOG.info("Incorrect PIN or Names.");
			PopupController.warning("Incorrect PIN or names.");
		}
		else
		{
			SYSLOG.info("Invalid PIN.");
			PopupController.warning("Invalid PIN.");
		}

		return false;
	}

	/**
	 * Logs out the current user.
	 */
	public static void logout()
	{
		if (MainWindow.DEBUG_MODE)
			debugController.setLogInButtonVisiblity(true);

		USRLOG.info(currentUser.toString() + " logged out.");

		// Remove the user id from the log4j MDC thing.
		MDC.remove("user_id");

		currentUser = null;
		uiController.destroyAllViews();
		checkLogin();
	}

	/**
	 * Forcibly logs a user in with the specified role.
	 * Does nothing if {@link MainWindow#DEBUG_MODE} is <code>false</code>.
	 *
	 * @param userRoleName name of the role
	 * @return <code>true</code> if login was successful, or <code>false</code> if debug mode was disabled
	 */
	public static boolean debugLogin(final UserRole role)
	{
		if (MainWindow.DEBUG_MODE)
		{
			currentUser = UserController.getDebugUser(role);

			// Put the user database ID into the MDC thing for log4j.
			MDC.put("user_id", currentUser.getDatabaseID());

			USRLOG.info(currentUser.toString() + " logged in via Debug Window.");

			uiController.showMainMenu(currentUser.getRole());

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
	public static VBox getView()
	{
		if (view == null)
			view = new LoginView();
		return view.getView();
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
			uiController.setView(Position.CENTER, getView());
			uiController.setView(Position.BOTTOM, null);
			uiController.resetMainMenu();
			SYSLOG.debug("Login check failed.");

			return false;
		}

		SYSLOG.debug("Login check passed.");

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

	/**
	 * Checks if the currently logged in user's role is greater than or equal to the given role.
	 *
	 * @param role role to check against
	 * @return <code>true</code> if logged in user's role is greater than or equal to the given role, <code>false</code>
	 * if user is not logged in
	 */
	public static boolean userRoleIsGreaterOrEqualTo(final UserRole role)
	{
		if (isLoggedIn())
			return currentUser.getRole().compareTo(role) >= 0;
		return false;
	}

	/**
	 * Checks if the currently logged in user's role is the given role.
	 *
	 * @param role role to check against
	 * @return <code>true</code> if logged in user's role is the given role, <code>false</code> if user is not logged in
	 */
	public static boolean userRoleIs(final UserRole role)
	{
		if (isLoggedIn())
			return currentUser.getRole().compareTo(role) == 0;
		return false;
	}
}
