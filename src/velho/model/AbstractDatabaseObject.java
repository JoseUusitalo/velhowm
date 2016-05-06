package velho.model;

import java.util.UUID;

import velho.model.interfaces.DatabaseObject;

/**
 * An abstract {@link DatabaseObject} that handles manipulating the database ID and {@link UUID}.
 * Helps in reducing duplicate code.
 *
 * @author Jose Uusitalo
 */
public abstract class AbstractDatabaseObject implements DatabaseObject, Comparable<AbstractDatabaseObject>
{
	/**
	 * The database ID of this {@link DatabaseObject}.
	 */
	private int databaseID;

	/**
	 * The {@link UUID} of this {@link DatabaseObject}.
	 */
	private UUID uuid;

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
			return true;

		if (o == null || !(o instanceof AbstractDatabaseObject))
			return false;

		return this.getUuid().equals(((AbstractDatabaseObject) o).getUuid());
	}

	@Override
	public int hashCode()
	{
		// 3 is a prime and apparently using primes is good.
		return 3 * getUuid().hashCode();
	}

	@Override
	public int compareTo(final AbstractDatabaseObject object)
	{
		return this.getDatabaseID() - object.getDatabaseID();
	}

	@Override
	public String toString()
	{
		return this.getClass().getName() + " [" + getDatabaseID() + "]";
	}

	@Override
	final public int getDatabaseID()
	{
		return databaseID;
	}

	/**
	 * For Hibernate only.
	 * Do not use, does not work as intended and probably breaks things.
	 *
	 * @param databaseID database ID
	 */
	final public void setDatabaseID(final int databaseID)
	{
		this.databaseID = databaseID;
	}

	@Override
	public UUID getUuid()
	{
		return uuid;
	}

	/**
	 * Assign a new universally unique identifier for this {@link DatabaseObject}.
	 *
	 * @param uuid the new {@link UUID}
	 */
	public void setUuid(final UUID uuid)
	{
		this.uuid = uuid;
	}
}
