package velho.controller;

import org.apache.log4j.Logger;

import javafx.scene.Node;
import velho.controller.database.DatabaseController;
import velho.controller.interfaces.UIActionController;
import velho.model.User;
import velho.model.enums.UserRole;
import velho.model.strategies.UserValidationStrategy;
import velho.view.AddUserView;
import velho.view.ListView;
import velho.view.MainWindow;

/**
 * The singleton controller for managing {@link User} objects.
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
	 * A private inner class holding the class instance.
	 *
	 * @author Jose Uusitalo
	 */
	private static class Holder
	{
		/**
		 * The only instance of {@link UserController}.
		 */
		private static final UserController INSTANCE = new UserController();
	}

	/**
	 */
	private UserController()
	{
		// No need to instantiate this class.
	}

	/**
	 * Gets the instance of the {@link UserController}.
	 *
	 * @return the user controller
	 */
	public static synchronized UserController getInstance()
	{
		return Holder.INSTANCE;
	}

	/**
	 * Converts the given string to an object.
	 *
	 * @param userRoleName name of the user role to convert to an object
	 * @return a {@link UserRole} object
	 */
	@Deprecated
	public UserRole stringToRole(final String userRoleName)
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

	@Override
	public void recreateViews(final ListView listView)
	{
		// TODO refactor userlist into tabview
	}

	/**
	 * Attempts to add a new user to the database.
	 *
	 * @param badgeID user's badge id number
	 * @param userFirstName user's first name
	 * @param userLastName user's last name
	 * @param userRole user's role in the company
	 * @param showPopup show popups?
	 * @return the created user or <code>null</code> if data was invalid or user
	 *         already existed in the database
	 */
	public User createUser(final String badgeID, final String userPIN, final String userFirstName, final String userLastName, final UserRole userRole,
			final boolean showPopup)
	{
		try
		{
			final User newUser = new User(userFirstName, userLastName, userPIN, badgeID, userRole);

			if (DatabaseController.getInstance().getAllUsers().contains(newUser))
			{
				SYSLOG.debug("User already exists.");

				if (showPopup)
					PopupController.getInstance().info(LocalizationController.getInstance().getString("userAlreadyExistInfoPopUp"));

				return null;
			}

			DatabaseController.getInstance().saveOrUpdate(newUser);

			if (LoginController.getInstance().getCurrentUser() != null)
				USRLOG.debug("Created a user.");
			// Else: running a JUnit test -> above line causes a null pointer
			// error.

			if (showPopup)
				PopupController.getInstance().info(LocalizationController.getInstance().getString("userCreatedPopUpNotice"));

			return newUser;
		}
		catch (IllegalArgumentException e)
		{
			SYSLOG.debug("Invalid user data.");

			if (showPopup)
				PopupController.getInstance().warning(LocalizationController.getInstance().getString("invalidUserDataPopUp"));

			return null;
		}
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

		if (LoginController.getInstance().getCurrentUser().getDatabaseID() == user.getDatabaseID())
		{
			if (PopupController.getInstance().confirmation(LocalizationController.getInstance().getString("yourAccountDeletationConfirmationPopUp")))
			{

				if (DatabaseController.getInstance().deleteUser(user))
				{
					LoginController.getInstance().logout();
					USRLOG.debug("User deleted themselves: " + user.getFullDetails());
					PopupController.getInstance().info(LocalizationController.getInstance().getString("deletedUserInfoPopUp") + user.getFullDetails());
					return true;
				}

				USRLOG.debug("Failed to delete user: " + user.getFullDetails());
				PopupController.getInstance().info(LocalizationController.getInstance().getCompoundString("failedToDeleteUserNotice", user.getFullDetails()));
			}

			USRLOG.trace("Cancelled self-deletion confirmation.");
			return false;
		}

		if (PopupController.getInstance().confirmation(LocalizationController.getInstance().getString("yourAccountDeletationConfirmationCheckPopUp")))
		{
			if (DatabaseController.getInstance().deleteUser(user))
			{
				USRLOG.debug("User removed: " + user.getFullDetails());
				PopupController.getInstance().info(LocalizationController.getInstance().getString("userRemovedInfoPopUp") + user.getFullDetails());
				return true;
			}

			USRLOG.debug("Failed to delete user: " + user.getFullDetails());
			PopupController.getInstance().info(LocalizationController.getInstance().getCompoundString("failedToDeleteUserNotice", user.getFullDetails()));

			return false;
		}

		USRLOG.trace("Cancelled deletion confirmation.");
		return false;
	}

	/**
	 * Gets the view for adding users.
	 *
	 * @return the {@link AddUserView}
	 */
	public Node getView()
	{
		if (view == null)
			view = new AddUserView(this, DatabaseController.getInstance().getAllUserRoles());
		return view.getView();
	}

	/**
	 * Destroys the add user view.
	 */
	public void destroyView()
	{
		view = null;
		getView();
	}

	/**
	 * Creates the temporary debug user for logging in through the debug window.
	 *
	 * @param role the role to create the user as
	 * @return a {@link User} object or <code>null</code> if
	 *         {@link MainWindow#DEBUG_MODE} is <code>false</code>
	 */
	public User getDebugUser(final UserRole role)
	{
		if (MainWindow.DEBUG_MODE)
			return new User(-1, "Debug", "Account", "000000", null, role);

		return null;
	}

	/**
	 * Validates the user data against the contextual requirements.
	 *
	 * @param user the user to be validated
	 *
	 * @return <code>true</code> if the user properties are valid
	 */
	public boolean validateUser(final User user)
	{
		return new UserValidationStrategy().isValidUser(user);
	}

	/**
	 * Checks if the given PIN is valid.
	 * PINs must be numerical.
	 *
	 * @param pin PIN to check
	 * @return <code>true</code> if the pin is valid
	 */
	public boolean isValidPIN(final String pin)
	{
		if (pin == null || pin.length() != User.PIN_LENGTH)
			return false;

		try
		{
			int value = Integer.parseInt(pin);
			return value >= 0 && value <= User.MAX_PIN_VALUE;
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
	public boolean isValidBadgeID(final String badgeID)
	{
		if (badgeID == null || badgeID.length() != User.BADGE_ID_LENGTH)
			return false;

		try
		{
			int value = Integer.parseInt(badgeID);
			return value >= 0 && value <= User.MAX_BADGE_ID_VALUE;
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}

	public Node getUserListView(final UserRole currentUserRole)
	{
		// TODO refactor into tabview
		/*
		 * What is shown in the user list depends on your role.
		 */
		switch (currentUserRole)
		{
			case ADMINISTRATOR:
			case MANAGER:
				return ListController.getTableView(this, DatabaseController.getInstance().getPublicUserDataColumns(true),
						DatabaseController.getInstance().getAllUsers());
			case LOGISTICIAN:
				return ListController.getTableView(this, DatabaseController.getInstance().getPublicUserDataColumns(false),
						DatabaseController.getInstance().getAllUsers());
			case GUEST:
				break;
			default:
				SYSLOG.error("Unknown user role '" + currentUserRole.getName() + "'.");
		}

		return null;
	}
}
