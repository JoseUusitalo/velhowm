package velho.model.interfaces;

/**
 * @author Edward Puustinen
 */
public interface UserRole extends Comparable<UserRole>
{
	/**
	 * This method share's role names.
	 *
	 * @return getName returns name
	 */
	public String getName();

	/**
	 * Compares this role with the given role and returns a integer [-1,1] depending on if the given role is above or
	 * below in this role in the company hierarchy.
	 */
	@Override
	public int compareTo(UserRole role);
}
