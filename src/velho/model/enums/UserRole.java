package velho.model.enums;

public enum UserRole
{
	/**
	 * A non-logged in user.
	 * Currently guests have no implemented features.
	 * Mainly exists to pad the ordinal numbers because H2 database row IDs start at 1.
	 */
	GUEST,

	/**
	 * A standard worker.
	 */
	LOGISTICIAN,

	/**
	 * Managerial staff.
	 */
	MANAGER,

	/**
	 * Software developers.
	 */
	ADMINISTRATOR;

	/**
	 * Equivalent to {@link UserRole#getName()}.
	 */
	@Override
	public String toString()
	{
		return getName();
	}

	/**
	 * The name of this role with the first letter capitalized.
	 *
	 * @return the name of this role
	 */
	public String getName()
	{
		return this.name().substring(0, 1) + this.name().substring(1).toLowerCase();
	}
}
