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

	@Override
	public String toString()
	{
		return name;
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
	 * Gets the database ID of this removal list state.
	 *
	 * @return the database ID of this removal list state
	 */
	public int getDatabaseID()
	{
		return databaseID;
	}
}
