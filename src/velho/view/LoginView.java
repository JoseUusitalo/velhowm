package velho.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import velho.controller.LoginController;

/**
 * Views the log ins to the applications.
 * @author Edward
 *
 */
public class LoginView
{
	private GridPane grid;

	public GridPane getLoginView()
	{
		if (grid == null)
		{
			grid = new GridPane();
			grid.setVgap(4);
			grid.setHgap(0);
			grid.setAlignment(Pos.CENTER);
			Label labels = new Label("Scan Badge or Write PIN");

			grid.add(labels, 0, 1, 1, 1);

			final PasswordField textField = new PasswordField();
			grid.add(textField, 0, 2, 2, 1);

			Button logInButton = new Button("Log In");
			grid.add(logInButton, 0, 3, 2, 1);

			logInButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent event)
				{
					LoginController.login(textField.getText());
				}
			});
		}

		return grid;
	}
}
