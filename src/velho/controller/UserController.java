package velho.controller;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;

import javafx.scene.Node;
import velho.controller.interfaces.UIActionController;
import velho.model.User;
import velho.model.enums.UserRole;
import velho.view.AddUserView;
import velho.view.MainWindow;

/**
 * A controller for managing users.
 *
 * @author Jose Uusitalo &amp; Joona Silvennoinen
 */
@SuppressWarnings("static-method")
public class UserController implements UIActionController
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(UserController.class.getName());

	/**
	 * Apache log4j logger: User.
	 */
	private static final Logger USRLOG = Logger.getLogger("userLogger");

	/**
	 * The add user view.
	 */
	private AddUserView view;

	/**
	 * @throws NoDatabaseLinkException
	 */
	public UserController()
	{
		view = new AddUserView(this, DatabaseController.getAllUserRoles());
	}

	/**
	 * Attempts to add a new user to the database.
	 *
	 * @param badgeID user's badge id number
	 * @param userFirstName user's first name
	 * @param userLastName user's last name
	 * @param userRole user's role in the company
	 * @param showPopup show popups?
	 * @return the created user or <code>null</code> if data was invalid
	 */
	public User createUser(final String badgeID, final String userPIN, final String userFirstName, final String userLastName, final UserRole userRole,
			final boolean showPopup)
	{
		if (validateUserData(badgeID, userPIN, userFirstName, userLastName, userRole))
		{
			User newUser;
			// If no pin is defined, use badge ID.
			if (userPIN == null || userPIN.isEmpty())
				newUser = new User(userFirstName, userLastName, null, badgeID, userRole);
			else
				newUser = new User(userFirstName, userLastName, userPIN, null, userRole);

			try
			{
				DatabaseController.saveOrUpdate(newUser);

				if (LoginController.getCurrentUser() != null)
					USRLOG.debug("Created a user.");
				// Else: running a JUnit test -> above line causes a null pointer error.

				if (showPopup)
					PopupController.info("User created.");

				return newUser;

			}
			catch (final ConstraintViolationException e)
			{
				SYSLOG.debug("User already exists.");

				if (showPopup)
				{
					PopupController.info("User already exists. Please make sure that the following criteria are met:\n" + "Every Badge ID must be unique.\n"
							+ "People with the same first and last name are allowed if their roles are different.\n"
							+ "The combination of the PIN, first name, and last name must be unique.");
				}

				return null;
			}
		}

		SYSLOG.trace("Invalid user data.");

		if (showPopup)
			PopupController.warning("Invalid user data.");

		return null;
	}

	/**
	 * Attempts to add a new user to the database.
	 *
	 * @param badgeID user's badge id number
	 * @param userFirstName user's first name
	 * @param userLastName user's last name
	 * @param userRoleName user's role in the company
	 * @return the created user or <code>null</code> if data was invalid
	 */
	public User createUser(final String badgeID, final String userPIN, final String userFirstName, final String userLastName, final UserRole userRole)
	{
		return createUser(badgeID, userPIN, userFirstName, userLastName, userRole, true);
	}

	/**
	 * Attempts to remove the specified user from the database.
	 *
	 * @param user user to remove
	 */
	public boolean deleteUser(final User user)
	{
		USRLOG.debug("Attempting to delete: " + user.getFullDetails());

		if (LoginController.getCurrentUser().getDatabaseID() == user.getDatabaseID())
		{
			if (PopupController.confirmation(
					"Are you sure you wish the delete your own user account? You will be logged out and be unable to log in again as a result of this action."))
			{
				DatabaseController.deleteUser(user);
				LoginController.logout();
				USRLOG.debug("User deleted themselves: " + user.getFullDetails());
				PopupController.info("Deleted user: " + user.getFullDetails());
				return true;
			}

			USRLOG.trace("Cancelled self-deletion confirmation.");
			return false;
		}

		DatabaseController.deleteUser(user);
		USRLOG.debug("User removed: " + user.getFullDetails());
		PopupController.info("User removed: " + user.getFullDetails());
		return true;
	}

	/**
	 * Gets the view for adding users.
	 *
	 * @return the {@link AddUserView}
	 */
	public Node getView()
	{
		return view.getView();
	}

	/**
	 * Destroys the add user view.
	 */
	public void destroyView()
	{
		view.destroy();
	}

	/**
	 * Creates the temporary debug user for logging in through the debug window.
	 *
	 * @param role the role to create the user as
	 * @return a {@link User} object or <code>null</code> if {@link MainWindow#DEBUG_MODE} is <code>false</code>
	 */
	public static User getDebugUser(final UserRole role)
	{
		if (MainWindow.DEBUG_MODE)
			return new User(-1, "Debug", "Account", "000000", null, role);

		return null;
	}

	/**
	 * Converts the given string to an object.
	 *
	 * @param userRoleName name of the user role to convert to an object
	 * @return a {@link UserRole} object
	 */
	@Deprecated
	public static UserRole stringToRole(final String userRoleName)
	{
		switch (userRoleName)
		{
			case "Administrator":
				return UserRole.ADMINISTRATOR;
			case "Manager":
				return UserRole.MANAGER;
			case "Logistician":
				return UserRole.LOGISTICIAN;
			default:
				SYSLOG.error("Unknown user role '" + userRoleName + "'.");
				return null;
		}
	}

	@Override
	public void updateAction(final Object data)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeAction(final Object data)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAction(final Object data)
	{
		USRLOG.debug("Delete user: " + data);
		deleteUser((User) data);
	}

	@Override
	public void addAction(final Object data)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void viewAction(final Object data)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void createAction(final Object data)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Validates the user data against the database requirements.
	 * Either a badge ID or a PIN must be defined.
	 * Both cannot be null.
	 * Both cannot be defined.
	 *
	 * @param badgeID RFID identification string of the user's RFID badge
	 * @param pin the pin string used to log in to the system if no RFID badge ID is provided
	 * @param firstName the first name of the user
	 * @param lastName the last name of the user
	 * @param roleName the name of the role of the user
	 *
	 * @return <code>true</code> if given information is valid
	 * @throws NoDatabaseLinkException
	 */
	public static boolean validateUserData(final String badgeID, final String pin, final String firstName, final String lastName, final UserRole role)
	{
		final boolean hasBadgeID = isValidBadgeID(badgeID);
		final boolean hasPIN = isValidPIN(pin);

		// Must have exactly one.
		if ((hasBadgeID && hasPIN) || (!hasBadgeID && !hasPIN))
			return false;

		// Name cannot be null, empty, or longer than maximum and length.
		if (firstName == null || firstName.isEmpty() || firstName.length() > User.MAX_NAME_LENGTH)
			return false;

		// Name cannot be null, empty, or longer than maximum and length.
		if (lastName == null || lastName.isEmpty() || lastName.length() > User.MAX_NAME_LENGTH)
			return false;

		// TODO: The role is not in the database at the moment.
		if (role == null)
			return false;

		return true;
	}

	/**
	 * Checks if the given PIN is valid.
	 * PINs must be numerical.
	 *
	 * @param pin PIN to check
	 * @return <code>true</code> if the pin is valid
	 */
	public static boolean isValidPIN(final String pin)
	{
		if (pin == null || pin.length() != User.PIN_LENGTH)
			return false;

		try
		{
			int value = Integer.parseInt(pin);
			return (value >= 0 && value <= User.MAX_PIN_VALUE);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}

	/**
	 * Checks if the given badge ID is valid.
	 * Badge IDs must be numerical.
	 *
	 * @param badgeID badge ID to check
	 * @return <code>true</code> if the badge ID is valid
	 */
	public static boolean isValidBadgeID(final String badgeID)
	{
		if (badgeID == null || badgeID.length() != User.BADGE_ID_LENGTH)
			return false;

		try
		{
			int value = Integer.parseInt(badgeID);
			return (value >= 0 && value <= User.MAX_BADGE_ID_VALUE);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}
}
