package velho.model;

import java.util.UUID;

/**
 * A state of a {@link RemovalList}.
 *
 * @author Jose Uusitalo
 */
public class RemovalListState extends AbstractDatabaseObject
{
	/**
	 * The name of this state.
	 */
	private String name;

	/**
	 * @param databaseID
	 * @param uuid
	 * @param name
	 */
	@SuppressWarnings("unused")
	public RemovalListState(final int databaseID, final UUID uuid, final String name)
	{
		// Database ID left unused on purpose.
		setUuid(uuid);
		this.name = name;
	}

	/**
	 * @param databaseID
	 * @param name
	 */
	public RemovalListState(final int databaseID, final String name)
	{
		this(databaseID, UUID.randomUUID(), name);
	}

	/**
	 */
	public RemovalListState()
	{
		// For Hibernate.
		setUuid(UUID.randomUUID());
	}

	/**
	 * @param state
	 */
	public RemovalListState(final RemovalListState state)
	{
		setDatabaseID(state.getDatabaseID());
		setUuid(state.getUuid());
		this.name = state.getName();
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
	 * Sets the name of this removal list state.
	 *
	 * @param name the new name of this removal list state
	 */
	public void setName(final String name)
	{
		this.name = name;
	}
}
