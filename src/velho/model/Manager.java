package velho.model;

import velho.model.interfaces.UserRole;

/**
 * This this is the manager role.
 * @author Edward
 *
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
}
