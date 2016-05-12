package velho.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import javafx.stage.Stage;
import velho.controller.database.DatabaseController;
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
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(DebugController.class.getName());

	/**
	 * The {@link DebugWindow}.
	 */
	private final DebugWindow view;

	/**
	 * The {@link RemovalPlatformController}.
	 */
	private final RemovalPlatformController removalPlatformController;

	/**
	 * A pseudo-random number generator.
	 */
	private final Random random;

	/**
	 * DebugController should call the RemovalPlatform
	 *
	 * @param removalPlatformController removalPlatformController so that it can be called on the DebugController
	 */
	public DebugController(final RemovalPlatformController removalPlatformController)
	{
		this.random = new Random();
		this.removalPlatformController = removalPlatformController;
		List<UserRole> roles = new ArrayList<UserRole>();
		roles.addAll(Arrays.asList(UserRole.values()));

		/*
		 * Reverse the roles so administrator is first.
		 * This makes testing faster.
		 */
		Collections.reverse(roles);

		view = new DebugWindow(this, roles, DatabaseController.getInstance().getAllBadgeIDS());
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
	 * @param role role to log in as
	 */
	public void login(final UserRole role)
	{
		if (LoginController.debugLogin(role))
			setLogInButtonVisiblity(false);
	}

	/**
	 * Forcibly logs the user in with a badge number.
	 *
	 * @param badgeString badgeString is an alternative way to log in as
	 */
	public void login(final String badgeString)
	{
		if (LoginController.login(badgeString))
			setLogInButtonVisiblity(false);
	}

	/**
	 * Logs the user out and toggles the debug window log in/out buttons.
	 */
	public void logout()
	{
		// Only log out if a user is logged in, otherwise just visually toggle the buttons.
		if (LoginController.isLoggedIn())
			LoginController.logout();
		setLogInButtonVisiblity(true);
	}

	/**
	 * Shows or hides the login/logout buttons.
	 *
	 * @param visibility show log in button?
	 */
	public void setLogInButtonVisiblity(final boolean visibility)
	{
		view.setLogInButton(visibility);
		view.setLogOutButton(!visibility);
		view.setScanBadgeButton(visibility);
	}

	/**
	 * Sends the order from DebugWindow to the external systems controller.
	 */
	@SuppressWarnings("static-method")
	public void scannerMoveValid()
	{
		ExternalSystemsController.getInstance().scannerMoveValid();
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
		removalPlatformController.modifyFreeSpace(-1.0 * (random.nextDouble() * 0.15 + 0.2 - 0.1));
	}

	/**
	 * Empties the removal platform.
	 */
	public void emptyPlatform()
	{
		removalPlatformController.emptyPlatform();
	}

	/**
	 * Sends the given badge ID to the badge scanner.
	 *
	 * @param badgeID badge ID number string
	 */
	public static void scanBadge(final String badgeID)
	{
		SYSLOG.info("Badge scanned: " + badgeID);
		ExternalSystemsController.getInstance().receiveBadgeID(badgeID);
	}
}
