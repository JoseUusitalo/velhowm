package velho.view;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import velho.controller.DebugController;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.*;

public class DebugWindow extends Application
{

	public Button logInButton = new Button("Log In");
	public Button logOutButton = new Button("Log Out");
	private DebugController controller;

	public void setLogInButton(boolean visibility)
	{
		logInButton.setVisible(visibility);
	}

	public void setLogOutButton(boolean visibility)
	{
		logOutButton.setVisible(visibility);
	}

	@Override public void start(Stage stage)
	{
		stage.setTitle("VELHO WM DEBUG");
		Scene scene = new Scene(new Group(), 300, 150);

		controller = new DebugController(this);

		final ComboBox taskComboBox = new ComboBox();

		taskComboBox.setValue("");

		GridPane grid = new GridPane();
		grid.setVgap(4);
		grid.setHgap(10);
		grid.setPadding(new Insets(5, 5, 5, 5));
		grid.add(new Label("User :"), 0, 0);
		grid.add(taskComboBox, 1, 0);

		logOutButton.setVisible(false);

		grid.add(logInButton, 2, 0);

		grid.add(logOutButton, 3, 0);

		Group root = (Group) scene.getRoot();
		root.getChildren().add(grid);

		logInButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override public void handle(ActionEvent event)
			{
				controller.login();
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
