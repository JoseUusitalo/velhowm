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
	private Button logInButton;

	/**
	 * Button has been tagged logOutButton and named as "Log Out" on the scene.
	 */
	public Button logOutButton;

	/**
	 * Button for sending a badge ID from the debug window.
	 */
	public Button scanBadgeButton;

	/**
	 * Button has been tagged scannerMoveValid and named as "Scanner Move Valid"
	 * on the scene.
	 */
	public Button scannerMoveValid;

	/**
	 * The DebugController has been tagged as controller.
	 */
	private final DebugController debugController;

	/**
	 * A list of for the unique roles in the database.
	 */
	private final List<UserRole> roles;

	/**
	 * A list of unique badge numbers in the database.
	 */
	private final List<String> badgeNumbers;

	/**
	 * @param debugController
	 * @param rolelist
	 */
	public DebugWindow(final DebugController debugController, final List<UserRole> userRoles, final List<String> badges)
	{
		roles = userRoles;
		badgeNumbers = badges;
		this.debugController = debugController;
	}

	/**
	 * Changes the visiblity of the log in button.
	 *
	 * @param visibility show log in button?
	 */
	public void setLogInButton(final boolean visibility)
	{
		logInButton.setVisible(visibility);
	}

	/**
	 * Changes the visiblity of the log out button.
	 *
	 * @param visibility show scan badge button?
	 */
	public void setLogOutButton(final boolean visibility)
	{
		logOutButton.setVisible(visibility);
	}

	/**
	 * Changes the visiblity of the scan badge button.
	 *
	 * @param visibility show scan badge button?
	 */
	public void setScanBadgeButton(final boolean visibility)
	{
		scanBadgeButton.setVisible(visibility);
	}

	/**
	 * Shows the debug window.
	 *
	 * @param primaryStage
	 */
	public void start(final Stage primaryStage)
	{
		primaryStage.setTitle("VELHO WM DEBUG");
		final Group root = new Group();
		final Scene scene = new Scene(root, 300, 150);
		scene.getStylesheets().add(getClass().getResource("velho.css").toExternalForm());

		final BorderPane rootBorderPane = new BorderPane();
		rootBorderPane.prefHeightProperty().bind(scene.heightProperty());
		rootBorderPane.prefWidthProperty().bind(scene.widthProperty());
		rootBorderPane.getStyleClass().add("standard-background-color");
		rootBorderPane.getStyleClass().add("standard-padding-half");

		final GridPane grid = new GridPane();
		grid.setVgap(5);
		grid.setHgap(10);

		final ComboBox<UserRole> roleListBox = new ComboBox<UserRole>();
		roleListBox.getItems().addAll(roles);
		roleListBox.getSelectionModel().selectFirst();

		logInButton = new Button("Log In");
		logOutButton = new Button("Log Out");
		logOutButton.setVisible(false);

		final ComboBox<String> badgeList = new ComboBox<String>();
		badgeList.getItems().addAll(badgeNumbers);
		badgeList.getSelectionModel().selectFirst();

		scanBadgeButton = new Button("Scan Badge");

		final Button sendRandomShipmentButton = new Button("Send Random Shipment");

		final Button fillUpPlatformButton = new Button("Fill Up Removal Platform");
		final Button emptyPlatformButton = new Button("Empty");

		scannerMoveValid = new Button("Scanner Move Valid");

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

		scanBadgeButton.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(final ActionEvent event)
			{
				DebugController.scanBadge(badgeList.getValue());
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
		grid.add(logOutButton, 1, 0);

		grid.add(badgeList, 0, 1);
		grid.add(scanBadgeButton, 1, 1);

		grid.add(scannerMoveValid, 0, 2);
		grid.add(sendRandomShipmentButton, 0, 3);
		grid.add(fillUpPlatformButton, 0, 4);
		grid.add(emptyPlatformButton, 1, 4);

		rootBorderPane.setCenter(grid);
		root.getChildren().add(rootBorderPane);

		primaryStage.setScene(scene);
		primaryStage.show();
	}
}
