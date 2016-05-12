
package velho.view;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.hibernate.HibernateException;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import velho.controller.CSVController;
import velho.controller.DebugController;
import velho.controller.ExternalSystemsController;
import velho.controller.LocalizationController;
import velho.controller.LogController;
import velho.controller.LoginController;
import velho.controller.ManifestController;
import velho.controller.ProductController;
import velho.controller.RemovalListController;
import velho.controller.RemovalPlatformController;
import velho.controller.SearchController;
import velho.controller.UIController;
import velho.controller.database.DatabaseController;
import velho.controller.database.LogDatabaseController;
import velho.model.enums.SupportedTranslation;
import velho.model.interfaces.GenericView;

/**
 * The main window and class for VELHO Warehouse Management.
 *
 * @author Jose Uusitalo &amp; Joona Silvennoinen
 */
public class MainWindow extends Application implements GenericView
{
	/**
	 * Relative file path to the Apache log4j logger properties file.
	 */
	private static final String LOG4J_PATH = "src/log4j.properties";

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
	 * for this to affect anything.
	 */
	public static final boolean SHOW_WINDOWS = true;

	/**
	 * Enable TRACE level logging. DEBUG_MODE must be <code>true</code> for this
	 * to affect anything.
	 */
	public static final boolean SHOW_TRACE = true;

	/**
	 * Skips the entire main application code. DEBUG_MODE must be
	 * <code>true</code> for this
	 * to affect anything.
	 */
	public static final boolean SKIP_MAIN_CODE = false;

	/**
	 * The height of the window.
	 */
	public static final double WINDOW_HEIGHT = 640;

	/**
	 * The width of the window.
	 */
	public static final double WINDOW_WIDTH = 1024;

	/**
	 * The {@link DebugController}.
	 */
	private static DebugController debugController;

	/**
	 * The {@link SearchController}.
	 */
	private SearchController searchController;

	/**
	 * The {@link UIController}.
	 */
	private static UIController uiController;

	/**
	 * The {@link RemovalListController}.
	 */
	private RemovalListController removalListController;

	/**
	 * The {@link ManifestController}.
	 */
	private ManifestController manifestController;

	/**
	 * The {@link RemovalPlatformController}.
	 */
	private RemovalPlatformController removalPlatformController;

	/**
	 * The current width of the window.
	 */
	public static ReadOnlyDoubleProperty widthProperty;

	/**
	 * The root layout of the main window.
	 */
	private BorderPane rootBorderPane;

	/**
	 * The main menu tab panel.
	 */
	private TabPane mainTabPane;

	/**
	 * The map of tab names and tab objects used for selecting tabs in the tab
	 * pane.
	 */
	private Map<String, Tab> tabMap;

	/**
	 * The main scene.
	 */
	private Scene scene;

	/**
	 * The debug window stage.
	 */
	private Stage debugStage;

	/**
	 * The {@link ProductController}.
	 */
	private ProductController productController;

	/**
	 * A label showing the status of the removal platform.
	 */
	private Label removalPlatformStatus;

	/**
	 * The {@link CSVController}.
	 */
	private CSVController csvController;

	/**
	 * The primary stage where the window is.
	 */
	private Stage primaryStage;

	/**
	 * The main window constructor.
	 */
	public MainWindow()
	{
		prepareLogger();
		LocalizationController.getInstance().initializeBundle();
		prepareDatabase();
		runApp();
	}

