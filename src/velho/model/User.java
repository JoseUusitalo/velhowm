package velho.model;

import velho.controller.DatabaseController;
import velho.model.enums.UserRole;
import velho.model.exceptions.NoDatabaseLinkException;

/**
 * A User represents the person using this system.
 *
 * @author Jose Uusitalo
 */
public class User implements Comparable<User>
{
	/**
	 * The maximum number of characters a first or last name may have.
	 */
	private static final int MAX_NAME_LENGTH = 128;

	/**
	 * The maximum value for a PIN code.
	 */
	private static final int MAX_PIN_VALUE = 999999;

	/**
	 * The maximum value for a badge ID code.
	 */
	private static final int MAX_BADGE_ID_VALUE = 99999999;

	/**
	 * The number of digits a badge ID must have.
	 */
	private static final int BADGE_ID_LENGTH = 8;

	/**
	 * The number of digits a PIN must have.
	 */
	private static final int PIN_LENGTH = 6;

	/**
	 * The database row ID of this user.
	 */
	private int databaseID;

	/**
	 * The first name of this user.
	 */
	private String firstName;

	/**
	 * The last name of this user.
	 */
	private String lastName;

	/**
	 * The ID number of the user's badge.
	 */
	private String badgeID;

	/**
	 * The secret PIN of the user.
	 */
	private String pin;

	/**
	 * The role of this user.
	 */
	private UserRole role;

	/**
	 * @param databaseID
	 * @param firstName
	 * @param lastName
	 * @param role
	 */
	public User(final int databaseID, final String firstName, final String lastName, final UserRole role)
	{
		this.databaseID = databaseID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = role;
	}

	/**
	 */
	public User()
	{
		// For Hibernate.
	}

	/*
	 * STATIC METHODS
	 */

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
	public static boolean validateUserData(final String badgeID, final String pin, final String firstName, final String lastName, final String roleName)
			throws NoDatabaseLinkException
	{
		final boolean hasBadgeID = isValidBadgeID(badgeID);
		final boolean hasPIN = isValidPIN(pin);

		// Must have exactly one.
		if ((hasBadgeID && hasPIN) || (!hasBadgeID && !hasPIN))
			return false;

		// Name cannot be null, empty, or longer than maximum and length.
		if (firstName == null || firstName.isEmpty() || firstName.length() > MAX_NAME_LENGTH)
			return false;

		// Name cannot be null, empty, or longer than maximum and length.
		if (lastName == null || lastName.isEmpty() || lastName.length() > MAX_NAME_LENGTH)
			return false;

		// The role must exist in the database.
		if (DatabaseController.getRoleID(roleName) == -1)
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
		if (pin == null || pin.length() != PIN_LENGTH)
			return false;

		try
		{
			int value = Integer.parseInt(pin);
			return (value >= 0 && value <= MAX_PIN_VALUE);
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
		if (badgeID == null || badgeID.length() != BADGE_ID_LENGTH)
			return false;

		try
		{
			int value = Integer.parseInt(badgeID);
			return (value >= 0 && value <= MAX_BADGE_ID_VALUE);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}

	/*
	 * INSTANCE METHODS
	 */

	/**
	 * Returns the user data in the following format:
	 * <code>firstname lastname [rolename | databaseid]</code>
	 */
	@Override
	public String toString()
	{
		return firstName + " " + lastName + " [" + role.toString() + " | " + databaseID + "]";
	}

	@Override
	public boolean equals(final Object o)
	{
		if (!(o instanceof User))
			return false;

		final User u = (User) o;

		if (this.getDatabaseID() <= 0)
			return this == u;

		return this.getDatabaseID() == u.getDatabaseID();
	}

	@Override
	public int compareTo(final User user)
	{
		return this.getFullName().compareToIgnoreCase(user.getFullName());
	}

	/**
	 * Gets the first and last name of this user.
	 *
	 * @return the full name of this user
	 */
	public String getFullName()
	{
		return firstName + " " + lastName;
	}

	/**
	 * Gets the user data in the following format:
	 * <code>firstname lastname (rolename)</code>
	 *
	 * @return the full user data
	 */
	public String getFullDetails()
	{
		return firstName + " " + lastName + " (" + role.toString() + ")";
	}

	/**
	 * Gets the name of the role of this user.
	 *
	 * @return the role name of this user
	 */
	public String getRoleName()
	{
		return role.getName();
	}

	/**
	 * Gets the database ID of this user.
	 *
	 * @return the database ID of this user
	 */
	public int getDatabaseID()
	{
		return databaseID;
	}

	/**
	 * Sets the database ID of this user.
	 *
	 * @param databaseID the new database ID
	 */
	public void setDatabaseID(final int databaseID)
	{
		this.databaseID = databaseID;
	}

	/**
	 * Gets the first name of this user.
	 *
	 * @return the first name of this user
	 */
	public String getFirstName()
	{
		return firstName;
	}

	/**
	 * Sets the first name of this user.
	 *
	 * @param firstName the new first name of this user
	 */
	public void setFirstName(final String firstName)
	{
		this.firstName = firstName;
	}

	/**
	 * Gets the last name of this user.
	 *
	 * @return the last name of this user
	 */
	public String getLastName()
	{
		return lastName;
	}

	/**
	 * Sets the last name of this user.
	 *
	 * @param lastName the new last name of this user
	 */
	public void setLastName(final String lastName)
	{
		this.lastName = lastName;
	}

	/**
	 * Gets the role of this user.
	 *
	 * @return the role of this user
	 */
	public UserRole getRole()
	{
		return role;
	}

	/**
	 * Sets the role of this user.
	 *
	 * @param role the new role of this user
	 */
	public void setRole(final UserRole role)
	{
		this.role = role;
	}

	/**
	 * Gets the unique badge ID of this user.
	 *
	 * @return the badge ID
	 */
	public String getBadgeID()
	{
		return badgeID;
	}

	/**
	 * Sets the unique badge ID of this user.
	 *
	 * @param badgeID the new badge ID
	 */
	public void setBadgeID(final String badgeID)
	{
		this.badgeID = badgeID;
	}

	/**
	 * Get the login PIN of this user.
	 *
	 * @return the user login PIN
	 */
	public String getPin()
	{
		return pin;
	}

	/**
	 * Sets the login PIN of this user.
	 *
	 * @param pin the new PIN
	 */
	public void setPin(final String pin)
	{
		this.pin = pin;
	}
}
