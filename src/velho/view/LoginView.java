package velho.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import velho.controller.DebugController;
import velho.controller.LoginController;

/**
 * Views the log ins to the applications.
 * @author Edward
 *
 */
public class LoginView
{
	private LoginController controller;
	private GridPane grid;

	public LoginView(LoginController loginController)
	{
		controller = loginController;
	}

	public GridPane getLoginView()
	{
		if (grid == null)
		{
			grid = new GridPane();
			grid.setVgap(4);
			grid.setHgap(0);
			grid.setAlignment(Pos.CENTER);
			Label labels = new Label("Scan badge or write password");

			grid.add(labels, 0, 1, 1, 1);

			final TextField textField = new TextField();
			grid.add(textField, 0, 2, 2, 1);

			Button logInButton = new Button("Log In");
			grid.add(logInButton, 0, 3, 2, 1);

			logInButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent event)
				{
					controller.login(textField.getText());
				}
			});
		}

		return grid;
	}
}
