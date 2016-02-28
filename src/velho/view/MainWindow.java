package velho.view;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import velho.controller.DatabaseController;
import velho.controller.DebugController;
import velho.controller.ListController;
import velho.controller.LogController;
import velho.controller.LogDatabaseController;
import velho.controller.LoginController;
import velho.controller.RemovalListController;
import velho.controller.SearchController;
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
	 * Relative file path to the Apache log4j logger properties file.
	 */
	private static final String LOG4J_PATH = "src/velho/model/log4j.properties";

	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(MainWindow.class.getName());

	/**
	 * Enable or disable debug features.
	 */
	public static final boolean DEBUG_MODE = true;

	/**
	 * Enable or disable showing windows. DEBUG_MODE must be <code>true</code>
	 * to make this <code>false</code>.
	 */
	public static final boolean SHOW_WINDOWS = true;

	/**
	 * The height of the window.
	 */
	public static final double WINDOW_HEIGHT = 640;

	/**
	 * The width of the window.
	 */
	public static final double WINDOW_WIDTH = 1024;

	/**
	 * The current width of the window.
	 */
	public static ReadOnlyDoubleProperty WIDTH_PROPERTY;

	/**
	 * The {@link DebugController}.
	 */
	private static DebugController debugController;

	/**
	 * The {@link SearchController}.
	 */
	private SearchController searchController;

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
	 * The {@link RemovalListController}.
	 */
	private RemovalListController removalListController;

	/**
	 * The {@link LogController}.
	 */
	private LogController logController;

	/**
	 * The main window constructor.
	 */
	public MainWindow()
	{
		// Load the logger properties.
		PropertyConfigurator.configure(LOG4J_PATH);

		try
		{
			if (LogDatabaseController.connectAndInitialize())
			{
				if (!DEBUG_MODE)
				{
					// This is how we prevent Logisticians from reading logs.
					// Logs can now only be read through the database access to which can easily be limited.
					SYSLOG.info("Debug mode not enabled, disabling file and console appenders for all loggers.");

					// Remove console appenders from all system loggers.
					Logger.getRootLogger().removeAppender("SysConsoleAppender");

					// Remove file appenders from all system loggers.
					Logger.getRootLogger().removeAppender("SysRollingAppender");

					// Do the same for user and database loggers.
					Logger.getLogger("userLogger").removeAppender("UsrConsoleAppender");
					Logger.getLogger("userLogger").removeAppender("UsrRollingAppender");
					Logger.getLogger("dbLogger").removeAppender("DbConsoleAppender");
					Logger.getLogger("dbLogger").removeAppender("DbRollingAppender");

				}
				SYSLOG.info("Running VELHO Warehouse Management.");

				try
				{
					if (DatabaseController.connectAndInitialize())
					{
						SYSLOG.debug("Creating all controllers...");

						DatabaseController.loadData();
						debugController = new DebugController();
						userController = new UserController();
						logController = new LogController();
						listController = new ListController(userController);
						searchController = new SearchController(listController);
						removalListController = new RemovalListController(searchController);
						uiController = new UIController(this, listController, userController, removalListController, searchController, logController);

						LoginController.setControllers(uiController, debugController);

						SYSLOG.debug("All controllers created.");
					}
					else
					{
						SYSLOG.fatal("Failed to connect to database.");
						SYSLOG.info("Closing application.");
						System.exit(0);
					}
				}
				catch (ClassNotFoundException | ExistingDatabaseLinkException | NoDatabaseLinkException e1)
				{
					e1.printStackTrace();
				}
			}
			else
			{
				SYSLOG.fatal("Failed to connect to log database.");
				SYSLOG.info("Closing application.");
				System.exit(0);
			}
		}
		catch (ClassNotFoundException | ExistingDatabaseLinkException | NoDatabaseLinkException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * The main method of Velho Warehouse Management.
	 *
	 * @param args
	 */
	public static void main(final String[] args)
	{
		launch(args);
	}

	/**
	 * Adds a new tab to the main tab panel.
	 *
	 * @param tabName
	 * name of the tab
	 * @param view
	 * view to show in the tab
	 */
	public void addTab(final String tabName, final Node view)
	{
		if (mainTabPane == null)
			showMainMenu();

		final Tab tab = new Tab();
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
			final HBox statusBar = new HBox();
			statusBar.getStyleClass().add("status-bar");

			final HBox userBar = new HBox(10);

			final Label userName = new Label("Hello, " + LoginController.getCurrentUser().getRoleName() + " " + LoginController.getCurrentUser().getFullName());
			final Button logoutButton = new Button("Log Out");
			logoutButton.setPrefHeight(5.0);

			logoutButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
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
	 * Creates the window.
	 */
	@SuppressWarnings("unused")
	@Override
	public void start(final Stage primaryStage)
	{
		if (!SHOW_WINDOWS && DEBUG_MODE)
		{
			SYSLOG.debug("Windows are disabled.");
		}
		else
		{
			setUserAgentStylesheet(STYLESHEET_MODENA);

			primaryStage.setTitle("Velho Warehouse Management");
			final Group root = new Group();
			scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
			scene.getStylesheets().add(getClass().getResource("velho.css").toExternalForm());
			WIDTH_PROPERTY = scene.widthProperty();

			rootBorderPane = new BorderPane();
			rootBorderPane.getStyleClass().add("standard-background-color");
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
					public void handle(final WindowEvent event)
					{
						shutdown(primaryStage);
					}
				});
			}

			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
			{
				@Override
				public void handle(final WindowEvent event)
				{
					shutdown(primaryStage);
				}
			});
		}
	}

	/**
	 * A method called to shut down the software and perform any necessary
	 * cleanup.
	 *
	 * @param primaryStage
	 * the stage the main window is open in
	 */
	protected void shutdown(final Stage primaryStage)
	{
		primaryStage.close();

		if (DEBUG_MODE)
		{
			if (debugStage != null)
				debugStage.close();
		}

		try
		{
			DatabaseController.unlink();
		}
		catch (final NoDatabaseLinkException e)
		{
			// Ignore.
		}

		SYSLOG.info("Exit.");
	}

	/**
	 * Replaces the top view of the window
	 *
	 * @param view
	 * view to set the top of the window
	 */
	public void setTopView(final Node view)
	{
		rootBorderPane.setTop(view);
	}

	/**
	 * Replaces the right side view of the window
	 *
	 * @param view
	 * view to set the right of the window
	 */
	public void setRightView(final Node view)
	{
		rootBorderPane.setRight(view);
	}

	/**
	 * Replaces the bottom view of the window
	 *
	 * @param view
	 * view to set the bottom of the window
	 */
	public void setBottomView(final Node view)
	{
		rootBorderPane.setBottom(view);
	}

	/**
	 * Replaces the left side view of the window
	 *
	 * @param view
	 * view to set the l of the window
	 */
	public void setLeftView(final Node view)
	{
		rootBorderPane.setLeft(view);
	}

	/**
	 * Replaces the center view of the window
	 *
	 * @param view
	 * view to set the middle of the window
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
