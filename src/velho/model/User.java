package velho.model;

import velho.model.enums.UserRole;

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
	public static final int MAX_NAME_LENGTH = 128;

	/**
	 * The maximum value for a PIN code.
	 */
	public static final int MAX_PIN_VALUE = 999999;

	/**
	 * The maximum value for a badge ID code.
	 */
	public static final int MAX_BADGE_ID_VALUE = 99999999;

	/**
	 * The number of digits a badge ID must have.
	 */
	public static final int BADGE_ID_LENGTH = 8;

	/**
	 * The number of digits a PIN must have.
	 */
	public static final int PIN_LENGTH = 6;

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

	public User(final String identifier, final String firstName, final String lastName, final UserRole role)
	{
		if (identifier == null || identifier.isEmpty())
			throw new IllegalArgumentException("Invalid badge ID or PIN.");

		if (identifier.length() > 6)
			this.badgeID = identifier;
		else
			this.pin = identifier;

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
