package velho.controller;

import org.apache.log4j.Logger;

import javafx.scene.Node;
import velho.controller.interfaces.UIActionController;
import velho.model.User;
import velho.model.enums.UserRole;
import velho.model.exceptions.NoDatabaseLinkException;
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
		view = new AddUserView(this, DatabaseController.getUserRoleNames());
	}

	/**
	 * Attempts to add a new user to the database.
	 *
	 * @param badgeID user's badge id number
	 * @param userFirstName user's first name
	 * @param userLastName user's last name
	 * @param userRoleName user's role in the company
	 */
	public boolean createUser(final String badgeID, final String userPIN, final String userFirstName, final String userLastName, final String userRoleName)
	{
		// Validate user data.
		try
		{
			if (User.validateUserData(badgeID, userPIN, userFirstName, userLastName, userRoleName))
			{
				final int roleID = DatabaseController.getRoleID(userRoleName);

				// TODO: User accounts related code needs rewriting. This should return the user object.

				if (DatabaseController.insertUser(badgeID, userPIN, userFirstName, userLastName, roleID))
				{
					USRLOG.debug("Created a user.");
					PopupController.info("User created.");
					return true;
				}

				SYSLOG.debug("User already exists.");
				PopupController.info("User already exists. Please make sure that the following criteria are met:\n" + "Every Badge ID must be unique.\n"
						+ "People with the same first and last name are allowed if their roles are different.\n"
						+ "The combination of the PIN, first name, and last name must be unique.");
				return false;
			}

			SYSLOG.trace("Invalid user data.");
			PopupController.warning("Invalid user data.");
		}
		catch (final NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}

		return false;
	}

	/**
	 * Attempts to remove the specified user from the database.
	 *
	 * @param user user to remove
	 */
	public boolean deleteUser(final User user)
	{
		USRLOG.debug("Attempting to delete: " + user.getFullDetails());

		try
		{
			if (LoginController.getCurrentUser().getDatabaseID() == user.getDatabaseID())
			{
				if (PopupController.confirmation(
						"Are you sure you wish the delete your own user account? You will be logged out and be unable to log in again as a result of this action."))
				{
					if (DatabaseController.deleteUser(user.getDatabaseID()))
					{
						LoginController.logout();
						USRLOG.debug("User deleted themselves: " + user.getFullDetails());
						PopupController.info("Deleted user: " + user.getFullDetails());
						return true;
					}

					SYSLOG.trace("Non-existent user: " + user.toString());
					PopupController.warning("User does not exist in the database.");
					return false;
				}

				USRLOG.trace("Cancelled self-deletion confirmation.");
				return false;
			}

			if (DatabaseController.deleteUser(user.getDatabaseID()))
			{
				USRLOG.debug("User removed: " + user.getFullDetails());
				PopupController.info("User removed: " + user.getFullDetails());
				return true;

			}
			SYSLOG.warn("Failed to remove user: " + user.getFullDetails());
			PopupController.info("Failed to remove user.");
			return false;
		}
		catch (final NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}

		return false;
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
			return new User(-1, "Debug", "Account", role);

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
}
