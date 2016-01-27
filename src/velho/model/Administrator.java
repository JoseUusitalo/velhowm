package velho.model;

import velho.model.interfaces.UserRole;
/**
 * This is the administrator user role.
 * @author Edward
 *
 */
public class Administrator implements UserRole
{
	/**
	 * String is name that is in UserRole.
	 */
	private String name;
	public Administrator(String name)
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