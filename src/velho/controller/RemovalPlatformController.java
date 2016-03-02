package velho.controller;

import org.apache.log4j.Logger;

import velho.model.RemovalPlatform;
import velho.model.exceptions.NoDatabaseLinkException;

public class RemovalPlatformController
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(RemovalPlatformController.class.getName());

	/**
	 * The system only supports one removal platform for now.
	 */
	private RemovalPlatform platform;

	/**
	 * Gets the only removal platform currently supported by the system.
	 *
	 * @return the removal platform
	 */
	private RemovalPlatform getPlatform()
	{
		if (platform == null)
		{
			try
			{
				this.platform = DatabaseController.getRemovalPlatformByID(0);
			}
			catch (NoDatabaseLinkException e)
			{
				DatabaseController.tryReLink();
			}
		}

		return this.platform;
	}

	/**
	 * Gets the amount of free space left on the removal platform.
	 *
	 * @return the percent of free space left
	 */
	public double getFreeSpace()
	{
		return getPlatform().getFreeSpacePercent();
	}

	/**
	 * Changes the amount of free space on the platform by adding or removing the specified percentage points depending
	 * on its sign.
	 *
	 * @param modifyBy
	 */
	public void modifyFreeSpace(final double percentagePoints)
	{
		getPlatform().modifyFreeSpace(percentagePoints);
		checkWarning();
	}

	/**
	 * Checks if the removal platform free space is equal to or below the set warning limit.
	 */
	private void checkWarning()
	{
		SYSLOG.trace("Checking for removal platform fullness.");

		if (Double.compare(getFreeSpace(), getPlatform().getFreeSpaceLeftWarningPercent()) <= 0)
		{
			SYSLOG.info("The removal platform is at or below the warning limit of " + getFreeSpace() + "% full!");
			PopupController.warning("The removal platform is " + getFreeSpace() + "% full. Please contact the waste disposal services.");
		}
	}
}
