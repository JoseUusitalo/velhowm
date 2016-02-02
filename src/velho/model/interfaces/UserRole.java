package velho.model.interfaces;

/**
 * @author Edward
 */
public interface UserRole extends Comparable<UserRole>
{
	/**
	 * This method share's role names.
	 *
	 * @return getName returns name
	 */
	public String getName();

	@Override
	public int compareTo(UserRole role);
}
