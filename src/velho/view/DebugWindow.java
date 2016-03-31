package velho.view;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import velho.controller.DebugController;
import velho.model.enums.UserRole;

/**
 * A window with various debug features.
 *
 * @author Edward Puustinen
 */
public class DebugWindow
{
	/**
	 * Button has been tagged logInButton and named as "Log In" on the scene.
	 */
	private Button logInButton = new Button("Log In");

	/**
	 * Button has been tagged logOutButton and named as "Log Out" on the scene.
	 */
	public Button logOutButton = new Button("Log Out");

	/**
	 * Button has been tagged scannerMoveValid and named as "Scanner Move Valid"
	 * on the scene.
	 */
	public Button scannerMoveValid = new Button("Scanner Move Valid");

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
	private List<UserRole> roles;

	/**
	 * @param debugController
	 * @param rolelist
	 */
	public DebugWindow(final DebugController debugController, final List<UserRole> userRoles)
	{
		roles = userRoles;
		this.debugController = debugController;
		grid = null;
	}

	/**
	 * Sets the value as either true or false to show in the scene.
	 *
	 * @param visibility
	 */
	public void setLogInButton(final boolean visibility)
	{
		logInButton.setVisible(visibility);
	}

	/**
	 * sets the value as either true or false to show in the scene.
	 *
	 * @param visibility
	 */
	public void setLogOutButton(final boolean visibility)
	{
		logOutButton.setVisible(visibility);
	}

	/**
	 * Shows the debug window.
	 *
	 * @param primaryStage
	 */
	public void start(final Stage primaryStage)
	{
		primaryStage.setTitle("VELHO WM DEBUG");
		Group root = new Group();
		Scene scene = new Scene(root, 300, 150);
		scene.getStylesheets().add(getClass().getResource("velho.css").toExternalForm());

		BorderPane rootBorderPane = new BorderPane();
		rootBorderPane.prefHeightProperty().bind(scene.heightProperty());
		rootBorderPane.prefWidthProperty().bind(scene.widthProperty());
		rootBorderPane.getStyleClass().add("standard-background-color");
		rootBorderPane.getStyleClass().add("standard-padding-half");

		final ComboBox<UserRole> roleListBox = new ComboBox<UserRole>();

		grid = new GridPane();
		grid.setVgap(5);
		grid.setHgap(10);

		roleListBox.getItems().addAll(roles);
		roleListBox.getSelectionModel().selectFirst();

		logOutButton.setVisible(false);

		Button sendRandomShipmentButton = new Button("Send Random Shipment");

		final Button fillUpPlatformButton = new Button("Fill Up Removal Platform");
		final Button emptyPlatformButton = new Button("Empty");

		logInButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(final ActionEvent event)
			{
				debugController.login(roleListBox.getValue());
			}

		});

		logOutButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(final ActionEvent event)
			{

				debugController.logout();
			}
		});

		scannerMoveValid.setOnAction(new EventHandler<ActionEvent>()
		{

			@Override
			public void handle(final ActionEvent event)
			{

				debugController.scannerMoveValid();
			}
		});

		sendRandomShipmentButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(final ActionEvent event)
			{
				DebugController.sendRandomShipment();
			}
		});

		fillUpPlatformButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(final ActionEvent event)
			{
				debugController.fillUpPlatform();
			}
		});

		emptyPlatformButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(final ActionEvent event)
			{
				debugController.emptyPlatform();
			}
		});

		grid.add(roleListBox, 0, 0);
		grid.add(logInButton, 1, 0);
		grid.add(logOutButton, 1, 1);
		grid.add(scannerMoveValid, 0, 1);
		grid.add(sendRandomShipmentButton, 0, 2);
		grid.add(fillUpPlatformButton, 0, 3);
		grid.add(emptyPlatformButton, 1, 3);

		rootBorderPane.setCenter(grid);
		root.getChildren().add(rootBorderPane);

		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
