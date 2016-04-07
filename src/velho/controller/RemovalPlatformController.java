package velho.controller;

import org.apache.log4j.Logger;

import velho.model.RemovalPlatform;
import velho.view.MainWindow;

/**
 * Controller handling the {@link RemovalPlatform}.
 *
 * @author Jose Uusitalo
 */
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
	 * The {@link MainWindow}.
	 */
	private MainWindow mainWindow;

	/**
	 * @param mainWindow
	 */
	public RemovalPlatformController(final MainWindow mainWindow)
	{
		this.mainWindow = mainWindow;
	}

	/**
	 * Gets the only removal platform currently supported by the system.
	 *
	 * @return the removal platform
	 */
	private RemovalPlatform getPlatform()
	{
		if (platform == null)
			platform = DatabaseController.getRemovalPlatformByID(1);

		return platform;
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
	 * Changes the amount of free space on the platform by adding or removing
	 * the specified percentage points depending
	 * on its sign.
	 *
	 * @param percentagePoints percentage points to modify by [0.0, 1.0]
	 */
	public void modifyFreeSpace(final double percentagePoints)
	{
		SYSLOG.debug(getPlatform() + " free space decreased by " + percentagePoints);
		getPlatform().modifyFreeSpace(percentagePoints);

		DatabaseController.save(getPlatform());
		checkWarning();
	}

	/**
	 * Checks if the removal platform free space is equal to or below the set
	 * warning limit and displays a warning
	 * message.
	 */
	public void checkWarning()
	{
		SYSLOG.trace("Checking for removal platform fullness.");

		final int percentFull = (int) (100.0 - getFreeSpace() * 100.0);

		mainWindow.setRemovalPlatformFullPercent(String.valueOf(percentFull));

		if (Double.compare(getFreeSpace(), getPlatform().getFreeSpaceLeftWarningPercent()) <= 0)
		{
			SYSLOG.info("The removal platform is " + percentFull + " / " + (int) (100.0 - getPlatform().getFreeSpaceLeftWarningPercent() * 100.0) + "% full!");

			// Warning is only showed when logged in.
			if (LoginController.isLoggedIn())
				PopupController.warning("The removal platform is " + percentFull + "% full. Please contact the waste disposal services.");
		}
	}

	/**
	 * Empties the removal platform.
	 */
	public void emptyPlatform()
	{
		SYSLOG.info(LocalizationController.getString("removalPlatformEmptiedNotice"));
		getPlatform().empty();
		checkWarning();
	}
}
