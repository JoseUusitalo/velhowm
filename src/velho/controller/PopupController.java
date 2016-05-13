package velho.controller;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 * The singleton controller for popup alerts.
 *
 * @author Joona Silvennoinen
 */
@SuppressWarnings("static-method")
public class PopupController
{
	/**
	 * A private inner class holding the class instance.
	 *
	 * @author Jose Uusitalo
	 */
	private static class Holder
	{
		/**
		 * The only instance of {@link PopupController}.
		 */
		private static final PopupController INSTANCE = new PopupController();
	}

	/**
	 */
	private PopupController()
	{
		// No need to instantiate this class.
	}

	/**
	 * Gets the instance of the {@link PopupController}.
	 *
	 * @return the popup controller
	 */
	public static synchronized PopupController getInstance()
	{
		return Holder.INSTANCE;
	}

	/**
	 * Displays a popup alert with the given message asking the user a yes/no
	 * question.
	 *
	 * @param msg the message to show
	 */
	public boolean confirmation(final String msg)
	{
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(LocalizationController.getInstance().getString("popUpConfirmTitle"));
		alert.setHeaderText(null);
		alert.setContentText(msg);

		Optional<ButtonType> result = alert.showAndWait();
		return result.get() == ButtonType.OK;
	}

	/**
	 * Displays a popup alert with the given message.
	 *
	 * @param msg the message to show
	 */
	public void info(final String msg)
	{
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(LocalizationController.getInstance().getString("popUpInfoTitle"));
		alert.setHeaderText(null);
		alert.setContentText(msg);

		alert.showAndWait();
	}

	/**
	 * Displays a popup alert with the given message.
	 *
	 * @param msg the message to show
	 */
	public void warning(final String msg)
	{
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(LocalizationController.getInstance().getString("popUpWarningTitle"));
		alert.setHeaderText(null);
		alert.setContentText(msg);

		alert.showAndWait();
	}

	/**
	 * Displays a popup alert with the given message.
	 *
	 * @param msg the message to show
	 */
	public void error(final String msg)
	{
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(LocalizationController.getInstance().getString("popUpDatabaseInUse"));
		alert.setHeaderText(null);
		alert.setContentText(msg);

		alert.showAndWait();
	}
}
