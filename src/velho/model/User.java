package velho.model;

import velho.controller.DatabaseController;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.model.interfaces.UserRole;

/**
 * A User represents the person using this system.
 *
 * @author Jose Uusitalo
 */
public class User
{
	/**
	 * The maximum number of characters a first or last name may have.
	 */
	private static final int MAX_NAME_LENGTH = 128;

	/**
	 * The maximum number of digits a PIN code can have.
	 */
	private static final int MAX_PIN_LENGTH = 6;

	/**
	 * The maximum value for a badge ID code.
	 */
	private static final int MAX_BADGE_ID_VALUE = 99999999;

	/**
	 * The unique identifier of a RFID badge.
	 */
	private int badgeID;

	/**
	 * The first name of this user.
	 */
	private String firstName;

	/**
	 * The last name of this user.
	 */
	private String lastName;

	/**
	 * The role of this user.
	 */
	private UserRole role;

	/**
	 * @param badgeID
	 * @param firstName
	 * @param lastName
	 * @param role
	 */
	public User(final int badgeID, final String firstName, final String lastName, final UserRole role)
	{
		this.badgeID = badgeID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = role;
	}

	/**
	 * Validates the user data against the database requirements.
	 * Either a badge ID or a PIN must be defined.
	 * Both cannot be null.
	 * Both cannot be defined.
	 *
	 * @param badgeID RFID identification number of the user's RFID badge
	 * @param pin the pin number used to log in to the system if no RFID badge ID is provided
	 * @param firstName the first name of the user
	 * @param lastName the last name of the user
	 * @param role the name of the role of the user
	 * @return <code>true</code> if given information is valid
	 */
	@SuppressWarnings("null")
	public static boolean validateUserData(final String badgeID, final String pin, final String firstName, final String lastName, final String roleName)
			throws NoDatabaseLinkException
	{
		boolean hasBadgeID = (badgeID != null);

		// Has a badge ID, is it not empty?
		if (hasBadgeID)
			hasBadgeID = !badgeID.isEmpty();

		boolean hasPIN = (pin != null);

		// Has a PIN, is it not empty?
		if (hasPIN)
			hasPIN = !pin.isEmpty();

		if (hasBadgeID)
		{
			if (hasPIN)
				return false;
			else if (Integer.parseInt(badgeID) > MAX_BADGE_ID_VALUE) // Badge ID must be valid.
				return false;
		}
		else
		{
			// hasBadgeID == false
			if (!hasPIN)
				return false;
			else if (Integer.parseInt(pin) > (Math.pow(10.0, MAX_PIN_LENGTH))) // PIN must be valid.
				return false;
		}

		if (firstName.length() > MAX_NAME_LENGTH)
			return false;

		if (lastName.length() > MAX_NAME_LENGTH)
			return false;

		if (DatabaseController.getRoleID(roleName) == -1)
			return false;

		return true;
	}

	/**
	 * Returns the user data in the following format:
	 * <code>firstname lastname (rolename)</code>
	 */
	@Override
	public String toString()
	{
		return firstName + " " + lastName + " (" + role.toString() + ")";
	}

	/**
	 * Gets the unique RFID badge number that this user has.
	 *
	 * @return the badge number of this user
	 */
	public int getBadgeID()
	{
		return badgeID;
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
	 * Gets the last name of this user.
	 *
	 * @return the last name of this user
	 */
	public String getLastName()
	{
		return lastName;
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

	public static boolean isValidPIN(final int pin)
	{
		return (pin <= (Math.pow(10.0, MAX_PIN_LENGTH)));
	}
}
