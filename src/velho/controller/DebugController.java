package velho.controller;

import java.util.Random;

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
	 * The {@link DebugWindow}.
	 */
	private DebugWindow view;

	/**
	 * The {@link RemovalPlatformController}.
	 */
	private RemovalPlatformController removalPlatformController;

	/**
	 * A pseudo-random number generator.
	 */
	private Random r = new Random();

	/**
	 * @param removalPlatformController
	 * @throws NoDatabaseLinkException
	 */
	public DebugController(final RemovalPlatformController removalPlatformController) throws NoDatabaseLinkException
	{
		this.removalPlatformController = removalPlatformController;
		view = new DebugWindow(this, DatabaseController.getUserRoleNames());
	}

	/**
	 * Creates and shows the debug window.
	 *
	 * @param stage the stage to run the window in
	 */
	public void createDebugWindow(final Stage stage)
	{
		view.start(stage);
	}

	/**
	 * Forcibly logs the user in with the specified role.
	 *
	 * @param userRoleName role to log in as
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
	 * Logs the user out and toggles the debug window log in/out buttons.
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
	 * Shows or hides the login button.
	 *
	 * @param visibility show log in button?
	 */
	public void setLogInButton(final boolean visibility)
	{
		view.setLogInButton(visibility);
	}

	/**
	 * Shows or hides the logout button.
	 *
	 * @param visibility show log out button?
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
	 * moveResult what currently sends the message and informs the DebugWindow of current events.
	 *
	 * @param productCode is the code the the product identifies with
	 * @param shelfSlotCode is the code for the Shelf slot where the product resides in
	 * @param success is actually a true that prints a message to the DebugWindow
	 */
	public static void moveResult(final int productCode, final String shelfSlotCode, final boolean success)
	{
		// TODO: Figure out what this does.

		if (success == true)
		{
			PopupController.info(productCode + " was moved to " + shelfSlotCode + ": " + success);
			return;
		}

		PopupController.info(productCode + " was not moved to " + shelfSlotCode
				+ " error: If the product was not moved it either does not exist or the shelf does not exist!");
	}

	/**
	 * Sends a randomized shipment to the warehouse.
	 */
	public static void sendRandomShipment()
	{
		BarcodeScanner.sendRandomShipment();
	}

	/**
	 * Fills up the removal platform controller randomly by [0.1, 0.25] percentage points.
	 */
	public void fillUpPlatform()
	{
		removalPlatformController.modifyFreeSpace(-1.0 * ((r.nextDouble() * 0.15 + 0.2) - 0.1));
	}

	/**
	 * Empties the removal platform.
	 */
	public void emptyPlatform()
	{
		removalPlatformController.emptyPlatform();
	}
}
