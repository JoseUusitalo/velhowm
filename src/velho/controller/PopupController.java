package velho.controller;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * A controller for popup alerts.
 *
 * @author Joona Silvennoinen
 */
public class PopupController
{
	/**
	 * Displays a popup alert with the given message asking the user a yes/no
	 * question.
	 *
	 * @param msg the message to show
	 */
	public static boolean confirmation(final String msg)
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(LocalizationController.getString("popUpConfirmTitle"));
		alert.setHeaderText(null);
		alert.setContentText(msg);

		Optional<ButtonType> result = alert.showAndWait();
		return (result.get() == ButtonType.OK);
	}

	/**
	 * Displays a popup alert with the given message.
	 *
	 * @param msg the message to show
	 */
	public static void info(final String msg)
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(LocalizationController.getString("popUpInfoTitle"));
		alert.setHeaderText(null);
		alert.setContentText(msg);

		alert.showAndWait();
	}

	/**
	 * Displays a popup alert with the given message.
	 *
	 * @param msg the message to show
	 */
	public static void warning(final String msg)
	{
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(LocalizationController.getString("popUpWarningTitle"));
		alert.setHeaderText(null);
		alert.setContentText(msg);

		alert.showAndWait();
	}

	/**
	 * Displays a popup alert with the given message.
	 *
	 * @param msg the message to show
	 */
	public static void error(final String msg)
	{
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(LocalizationController.getString("popUpDatabaseInUse"));
		alert.setHeaderText(null);
		alert.setContentText(msg);

		alert.showAndWait();
	}
}
