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
	public Logistician(String name)
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
