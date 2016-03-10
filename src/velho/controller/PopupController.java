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
	 * Displays a popup alert with the given message asking the user a yes/no question.
	 *
	 * @param stage the message to show
	 */
	public static boolean confirmation(final String msg)
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirm");
		alert.setHeaderText(null);
		alert.setContentText(msg);

		Optional<ButtonType> result = alert.showAndWait();
		return (result.get() == ButtonType.OK);
	}

	/**
	 * Displays a popup alert with the given message.
	 *
	 * @param stage the message to show
	 */
	public static void info(final String msg)
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Info");
		alert.setHeaderText(null);
		alert.setContentText(msg);

		alert.showAndWait();
	}

	/**
	 * Displays a popup alert with the given message.
	 *
	 * @param msg the message to show
	 */
	public static void warning(String msg)
	{
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Warning");
		alert.setHeaderText(null);
		alert.setContentText(msg);

		alert.showAndWait();
	}

	/**
	 * Displays a popup alert with the given message.
	 *
	 * @param msg the message to show
	 */
	public static void error(String msg)
	{
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(null);
		alert.setContentText(msg);

		alert.showAndWait();
	}
}
