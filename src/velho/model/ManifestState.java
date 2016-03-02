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

	@Override
	public int compareTo(final ManifestState state)
	{
		/*
		 * NOTE: Assumes that the manifest states are created in the priority order in the database!
		 * Priority order:
		 * 1: Stored (Boxes on shelves.)
		 * 2: Accepted (Boxes can be moved to shelves.)
		 * 3: Received (Waiting for Manager approval.)
		 * 4: Rejected (Boxes waiting to be discharged.)
		 * 5: Discharged (Boxes have been moved out of the warehouse.)
		 */
		return databaseID - state.getDatabaseID();
	}
}
