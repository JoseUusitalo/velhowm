package velho.model;

/**
 * A state of a {@link Manifest}.
 *
 * @author Jose Uusitalo
 */
public class ManifestState implements Comparable<ManifestState>
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

	/**
	 */
	public ManifestState()
	{
		// For Hibernate.
	}

	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (!(o instanceof ManifestState))
			return false;

		final ManifestState ms = (ManifestState) o;

		if (this.getDatabaseID() <= 0)
			return this == ms;

		return this.getDatabaseID() == ms.getDatabaseID();
	}

	@Override
	public int compareTo(final ManifestState state)
	{
		return this.getDatabaseID() - state.getDatabaseID();
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

	/**
	 * Sets the database ID of this manifest state.
	 *
	 * @param databaseID the new database ID for this manifest state
	 */
	public void setDatabaseID(final int id)
	{
		databaseID = id;
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
	 * Sets the name of this manifest state.
	 *
	 * @param name the new name of this manifest state
	 */
	public void setName(final String name)
	{
		this.name = name;
	}
}
