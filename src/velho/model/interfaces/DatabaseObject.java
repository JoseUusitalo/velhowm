package velho.model.interfaces;

/**
 * An interface implemented by all classes that are stored in the database.
 *
 * @author Jose Uusitalo
 */
public interface DatabaseObject
{
	/**
	 * Gets the database ID of this database object.
	 *
	 * @return the database ID
	 */
	public int getDatabaseID();
}
