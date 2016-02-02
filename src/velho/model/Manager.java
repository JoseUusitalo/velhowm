package velho.model;

import velho.model.interfaces.UserRole;

/**
 * This this is the manager role.
 * Note: this class has a natural ordering that is inconsistent with equals.
 *
 * @author Jose Uusitalo &amp; Edward
 */
public class Manager implements UserRole
{
	/**
	 * String is name that is in UserRole.
	 */
	private String name;

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
	public int compareTo(UserRole role)
	{
		switch (role.getName())
		{
			case "Administrator":
				return 1;
			case "Manager":
				return 0;
			case "Logistician":
				return -1;
			default:
				return 0;
		}
	}
}
