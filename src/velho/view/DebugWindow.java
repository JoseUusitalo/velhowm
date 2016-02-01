package velho.view;

import java.util.Set;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import velho.controller.DebugController;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.control.*;

/**
 * A window with various debug features.
 *
 * @author Edward
 */
public class DebugWindow
{
	private Button getScannerData = new Button("Get Scanner Data");
	/**
	 * Button has been tagged logInButton and named as "Log In" on the scene.
	 */
	private Button logInButton = new Button("Log In");

	/**
	 * Button has been tagged logOutButton and named as "Log Out" on the scene
	 */
	private Button logOutButton = new Button("Log Out");

	/**
	 * The DebugController has been tagged as controller.
	 */
	private DebugController debugController;

	/**
	 * The grid panel.
	 */
	private GridPane grid;

	/**
	 * rolenameSet is a Set for the unique values in the code.
	 */
	private Set<String> rolenameSet;

	/**
	 * @param debugController
	 * @param rolelist
	 */
	public DebugWindow(final DebugController debugController, final Set<String> rolelist)
	{
		rolenameSet = rolelist;
		this.debugController = debugController;
		grid = null;
	}

	/**
	 * Sets the value as either true or false to show in the scene.
	 *
	 * @param visibility show log in button?
	 */
	public void setLogInButton(boolean visibility)
	{
		logInButton.setVisible(visibility);
	}

	/**
	 * sets the value as either true or false to show in the scene.
	 *
	 * @param visibility
	 * show log in button?
	 */
	public void setLogOutButton(boolean visibility)
	{
		logOutButton.setVisible(visibility);
	}

	/**
	 * Shows the debug window.
	 *
	 * @param primaryStage stage to show the window in
	 */
	public void start(Stage primaryStage)
	{
		primaryStage.setTitle("VELHO WM DEBUG");
		Scene scene = new Scene(new Group(), 300, 150);
		final ComboBox<String> taskListBox = new ComboBox<String>();

		grid = new GridPane();
		grid.setVgap(4);
		grid.setHgap(10);
		grid.setPadding(new Insets(5, 5, 5, 5));
		grid.add(new Label("User :"), 0, 0);
		taskListBox.getItems().addAll(rolenameSet);
		taskListBox.getSelectionModel().selectFirst();

		grid.add(taskListBox, 1, 0);
		logOutButton.setVisible(false);

		grid.add(getScannerData, 4, 0);

		grid.add(logInButton, 2, 0);

		grid.add(logOutButton, 3, 0);

		Group root = (Group) scene.getRoot();
		root.getChildren().add(grid);

		getScannerData.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				debugController.getScannerData();
			}

		});

		logInButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				debugController.login(taskListBox.getValue());
			}

		});

		logOutButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				debugController.logout();
			}
		});

		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
