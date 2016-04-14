package velho.model;

/**
 * A state of a {@link RemovalList}.
 *
 * @author Jose Uusitalo
 */
public class RemovalListState extends AbstractDatabaseObject implements Comparable<RemovalListState>
{
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
		setDatabaseID(databaseID);
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

	@Override
	public boolean equals(final Object o)
	{
		if (!(o instanceof RemovalListState))
			return false;

		final RemovalListState rls = (RemovalListState) o;

		if (this.getDatabaseID() <= 0)
			return this == rls;

		return this.getDatabaseID() == rls.getDatabaseID();
	}

	@Override
	public int compareTo(final RemovalListState state)
	{
		return this.getDatabaseID() - state.getDatabaseID();
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
