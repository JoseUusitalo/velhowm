package velho.model;

import velho.model.interfaces.DatabaseObject;

/**
 * An abstract {@link DatabaseObject} that handles manipulating the database ID.
 * Helps in reducing duplicate code.
 *
 * @author Jose Uusitalo
 */
public abstract class AbstractDatabaseObject implements DatabaseObject
{
	/**
	 * The database ID of this {@link DatabaseObject}.
	 */
	private int databaseID;

	@Override
	final public int getDatabaseID()
	{
		return databaseID;
	}

	/**
	 * Assign a new database ID for this {@link DatabaseObject}.
	 *
	 * @param databaseID the new database ID
	 */
	final public void setDatabaseID(final int databaseID)
	{
		this.databaseID = databaseID;
	}
}
