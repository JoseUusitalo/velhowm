package velho.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import velho.controller.LocalizationController;
import velho.controller.LoginController;

/**
 * Allows the users to log in to the system.
 *
 * @author Jose Uusitalo &amp; Edward Puustinen
 */
public class LoginView
{
	/**
	 * The root GridPane for this view.
	 */
	private GridPane grid;

	/**
	 * Gets the login view.
	 *
	 * @return the login view GridPane
	 */
	public GridPane getView()
	{
		if (grid == null)
		{
			grid = new GridPane();
			grid.setVgap(10);
			grid.setHgap(10);
			grid.setAlignment(Pos.CENTER);

			final Label infoText = new Label(LocalizationController.getString("infoTextScanBadgeOrLogInWithName"));
			infoText.setMaxWidth(Double.MAX_VALUE);
			infoText.setAlignment(Pos.CENTER);
			grid.add(infoText, 0, 0, 2, 1);

			final TextField firstNameField = new TextField();
			firstNameField.setPromptText(LocalizationController.getString("promptTextFirstName"));
			grid.add(firstNameField, 0, 1, 1, 1);

			final TextField lastNameField = new TextField();
			lastNameField.setPromptText(LocalizationController.getString("promptTextLastName"));
			grid.add(lastNameField, 1, 1, 1, 1);

			final PasswordField authenticationStringField = new PasswordField();
			authenticationStringField.setPromptText(LocalizationController.getString("passWordPromptText"));
			grid.add(authenticationStringField, 0, 2, 2, 1);

			Button logInButton = new Button(LocalizationController.getString("logInButton"));
			logInButton.setMaxWidth(Double.MAX_VALUE);
			logInButton.setPrefHeight(50.0);
			grid.add(logInButton, 0, 3, 2, 1);

			logInButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					LoginController.login(firstNameField.getText(), lastNameField.getText(), authenticationStringField.getText());
				}
			});
		}

		return grid;
	}

	/**
	 * Destroys the view.
	 */
	public void destroy()
	{
		grid = null;
	}
}
