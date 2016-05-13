package velho.controller;

import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;

import velho.controller.database.DatabaseController;
import velho.model.RemovalPlatform;
import velho.model.enums.UserRole;
import velho.view.MainWindow;

/**
 * The singleton controller handling {@link RemovalPlatform} objects.
 *
 * @author Jose Uusitalo
 */
public class RemovalPlatformController implements Observer
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
	 * A private inner class holding the class instance.
	 *
	 * @author Jose Uusitalo
	 */
	private static class Holder
	{
		/**
		 * The only instance of {@link RemovalPlatformController}.
		 */
		private static final RemovalPlatformController INSTANCE = new RemovalPlatformController();
	}

	/**
	 */
	private RemovalPlatformController()
	{
		getPlatform().addObserver(this);
	}

	/**
	 * Gets the instance of the {@link RemovalPlatformController}.
	 *
	 * @return the removal platform controller
	 */
	public static synchronized RemovalPlatformController getInstance()
	{
		return Holder.INSTANCE;
	}

	/**
	 * Initializes this controller
	 *
	 * @param main the {@link MainWindow}
	 */
	public void initialize(final MainWindow main)
	{
		this.mainWindow = main;
	}

	/**
	 * Gets the only removal platform currently supported by the system.
	 *
	 * @return the removal platform
	 */
	private RemovalPlatform getPlatform()
	{
		if (platform == null)
			platform = DatabaseController.getInstance().getRemovalPlatformByID(1);

		return platform;
	}

	/**
	 * <p>
	 * Changes the amount of free space on the platform by adding or removing the specified percentage points depending on its sign.
	 * </p>
	 * <p>
	 * <strong>For debug use only.</strong>
	 * </p>
	 *
	 * @param percentagePoints percentage points to modify by [0.0, 1.0]
	 */
	public void modifyFreeSpace(final double percentagePoints)
	{
		SYSLOG.debug(getPlatform() + " free space changed by " + percentagePoints);
		getPlatform().setFreeSpacePercent(getPlatform().getFreeSpacePercent() + percentagePoints);

		DatabaseController.getInstance().saveOrUpdate(getPlatform());
	}

	/**
	 * Checks if the removal platform free space is equal to or below the set warning limit and displays a warning message.
	 */
	@SuppressWarnings("static-method")
	private void checkWarning(final RemovalPlatform rplatform)
	{
		SYSLOG.trace("Checking for removal platform fullness.");

		if (Double.compare(rplatform.getFreeSpacePercent(), rplatform.getFreeSpaceLeftWarningPercent()) <= 0)
		{
			// Warning is only showed when logged in to Managers and lower.
			if (LoginController.getInstance().userRoleIsLessOrEqualTo(UserRole.MANAGER))
				PopupController.getInstance().warning(
						LocalizationController.getInstance().getCompoundString("removalPlatformFullnessPopUpNotice", (int) rplatform.getPercentFull()));
		}
	}

	@Override
	public void update(final Observable observable, final Object arg)
	{
		if (observable instanceof RemovalPlatform)
		{
			final RemovalPlatform rplatform = (RemovalPlatform) observable;
			updateMainWindow(rplatform);
			checkWarning(rplatform);
		}
	}

	/**
	 * Updates the label in the main window showing the removal platform fullness.
	 *
	 * @param rplatform platform to get data from
	 */
	private void updateMainWindow(final RemovalPlatform rplatform)
	{
		final int full = (int) rplatform.getPercentFull();
		mainWindow.setRemovalPlatformFullPercent(String.valueOf(full));
		SYSLOG.info("The removal platform is " + full + " / " + (int) (100.0 - rplatform.getFreeSpaceLeftWarningPercent() * 100.0) + "% full.");
	}

	/**
	 * Manually checks the fullness of the removal platform.
	 */
	public void update()
	{
		update(getPlatform(), null);
	}

	/**
	 * Changes the free space on the specified {@link RemovalPlatform} to the specified value.
	 *
	 * @param removalPlatformID the ID of the removal platform to be modified
	 * @param newFreeSpacePercent the new percentage of free space on the specified removal platform
	 */
	public void setFreeSpace(@SuppressWarnings("unused") final int removalPlatformID, final double newFreeSpacePercent)
	{
		// removalPlatformID is unused on purpose, at the moment the system supports a single removal platform.
		getPlatform().setFreeSpacePercent(newFreeSpacePercent);
	}
}
