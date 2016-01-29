package velho.view;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
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
	public MainWindow()
	{
		try
		{
			userController = new UserController();
		}
		catch (NoDatabaseLinkException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		System.out.println("Running VELHO Warehouse Management.");
		DatabaseController.connect();

		if (DatabaseController.isLinked())
		{
			uiController = new UIController();
			loginController = new LoginController();
			try
			{
				debugController = new DebugController(loginController);
			} catch (NoDatabaseLinkException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			listController = new ListViewController();
			launch(args);
		}
	}

	@Override
	public void start(final Stage primaryStage)
	{
		primaryStage.setTitle("Main Menu");
		Group root = new Group();
		Scene scene = new Scene(root, 1024, 700, Color.WHITE);

		TabPane tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		BorderPane borderPane = new BorderPane();

		Tab tab = new Tab();
		tab.setText("Login");
		HBox hbox = new HBox();
		hbox.getChildren().add(new Label("Login"));
		hbox.setAlignment(Pos.CENTER);
		tab.setContent(hbox);
		tabPane.getTabs().add(tab);
		

		Tab tab2 = new Tab();
		tab2.setText("Add User");
		tab2.setContent(userController.getView());
		tabPane.getTabs().add(tab2);
		
		Tab tab3 = new Tab();
		System.out.println(tab3);
		System.out.println(listController.getView());
		tab3.setText("User List");
		tab3.setContent(listController.getView());
		tabPane.getTabs().add(tab3);
		
		

		// bind to take available space
		borderPane.prefHeightProperty().bind(scene.heightProperty());
		borderPane.prefWidthProperty().bind(scene.widthProperty());

		borderPane.setCenter(tabPane);
		root.getChildren().add(borderPane);
		primaryStage.setScene(scene);
		primaryStage.show();

		Stage secondStage = new Stage();
		debugController.createDebugWindow(secondStage);
	}

}
