package velho.view;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import velho.controller.LoginController;

/**
 * Views the log ins to the applications.
 * @author Edward
 *
 */
public class LoginView extends Application
{

	public Button logInButton = new Button("Log In");
	public Button logOutButton = new Button("Log Out");
	private LoginController controller;

	/**
	 * Changes the visibility of the button "Log In".
	 *
	 * @param visibility show log in button?
	 */
	public void setLogInButton(boolean visibility)
	{
		logInButton.setVisible(visibility);
	}

	/**
	 * Changes the visibility of the button "Log Out".
	 *
	 * @param visibility show log in button?
	 */
	public void setLogOutButton(boolean visibility)
	{
		logOutButton.setVisible(visibility);
	}

	@Override public void start(Stage stage)
	{
		stage.setTitle("VELHO WM DEBUG");
		Scene scene = new Scene(new Group(), 300, 150);

		controller = new LoginController(this);

		Label labels = new Label("Badge ID or Password:");
		TextField textField = new TextField();
		VBox vb = new VBox();
		vb.getChildren().addAll(labels, textField);
		vb.setSpacing(10);

		GridPane grid = new GridPane();
		grid.setVgap(4);
		grid.setHgap(0);
		grid.setPadding(new Insets(5, 5, 5, 5));
		//		grid.add(new Label("User :"), 0, 0);
		//		grid.add(taskComboBox, 1, 0);
		grid.add(labels, 0, 1, 1, 1);
		grid.add(textField, 0, 2, 2, 1);

		logOutButton.setVisible(false);

		grid.add(logInButton, 0, 3);

		grid.add(logOutButton, 3, 0);

		Group root = (Group) scene.getRoot();
		root.getChildren().add(grid);

		logInButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override public void handle(ActionEvent event)
			{
				controller.login(textField.getText());
			}

		});

		logOutButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override public void handle(ActionEvent event)
			{
				controller.logout();
			}
		});

		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args)
	{
		launch(args);
	}
}
