package velho.view;

import java.util.Set;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import velho.controller.DebugController;
import velho.controller.UserController;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.control.*;

/**
 * Class made to debug the log ins and outs.
 * @author Edward
 *
 */
public class DebugWindow
{
	/**
	 * Button has been tagged logInButton and named as "Log In" on the scene.
	 */
	public Button logInButton = new Button("Log In");
	/**
	 * Button has been tagged logOutButton and named as "Log Out" on the scene
	 */
	public Button logOutButton = new Button("Log Out");
	/**
	 * The DebugController has been tagged as controller.
	 */
	private DebugController degController;
	/**
	 * The grid panel.
	 */
	private GridPane grid;
	/**
	 * rolenameSet is a Set for the unique values in the code.
	 */
	private Set<String> rolenameSet;

	/**
	 *
	 * @param debugController
	 * @param rolelist
	 */
	public DebugWindow(DebugController debugController, Set<String> rolelist)
	{
		rolenameSet = rolelist;
		degController = debugController;
		grid = null;
	}

	/**
	 * Sets the value as either true or false to show in the scene.
	 *
	 * @param visibility
	 *            show log in button?
	 */
	public void setLogInButton(boolean visibility)
	{
		logInButton.setVisible(visibility);
	}

	/**
	 * sets the value as either true or false to show in the scene.
	 *
	 * @param visibility
	 *            show log in button?
	 */
	public void setLogOutButton(boolean visibility)
	{
		logOutButton.setVisible(visibility);
	}

	public void start(Stage primaryStage)
	{
		primaryStage.setTitle("VELHO WM DEBUG");
		Scene scene = new Scene(new Group(), 300, 150);


		final ComboBox<String> taskListBox = new ComboBox<String>();

		taskListBox.setValue("");

		grid = new GridPane();
		grid.setVgap(4);
		grid.setHgap(10);
		grid.setPadding(new Insets(5, 5, 5, 5));
		grid.add(new Label("User :"), 0, 0);
		taskListBox.getItems().addAll(rolenameSet);
		taskListBox.getSelectionModel().selectFirst();

		grid.add(taskListBox, 1, 0);
		logOutButton.setVisible(false);

		grid.add(logInButton, 2, 0);

		grid.add(logOutButton, 3, 0);

		Group root = (Group) scene.getRoot();
		root.getChildren().add(grid);

		logInButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override public void handle(ActionEvent event)
			{
				taskListBox.getValue();
				degController.login(taskListBox.getValue());
			}

		});

		logOutButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override public void handle(ActionEvent event)
			{
				degController.logout();
			}
		});

		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
