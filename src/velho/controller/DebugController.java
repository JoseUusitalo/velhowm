package velho.controller;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import javafx.stage.Stage;
import velho.model.BarcodeScanner;
import velho.model.enums.UserRole;
import velho.view.DebugWindow;

/**
 * Various debugging features.
 *
 * @author Edward Puustinen &amp; Jose Uusitalo
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
	public DebugController(final RemovalPlatformController removalPlatformController)
	{
		this.removalPlatformController = removalPlatformController;
		Set<UserRole> roles = new LinkedHashSet<UserRole>();
		roles.addAll(Arrays.asList(UserRole.values()));
		view = new DebugWindow(this, roles);
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
	public void login(final UserRole role)
	{
		LoginController.debugLogin(role);
		view.setLogInButton(false);
		view.setLogOutButton(true);
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
