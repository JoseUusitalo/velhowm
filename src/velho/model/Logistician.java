package velho.model;

import velho.model.interfaces.UserRole;

/**
 * This is the logistician user role.
 * Note: this class has a natural ordering that is inconsistent with equals.
 *
 * @author Jose Uusitalo &amp; Edward
 */
public class Logistician implements UserRole
{
	/**
	 * String is name that is in UserRole.
	 */
	private String name;

	public Logistician()
	{
		name = "Logistician";
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
			case "Manager":
				return -1;
			case "Logistician":
				return 0;
			default:
				return -1;
		}
	}
}
