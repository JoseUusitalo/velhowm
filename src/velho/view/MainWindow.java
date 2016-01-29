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
import velho.controller.DatabaseController;
import velho.controller.DebugController;
import velho.controller.LoginController;
import velho.controller.ListViewController;
import velho.controller.UIController;
import velho.controller.UserController;
import velho.model.exceptions.NoDatabaseLinkException;

/**
 * The main window view class.
 * @author Joona
 *
 */

public class MainWindow extends Application
{
	private static DebugController debugController;
	private UserController userController;
	private static UIController uiController;
	private static LoginController loginController;
	private static ListViewController listController;

	private BorderPane rootBorderPane;
	private TabPane mainTabPane;
	private Scene scene;

	public MainWindow()
	{
		System.out.println("Running VELHO Warehouse Management.");
		DatabaseController.connect();

		try
		{
			userController = new UserController();
		}
		catch (NoDatabaseLinkException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (DatabaseController.isLinked())
		{
			uiController = new UIController(this);
			loginController = new LoginController(uiController);
			listController = new ListViewController();
			try
			{
				debugController = new DebugController(loginController);
				loginController.setDebugController(debugController);
			}
			catch (NoDatabaseLinkException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args)
	{
		launch(args);
	}

	public void setView(final Node view)
	{
		rootBorderPane.setCenter(view);
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

			Tab tab2 = new Tab();
			tab2.setText("Add User");
			tab2.setContent(userController.getView());
			mainTabPane.getTabs().add(tab2);

			Tab tab3 = new Tab();
			tab3.setText("User List");
			tab3.setContent(listController.getView());
			mainTabPane.getTabs().add(tab3);
		}

		// Force log in to see main menu.
		if (loginController.checkLogin())
		{
			HBox statusBar = new HBox();
			statusBar.setAlignment(Pos.CENTER_RIGHT);
			statusBar.setPadding(new Insets(4.0));
			
			// TODO: Use CSS.
			statusBar.setBackground(new Background(new BackgroundFill(Paint.valueOf(Color.LIGHTGRAY.toString()), null, null)));
			statusBar.setBorder(new Border(new BorderStroke(Paint.valueOf("b5b5b5"), Paint.valueOf(Color.TRANSPARENT.toString()), Paint.valueOf(Color.TRANSPARENT.toString()), Paint.valueOf(Color.TRANSPARENT.toString()), BorderStrokeStyle.SOLID, null, null, null, null, null, null)));
			
			HBox userBar = new HBox(10);

			Label userName = new Label("Hello, " + loginController.getCurrentUser().toString());
			Button logoutButton = new Button("Log Out");
			
			logoutButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent event)
				{
					loginController.logout();
					debugController.logout();
				}
			});
			
			userBar.getChildren().addAll(userName, logoutButton);
			userBar.setAlignment(Pos.CENTER_RIGHT);
			
			statusBar.getChildren().add(userBar);
			rootBorderPane.setBottom(statusBar);
		}
		rootBorderPane.setCenter(mainTabPane);
	}

	@Override
	public void start(final Stage primaryStage)
	{
		primaryStage.setTitle("Velho Warehouse Management");
		Group root = new Group();
		scene = new Scene(root, 1024, 700, Color.WHITE);
		rootBorderPane = new BorderPane();
		rootBorderPane.prefHeightProperty().bind(scene.heightProperty());
		rootBorderPane.prefWidthProperty().bind(scene.widthProperty());

		root.getChildren().add(rootBorderPane);

		loginController.checkLogin();

		primaryStage.setScene(scene);
		primaryStage.show();

		Stage secondStage = new Stage();
		debugController.createDebugWindow(secondStage);
	}

}
