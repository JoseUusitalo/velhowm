package velho.model;

/**
 * A state of a {@link RemovalList}.
 *
 * @author Jose Uusitalo
 */
public class RemovalListState
{
	/**
	 * The database ID.
	 */
	private int databaseID;

	/**
	 * The name of this state.
	 */
	private String name;

	/**
	 * @param databaseID
	 * @param name
	 */
	public RemovalListState(final int databaseID, final String name)
	{
		this.databaseID = databaseID;
		this.name = name;
	}

	/**
	 */
	public RemovalListState()
	{
		// For Hibernate.
	}

	@Override
	public String toString()
	{
		return name;
	}

	/**
	 * Gets the database ID of this removal list state.
	 *
	 * @return the database ID of this removal list state
	 */
	public int getDatabaseID()
	{
		return databaseID;
	}

	/**
	 * Sets the database ID of this removal list state.
	 *
	 * @param databaseID the new database ID for this removal list state
	 */
	public void setDatabaseID(final int id)
	{
		databaseID = id;
	}

	/**
	 * Gets the name of this removal list state.
	 *
	 * @return the name of this removal list state
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of this removal list state.
	 *
	 * @param name the new name of this removal list state
	 */
	public void setName(final String name)
	{
		this.name = name;
	}
}
