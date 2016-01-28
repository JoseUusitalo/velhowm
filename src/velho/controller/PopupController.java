package velho.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * A controller for popup alerts.
 *
 * @author Joona
 *
 */
public class PopupController
{
	/**
	 * Displays a popup alert with the given message.
	 *
	 * @param stage the message to show
	 */
	public static void info(final String string)
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("User Added");
		alert.setHeaderText(null);
		alert.setContentText(string);

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
	 * @param msg
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
