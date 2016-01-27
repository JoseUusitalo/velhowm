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
	public Manager(String name)
	{
		super();
		this.name = name;
	}
/**
 *
 */
	@Override public String getName()
	{
		return name;
	}
}
