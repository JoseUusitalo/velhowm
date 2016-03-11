package velho.model;

import velho.model.interfaces.UserRole;

/**
 * This this is the manager role.
 * Note: this class has a natural ordering that is inconsistent with equals.
 *
 * @author Jose Uusitalo &amp; Edward Puustinen
 */
public class Manager implements UserRole
{
	/**
	 * String is name that is in UserRole.
	 */
	private String name;

	/**
	 * Creates a new manager role with the name "Manager".
	 */
	public Manager()
	{
		name = "Manager";
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public int compareTo(final UserRole role)
	{
		if (role == null)
			throw new IllegalArgumentException();

		switch (role.getName())
		{
			case "Administrator":
				return -1;
			case "Manager":
				return 0;
			case "Logistician":
				return 1;
			default:
				return -1;
		}
	}
}
