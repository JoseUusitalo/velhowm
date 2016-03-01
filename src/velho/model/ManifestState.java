package velho.model;

/**
 * A state of a {@link Manifest}.
 *
 * @author Jose Uusitalo
 */
public class ManifestState
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
	public ManifestState(final int databaseID, final String name)
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
	 * Gets the name of this manifest state.
	 *
	 * @return the name of this manifest state
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the database ID of this manifest state.
	 *
	 * @return the database ID of this manifest state
	 */
	public int getDatabaseID()
	{
		return databaseID;
	}
}
