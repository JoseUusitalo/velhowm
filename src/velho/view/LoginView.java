package velho.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import velho.controller.LocalizationController;
import velho.controller.LoginController;
import velho.controller.UIController;
import velho.model.interfaces.GenericView;

/**
 * Allows the users to log in to the system.
 *
 * @author Jose Uusitalo &amp; Edward Puustinen
 */
public class LoginView implements GenericView
{
	/**
	 * The root VBox for this view.
	 */
	private VBox vbox;

	/**
	 * Gets the login view.
	 *
	 * @return the login view GridPane
	 */
	public VBox getView()
	{
		if (vbox == null)
		{
			vbox = new VBox(40);

			final ImageView logoView = new ImageView(new Image("/res/velhowm_logo.png"));
			logoView.setFitWidth(MainWindow.WINDOW_WIDTH / 2.0);
			logoView.setPreserveRatio(true);
			logoView.setSmooth(true);
			logoView.setCache(true);
			vbox.getChildren().add(logoView);

			final GridPane grid = new GridPane();
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

			vbox.getChildren().add(grid);
			vbox.setAlignment(Pos.CENTER);

			logInButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					LoginController.login(firstNameField.getText(), lastNameField.getText(), authenticationStringField.getText());
				}
			});
			UIController.recordView(this);
		}

		return vbox;
	}

	/**
	 * Destroys the view.
	 */
	@Override
	public void recreate()
	{
		vbox = null;
		getView();
	}

	@Override
	public void destroy()
	{
		vbox = null;
	}
}