	/**
	 * Prepares the main database by loading the sample data if needed.
	 */
	private static void prepareDatabase()
	{
		try
		{
			DatabaseController.getInstance().loadSampleData();
		}
		catch (final HibernateException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Prepares the logger by configuring it and connecting to the database.
	 */
	private static void prepareLogger()
	{
		// Load the logger properties.
		PropertyConfigurator.configure(LOG4J_PATH);

		try
		{
			if (LogDatabaseController.getInstance().connectAndInitialize())
			{
				if (!DEBUG_MODE)
				{
					/*
					 * This is how we prevent Logisticians from reading logs.
					 * Logs can now only be read through the database, access to
					 * which can easily be limited.
					 */
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
				else
				{
					if (SHOW_TRACE)
					{
						SYSLOG.debug("Enabling trace.");
						((AppenderSkeleton) Logger.getRootLogger().getAppender("SysConsoleAppender")).setThreshold(Level.TRACE);
						((AppenderSkeleton) Logger.getLogger("userLogger").getAppender("UsrConsoleAppender")).setThreshold(Level.TRACE);
						((AppenderSkeleton) Logger.getLogger("dbLogger").getAppender("DbConsoleAppender")).setThreshold(Level.TRACE);
					}
				}
			}
			else
			{
				SYSLOG.fatal("Failed to connect to log database.");
				SYSLOG.info("Closing application.");
				System.exit(0);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * The main method for running the application.
	 */
	private void runApp()
	{
		SYSLOG.info("Running VELHO Warehouse Management.");

		try
		{
			DatabaseController.getInstance().link();

			if (DatabaseController.getInstance().isLinked())
			{
				SYSLOG.debug("Creating all controllers...");

				// FIXME: Convert all controllers to use the singleton pattern.

				uiController = new UIController();
				productController = new ProductController();

				csvController = new CSVController(this);
				manifestController = new ManifestController(this);
				removalPlatformController = new RemovalPlatformController(this);
				debugController = new DebugController(removalPlatformController);
				searchController = new SearchController(productController);
				removalListController = new RemovalListController(searchController);

				ExternalSystemsController.getInstance().setControllers(manifestController);
				LoginController.getInstance().setControllers(uiController, debugController);
				LocalizationController.getInstance().setControllers(uiController);

				//@formatter:off
				uiController.setControllers(this,
											removalListController,
											searchController,
											manifestController,
											productController,
											removalPlatformController,
											csvController);
				//@formatter:on

				SYSLOG.debug("All controllers created.");
			}
			else
			{
				SYSLOG.fatal("Failed to connect to database.");
				SYSLOG.info("Closing application.");
				System.exit(0);
			}
		}
		catch (ClassNotFoundException e1)
		{
			e1.printStackTrace();
		}
	}

	/**
	 * The main method of VELHO Warehouse Management.
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
	 * @param tabName name of the tab
	 * @param view view to show in the tab
	 *
	 * @return <code>true</code> if tab was added
	 */
	public boolean addTab(final String tabName, final Node view)
	{
		if (mainTabPane == null)
			showMainMenu();

		final Tab tab = new Tab();
		tab.setText(tabName);
		tab.setContent(view);

		tabMap.put(tabName, tab);
		return mainTabPane.getTabs().add(tab);
	}

	/**
	 * Forcibly selects the specified tab and changes the view in the main
	 * window to that tab.
	 *
	 * @param tabName name of the tab
	 */
	public void selectTab(final String tabName)
	{
		if (mainTabPane != null)
			mainTabPane.getSelectionModel().select(tabMap.get(tabName));
	}

	/**
	 * Creates the main menu.
	 */
	public void showMainMenu()
	{
		if (mainTabPane == null)
		{
			mainTabPane = new TabPane();
			tabMap = new HashMap<String, Tab>();
			mainTabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

			mainTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>()
			{
				@Override
				public void changed(final ObservableValue<? extends Tab> old, final Tab oldTab, final Tab newTab)
				{
					// Perform an action whenever the tab changes.
					switch (newTab.getText())
					{
						case "Logs":
							LogController.getInstance().refresh();
							break;
						default:
							// Do nothing.
					}
				}

			});
		}

		// Force log in to see main menu.
		if (LoginController.getInstance().checkLogin())
		{
			final GridPane statusBar = new GridPane();
			statusBar.getStyleClass().add("status-bar");

			final ComboBox<SupportedTranslation> languageBox = new ComboBox<SupportedTranslation>();
			final Label languageChange = new Label(LocalizationController.getInstance().getString("changeTranslationLabel"));
			languageBox.getItems().addAll(SupportedTranslation.values());
			languageBox.getSelectionModel().select(LocalizationController.getInstance().getCurrentTranslation());
			languageBox.valueProperty().addListener(new ChangeListener<SupportedTranslation>()
			{
				@SuppressWarnings("rawtypes")
				@Override
				public void changed(final ObservableValue obsValue, final SupportedTranslation oldValue, final SupportedTranslation newValue)
				{
					if (oldValue == null || !oldValue.equals(newValue))
					{
						LocalizationController.getInstance().changeTranslation(newValue);
					}
				}
			});

			final HBox platformStatus = new HBox(3);
			final Label removalPlatform = new Label(LocalizationController.getInstance().getString("removalPlatformStatusLabel"));
			removalPlatform.setId("removalPlatformStatusLabel");
			removalPlatformStatus = new Label();
			platformStatus.getChildren().addAll(languageChange, languageBox, removalPlatform, removalPlatformStatus);
			platformStatus.setAlignment(Pos.CENTER_LEFT);

			final HBox userStatus = new HBox(10);
			final Label userName = new Label(LocalizationController.getInstance().getCompoundString("helloUserMessage",
					LoginController.getInstance().getCurrentUser().getRoleName(), LoginController.getInstance().getCurrentUser().getFullName()));
			userName.setId("userName");

			final Button logoutButton = new Button(LocalizationController.getInstance().getString("logOutButton"));
			logoutButton.setPrefHeight(5.0);
			userStatus.getChildren().addAll(userName, logoutButton);
			userStatus.setAlignment(Pos.CENTER_RIGHT);

			logoutButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					LoginController.getInstance().logout();
				}
			});

			statusBar.add(platformStatus, 0, 0);
			statusBar.add(userStatus, 1, 0);
			GridPane.setHgrow(platformStatus, Priority.ALWAYS);
			rootBorderPane.setBottom(statusBar);
			UIController.recordView(this);
		}
		rootBorderPane.setCenter(mainTabPane);
	}

	/**
	 * Creates the window.
	 */
	@Override
	public void start(final Stage mainStage)
	{
		this.primaryStage = mainStage;

		setUserAgentStylesheet(STYLESHEET_MODENA);
		this.primaryStage.setTitle(LocalizationController.getInstance().getString("mainWindowTitle"));
		final Group root = new Group();
		scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		scene.getStylesheets().add(getClass().getResource("velho.css").toExternalForm());
		widthProperty = scene.widthProperty();

		rootBorderPane = getRootBorderPane();

		root.getChildren().add(rootBorderPane);

		this.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
		{
			@Override
			public void handle(final WindowEvent event)
			{
				shutdown(MainWindow.this.primaryStage);
			}
		});

		if (!SKIP_MAIN_CODE)
		{
			LoginController.getInstance().checkLogin();
			this.primaryStage.setScene(scene);
			this.primaryStage.show();

			if (DEBUG_MODE)
			{
				debugStage = new Stage();
				debugController.createDebugWindow(debugStage);

				debugStage.setOnCloseRequest(new EventHandler<WindowEvent>()
				{
					@Override
					public void handle(final WindowEvent event)
					{
						shutdown(MainWindow.this.primaryStage);
					}
				});
			}
		}
		else if (DEBUG_MODE)
		{
			skip();
			shutdown(this.primaryStage);
		}
	}

	/**
	 * Gets the root {@link BorderPane} of the main window.
	 *
	 * @return the root node
	 */
	private BorderPane getRootBorderPane()
	{
		if (rootBorderPane == null)
		{
			rootBorderPane = new BorderPane();
			rootBorderPane.getStyleClass().add("standard-background-color");
			rootBorderPane.prefHeightProperty().bind(scene.heightProperty());
			rootBorderPane.prefWidthProperty().bind(scene.widthProperty());
		}
		return rootBorderPane;
	}

	/**
	 * A method called to shut down the software and perform any necessary
	 * cleanup.
	 *
	 * @param primaryStage the stage the main window is open in
	 */
	protected void shutdown(final Stage mainstage)
	{
		mainstage.close();

		if (DEBUG_MODE && debugStage != null)
			debugStage.close();

		DatabaseController.getInstance().closeSessionFactory();
		DatabaseController.getInstance().unlink();

		SYSLOG.info("Exit.");

		LogDatabaseController.getInstance().unlink();
	}

	/**
	 * Replaces the top view of the window.
	 *
	 * @param view is a view to be set to the top of the window
	 */
	public void setTopView(final Node view)
	{
		rootBorderPane.setTop(view);
	}

	/**
	 * Replaces the right side view of the window.
	 *
	 * @param view a view to be set to the right of the window
	 */
	public void setRightView(final Node view)
	{
		rootBorderPane.setRight(view);
	}

	/**
	 * Replaces the bottom view of the window.
	 *
	 * @param view a view to set the bottom of the window
	 */
	public void setBottomView(final Node view)
	{
		rootBorderPane.setBottom(view);
	}

	/**
	 * Replaces the left side view of the window.
	 *
	 * @param view a view to set the l of the window
	 */
	public void setLeftView(final Node view)
	{
		rootBorderPane.setLeft(view);
	}

	/**
	 * Replaces the center view of the window.
	 *
	 * @param view a view to set the middle of the window
	 */
	public void setCenterView(final Node view)
	{
		rootBorderPane.setCenter(view);
	}

	/**
	 * Updates the label in the status bar that shows how full the removal
	 * platform is.
	 *
	 * @param percent percentage as text
	 */
	public void setRemovalPlatformFullPercent(final String percent)
	{
		removalPlatformStatus.setText(percent + "%");
	}

	/**
	 * Called when the main application code is skipped.
	 */
	@SuppressWarnings("static-method")
	private void skip()
	{
		SYSLOG.info("Main application code skipped.");

		// It works.
		// System.out.println("id:" + new AssignedIdentifierGenerator().generate((SessionImplementor)
		// HibernateSessionFactory.getInstance().getCurrentSession(),
		// new User(120, "new", "thing", "111111", null, UserRole.ADMINISTRATOR)));
	}

	@Override
	public void recreate()
	{
		mainTabPane = null;
	}

	@Override
	public void destroy()
	{
		mainTabPane = null;
	}

	/**
	 * Gets the primary stage of the main window.
	 *
	 * @return the primary stage
	 */
	public Stage getStage()
	{
		return primaryStage;
	}
}
