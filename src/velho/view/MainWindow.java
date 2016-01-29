package velho.view;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import velho.controller.DatabaseController;
import velho.controller.DebugController;
import velho.controller.LoginController;
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
	private BorderPane borderPane;
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
			debugController = new DebugController(loginController);
		}
	}

	public static void main(String[] args)
	{
		launch(args);
	}

	public void setView(final Node view)
	{
		borderPane.setCenter(view);
	}

	/**
	 * Creates the main menu.
	 */
	public void showMainMenu()
	{
		TabPane tabPane = new TabPane();

		Tab tab2 = new Tab();
		tab2.setText("Add user");
		tab2.setContent(userController.getView());
		tabPane.getTabs().add(tab2);

		borderPane.setCenter(tabPane);
	}

	@Override
	public void start(final Stage primaryStage)
	{
		primaryStage.setTitle("Velho Warehouse Management");
		Group root = new Group();
		scene = new Scene(root, 1024, 700, Color.WHITE);
		borderPane = new BorderPane();
		borderPane.prefHeightProperty().bind(scene.heightProperty());
		borderPane.prefWidthProperty().bind(scene.widthProperty());

		root.getChildren().add(borderPane);

		loginController.checkLogin();

		primaryStage.setScene(scene);
		primaryStage.show();

		Stage secondStage = new Stage();
		debugController.createDebugWindow(secondStage);
	}

}
