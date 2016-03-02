package velho.controller;

import org.apache.log4j.Logger;

import javafx.stage.Stage;
import velho.model.BarcodeScanner;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.view.DebugWindow;

/**
 * Various debugging features.
 *
 * @author Edward &amp; Jose Uusitalo
 */
public class DebugController
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(DebugController.class.getName());

	/**
	 * The {@link DebugWindow}.
	 */
	private DebugWindow view;

	/**
	 * @param loginController
	 * @param debugView
	 * @throws NoDatabaseLinkException
	 */
	public DebugController() throws NoDatabaseLinkException
	{
		view = new DebugWindow(this, DatabaseController.getUserRoleNames());
	}

	/**
	 * Creates and shows the debug window.
	 *
	 * @param stage
	 * the stage to run the window in
	 */
	public void createDebugWindow(final Stage stage)
	{
		view.start(stage);
	}

	/**
	 * Here the login sets value to the buttons.
	 *
	 * @param userRoleName
	 */
	public void login(final String userRoleName)
	{
		try
		{
			LoginController.debugLogin(userRoleName);
			view.setLogInButton(false);
			view.setLogOutButton(true);
		}
		catch (final NoDatabaseLinkException e)
		{
			PopupController.error("Login failed, no database connection.");
			DatabaseController.tryReLink();
		}
	}

	/**
	 * Sets the logout sets value to the buttons.
	 */
	public void logout()
	{
		// Only log out if a user is logged in, otherwise just visually toggle the buttons.
		if (LoginController.isLoggedIn())
			LoginController.logout();
		view.setLogInButton(true);
		view.setLogOutButton(false);
	}

	/**
	 * The parameter for LogInButton is either false or true.
	 *
	 * @param visibility
	 * show log in button?
	 */
	public void setLogInButton(final boolean visibility)
	{
		view.setLogInButton(visibility);
	}

	/**
	 * The parameter for LogInButton is either false or true.
	 *
	 * @param visibility
	 * show log in button?
	 */
	public void setLogOutButton(final boolean visibility)
	{
		view.setLogOutButton(visibility);
	}

	/**
	 * Sends the order from DebugWindow to the external systems controller.
	 */
	@SuppressWarnings("static-method")
	public void scannerMoveValid()
	{
		ExternalSystemsController.scannerMoveValid();
	}

	/**
	 * Result message from the External systems controller to the DebugWindow of the procedures end result.
	 * Still in progress since there are not items in the database.
	 */
	public static void resultMessage()
	{
		final boolean s = ExternalSystemsController.move(0, null, true);
		if (s == true)
		{
			SYSLOG.debug("Product box move successful.");
		}
		else
		{
			SYSLOG.debug("Product box move failed.");
		}
	}

	/**
	 * moveResult what currently sends the message and informs the DebugWindow of current events.
	 *
	 * @param productCode
	 * is the code the the product identifies with.
	 * @param shelfSlotCode
	 * is the code for the Shelf slot where the product resides in.
	 * @param success
	 * is actually a true that prints a message to the DebugWindow.
	 */
	public static void moveResult(final int productCode, final String shelfSlotCode, final boolean success)
	{
		if (success == true)
		{
			PopupController.info(productCode + " was moved to " + shelfSlotCode + ": " + success);
			return;
		}

		PopupController.info(productCode + " was not moved to " + shelfSlotCode
				+ " error: If the product was not moved it either does not exist or the shelf does not exist!");
	}

	public static void sendRandomShipment()
	{
		BarcodeScanner.sendRandomShipment();
	}
}
