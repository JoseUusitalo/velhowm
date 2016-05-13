package velho.model;

import java.util.UUID;

/**
 * A basic removal platform implementation.
 *
 * @author Jose Uusitalo
 */
public class RemovalPlatform extends ObservableDatabaseObject
{
	/**
	 * The amount of free space left on this removal platform as a percentage.
	 */
	private double freeSpacePercent;

	/**
	 * Should the free space on this platform fall below or to this limit a warning will the displayed in the UI.
	 */
	private double freeSpaceLeftWarningPercent;

	/**
	 * @param databaseID
	 * @param uuid
	 * @param freeSpacePercent
	 * @param freeSpaceLeftWarningPercent
	 */
	@SuppressWarnings("unused")
	public RemovalPlatform(final int databaseID, final UUID uuid, final double freeSpacePercent, final double freeSpaceLeftWarningPercent)
	{
		// Database ID left unused on purpose.
		setUuid(uuid);
		this.freeSpacePercent = freeSpacePercent;
		this.freeSpaceLeftWarningPercent = freeSpaceLeftWarningPercent;
	}

	/**
	 * @param databaseID
	 * @param freeSpacePercent
	 * @param freeSpaceLeftWarningPercent
	 */
	public RemovalPlatform(final int databaseID, final double freeSpacePercent, final double freeSpaceLeftWarningPercent)
	{
		this(databaseID, UUID.randomUUID(), freeSpacePercent, freeSpaceLeftWarningPercent);
	}

	/**
	 */
	public RemovalPlatform()
	{
		// For Hibernate.
		setUuid(UUID.randomUUID());
	}

	@Override
	public String toString()
	{
		return "[" + getDatabaseID() + "] Removal Platform: " + freeSpacePercent * 100.0 + "% (" + freeSpaceLeftWarningPercent * 100.0 + "%)";
	}

	/**
	 * Gets the amount of free space left on this removal platform.
	 *
	 * @return free space on this platform as a percentage
	 */
	public double getFreeSpacePercent()
	{
		return freeSpacePercent;
	}

	/**
	 * Sets the amount of free space on this removal platform.
	 *
	 * @param freeSpacePercent the free space percentage
	 */
	public void setFreeSpacePercent(final double freeSpacePercent)
	{
		this.freeSpacePercent = freeSpacePercent;
		setChanged();
		notifyObservers();
	}

	/**
	 * Gets the warning percent of the platform.
	 * Should the free space on the platform drop to below or equal to the percentage, a warning will be displayed in
	 * the UI.
	 *
	 * @return the warning percent of the platform.
	 */
	public double getFreeSpaceLeftWarningPercent()
	{
		return freeSpaceLeftWarningPercent;
	}

	/**
	 * Sets the warning percent of this platform.
	 * Should the free space on the platform drop to below or equal to the percentage, a warning will be displayed in
	 * the UI.
	 *
	 * @param freeSpaceLeftWarningPercent a percentage
	 */
	public void setFreeSpaceLeftWarningPercent(final double freeSpaceLeftWarningPercent)
	{
		this.freeSpaceLeftWarningPercent = freeSpaceLeftWarningPercent;
	}

	/**
	 * The inverse of {@link #getFreeSpacePercent()} as a percentage [0.0, 100.0].
	 *
	 * @return a percent value of how full this platform is
	 */
	public double getPercentFull()
	{
		return 100.0 - getFreeSpacePercent() * 100.0;
	}
}
