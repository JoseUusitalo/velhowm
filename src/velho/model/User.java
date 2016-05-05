package velho.model;

import java.util.UUID;

import velho.controller.DatabaseController;
import velho.controller.UserController;
import velho.model.enums.UserRole;

/**
 * A User represents the person using this system.
 *
 * @author Jose Uusitalo
 */
public class User extends AbstractDatabaseObject
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
	 * @param uuid
	 * @param firstName
	 * @param lastName
	 * @param badgeID
	 * @param pin
	 * @param role
	 */
	public User(final int databaseID, final UUID uuid, final String firstName, final String lastName, final String pin, final String badgeID,
			final UserRole role)
	{
		setDatabaseID(databaseID);
		setUuid(uuid);
		this.firstName = firstName;
		this.lastName = lastName;
		this.badgeID = badgeID;
		this.pin = pin;
		this.role = role;

		System.out.println("new user " + firstName + " " + pin + " " + role);

		if (!UserController.validateUserData(this.badgeID, this.pin, firstName, lastName, role))
			throw new IllegalArgumentException("Invalid user data");
	}

	/**
	 * @param databaseID
	 * @param firstName
	 * @param lastName
	 * @param badgeID
	 * @param pin
	 * @param role
	 */
	public User(final int databaseID, final String firstName, final String lastName, final String pin, final String badgeID, final UserRole role)
	{
		this(databaseID, UUID.randomUUID(), firstName, lastName, pin, badgeID, role);
	}

	public User(final int databaseID, final String firstName, final String lastName, final String pin, final String badgeID, final String roleName)
	{
		this(databaseID, UUID.randomUUID(), firstName, lastName, pin, badgeID, DatabaseController.getUserByName(roleName));
	}

	/**
	 * @param databaseID
	 * @param firstName
	 * @param lastName
	 * @param badgeID
	 * @param pin
	 * @param role
	 */
	public User(final String firstName, final String lastName, final String pin, final String badgeID, final UserRole role)
	{
		this(0, firstName, lastName, pin, badgeID, role);
	}

	/**
	 */
	public User()
	{
		// For Hibernate.
		setUuid(UUID.randomUUID());
	}

	/**
	 * Returns the user data in the following format:
	 * <code>firstname lastname [rolename | databaseid]</code>
	 */
	@Override
	public String toString()
	{
		return firstName + " " + lastName + " [" + role.toString() + " | " + getDatabaseID() + "]";
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
			return true;

		if (o == null || !(o instanceof User))
			return false;

		final User u = (User) o;

		// Compare all details.

		//@formatter:off
		final boolean equalNames = this.getFirstName().equalsIgnoreCase(u.getFirstName()) &&
									this.getLastName().equalsIgnoreCase(u.getLastName());
		//@formatter:on

		boolean equalPINs = false;

		if (this.getPin() != null)
			equalPINs = this.getPin().equals(u.getPin());
		else
			equalPINs = u.getPin() == null;

		boolean equalBadgeIDs = false;

		if (this.getBadgeID() != null)
			equalBadgeIDs = this.getBadgeID().equals(u.getBadgeID());
		else
			equalBadgeIDs = u.getBadgeID() == null;

		return equalNames && equalPINs && equalBadgeIDs;
	}

	@Override
	public int compareTo(final AbstractDatabaseObject user)
	{
		if (user instanceof User)
			return this.getFullName().compareToIgnoreCase(((User) user).getFullName());

		return super.compareTo(user);
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
