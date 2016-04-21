package velho.model;

import java.util.UUID;

/**
 * A state of a {@link Manifest}.
 *
 * @author Jose Uusitalo
 */
public class ManifestState extends AbstractDatabaseObject
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
	public ManifestState(final int databaseID, final UUID uuid, final String name)
	{
		setDatabaseID(databaseID);
		setUuid(uuid);
		this.name = name;
	}

	/**
	 * @param databaseID
	 * @param name
	 */
	public ManifestState(final int databaseID, final String name)
	{
		this(databaseID, UUID.randomUUID(), name);
	}

	/**
	 */
	public ManifestState()
	{
		// For Hibernate.
		setUuid(UUID.randomUUID());
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
	 * Sets the name of this manifest state.
	 *
	 * @param name the new name of this manifest state
	 */
	public void setName(final String name)
	{
		this.name = name;
	}
}
