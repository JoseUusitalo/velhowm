package velho.view;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import velho.controller.DatabaseController;
import velho.controller.DebugController;
import velho.controller.ListController;
import velho.controller.LoginController;
import velho.controller.UIController;
import velho.controller.UserController;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseLinkException;

/**
 * The main window and class for Velho Warehouse Management.
 *
 * @author Jose Uusitalo &amp; Joona
 */
public class MainWindow extends Application
{
	/**
	 * Enable or disable debug features.
	 */
	public static final boolean DEBUG_MODE = false;

	/**
	 * Enable or disable showing windows. DEBUG_MODE must be <code>true</code> to make this <code>false</code>.
	 */
	public static final boolean SHOW_WINDOWS = true;

	/**
	 * The {@link DebugController}.
	 */
	private static DebugController debugController;

	/**
	 * The {@link UserController}.
	 */
	private UserController userController;

	/**
	 * The {@link UIController}.
	 */
	private static UIController uiController;

	/**
	 * The root layout of the main window.
	 */
	private BorderPane rootBorderPane;

	/**
	 * The main menu tab panel.
	 */
	private TabPane mainTabPane;

	/**
	 * The main scene.
	 */
	private Scene scene;

	/**
	 * The {@link ListController}.
	 */
	private ListController listController;

	/**
	 * The debug window stage.
	 */
	private Stage debugStage;

	/**
	 * The main window constructor.
	 */
	public MainWindow()
	{
		System.out.println("Running VELHO Warehouse Management.");
		try
		{
			DatabaseController.connectAndInitialize();

			debugController = new DebugController();
			userController = new UserController();

			listController = new ListController(userController);
			uiController = new UIController(this, listController, userController);

			LoginController.setControllers(uiController, debugController);
		}
		catch (ClassNotFoundException | ExistingDatabaseLinkException | NoDatabaseLinkException e1)
		{
			e1.printStackTrace();
		}
	}

	/**
	 * The main method of Velho Warehouse Management.
	 *
	 * @param args
	 */
	public static void main(String[] args)
	{
		launch(args);
	}

	/**
	 * Adds a new tab to the main tab panel.
	 *
	 * @param tabName name of the tab
	 * @param view view to show in the tab
	 */
	public void addTab(final String tabName, final Node view)
	{
		if (mainTabPane == null)
			showMainMenu();

		Tab tab = new Tab();
		tab.setText(tabName);
		tab.setContent(view);
		mainTabPane.getTabs().add(tab);
	}

	/**
	 * Creates the main menu.
	 */
	public void showMainMenu()
	{
		if (mainTabPane == null)
		{
			mainTabPane = new TabPane();
			mainTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		}

		// Force log in to see main menu.
		if (LoginController.checkLogin())
		{
			HBox statusBar = new HBox();
			statusBar.setAlignment(Pos.CENTER_RIGHT);
			statusBar.setPadding(new Insets(4.0));

			// TODO: Use CSS.
			statusBar.setBackground(new Background(new BackgroundFill(Paint.valueOf(Color.LIGHTGRAY.toString()), null, null)));
			statusBar.setBorder(new Border(
					new BorderStroke(Paint.valueOf("b5b5b5"), Paint.valueOf(Color.TRANSPARENT.toString()), Paint.valueOf(Color.TRANSPARENT.toString()),
							Paint.valueOf(Color.TRANSPARENT.toString()), BorderStrokeStyle.SOLID, null, null, null, null, null, null)));

			HBox userBar = new HBox(10);

			Label userName = new Label("Hello, " + LoginController.getCurrentUser().getRoleName() + " " + LoginController.getCurrentUser().getFullName());
			Button logoutButton = new Button("Log Out");
			logoutButton.setPrefHeight(5.0);

			logoutButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent event)
				{
					LoginController.logout();
				}
			});

			userBar.getChildren().addAll(userName, logoutButton);
			userBar.setAlignment(Pos.CENTER_RIGHT);

			statusBar.getChildren().add(userBar);
			rootBorderPane.setBottom(statusBar);
		}
		rootBorderPane.setCenter(mainTabPane);
	}

	/**
	 * Loads the data from the database and creates the window.
	 */
	@SuppressWarnings("unused")
	@Override
	public void start(final Stage primaryStage)
	{
		DatabaseController.loadData();

		if (!SHOW_WINDOWS && DEBUG_MODE)
		{
			System.out.println("Windows are disabled.");
		}
		else
		{
			primaryStage.setTitle("Velho Warehouse Management");
			Group root = new Group();
			scene = new Scene(root, 1024, 700, Color.WHITE);
			rootBorderPane = new BorderPane();
			rootBorderPane.prefHeightProperty().bind(scene.heightProperty());
			rootBorderPane.prefWidthProperty().bind(scene.widthProperty());

			root.getChildren().add(rootBorderPane);

			LoginController.checkLogin();

			primaryStage.setScene(scene);
			primaryStage.show();

			if (DEBUG_MODE)
			{

				debugStage = new Stage();
				debugController.createDebugWindow(debugStage);

				debugStage.setOnCloseRequest(new EventHandler<WindowEvent>()
				{
					@Override
					public void handle(WindowEvent event)
					{
						shutdown(primaryStage);
					}
				});
			}

			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
			{
				@Override
				public void handle(WindowEvent event)
				{
					shutdown(primaryStage);
				}
			});
		}
	}

	/**
	 * A method called to shut down the software and perform any necessary cleanup.
	 *
	 * @param primaryStage the stage the main window is open in
	 */
	protected void shutdown(final Stage primaryStage)
	{
		primaryStage.close();

		if (DEBUG_MODE)
		{
			if (debugStage != null)
				debugStage.close();
		}
		System.out.println("[MainWindow] Closing windows.");
		try
		{
			DatabaseController.unlink();
		}
		catch (NoDatabaseLinkException e)
		{
			// Ignore.
		}
		System.out.println("Exit.");
	}

	/**
	 * Replaces the top view of the window
	 *
	 * @param view view to set the top of the window
	 */
	public void setTopView(final Node view)
	{
		rootBorderPane.setTop(view);
	}

	/**
	 * Replaces the right side view of the window
	 *
	 * @param view view to set the right of the window
	 */
	public void setRightView(final Node view)
	{
		rootBorderPane.setRight(view);
	}

	/**
	 * Replaces the bottom view of the window
	 *
	 * @param view view to set the bottom of the window
	 */
	public void setBottomView(final Node view)
	{
		rootBorderPane.setBottom(view);
	}

	/**
	 * Replaces the left side view of the window
	 *
	 * @param view view to set the l of the window
	 */
	public void setLeftView(final Node view)
	{
		rootBorderPane.setLeft(view);
	}

	/**
	 * Replaces the center view of the window
	 *
	 * @param view view to set the middle of the window
	 */
	public void setCenterView(final Node view)
	{
		rootBorderPane.setCenter(view);
	}

	/**
	 * Destroys the main tab panel.
	 */
	public void destroy()
	{
		mainTabPane = null;
	}
}
