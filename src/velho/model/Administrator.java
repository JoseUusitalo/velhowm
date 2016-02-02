package velho.model;

import velho.model.interfaces.UserRole;

/**
 * This is the administrator user role.
 * Note: this class has a natural ordering that is inconsistent with equals.
 *
 * @author Jose Uusitalo &amp; Edward
 */
public class Administrator implements UserRole
{
	/**
	 * String is name that is in UserRole.
	 */
	private String name;

	public Administrator()
	{
		name = "Administrator";
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
				return 0;
			case "Manager":
			case "Logistician":
				return -1;
			default:
				return -0;
		}
	}
}
