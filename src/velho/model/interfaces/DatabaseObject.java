package velho.model.interfaces;

import java.util.UUID;

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

	/**
	 * Gets the universally unique identifier of this database object.
	 *
	 * @return the {@link UUID}
	 */
	public UUID getUuid();
}
