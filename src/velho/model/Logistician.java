package velho.model;

import velho.model.interfaces.UserRole;

/**
 * This is the logistician user role.
 * @author Edward
 *
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
}
