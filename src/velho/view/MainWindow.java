package velho.view;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import velho.controller.UserController;

/**
 * The main window view class.
 * @author Joona
 *
 */

public class MainWindow extends Application
{

	UserController userController;

	public MainWindow()
	{
		userController = new UserController();
	}

	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(final Stage primaryStage)
	{

		/*
		 * controller = new MainWindowController(this);
		 * rootBorderPane = new BorderPane();
		 * rootBorderPane.setPadding(new Insets(10, 10, 10, 10));
		 *
		 * GridPane grid = new GridPane();
		 *
		 * grid.setAlignment(Pos.CENTER);
		 * grid.setHgap(10);
		 * grid.setVgap(10);
		 *
		 * Label scenetitle = new Label("Main Menu");
		 * scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
		 * grid.add(scenetitle, 0, 0, 2, 1);
		 */
		primaryStage.setTitle("Main Menu");
		Group root = new Group();
		Scene scene = new Scene(root, 1024, 700, Color.WHITE);

		TabPane tabPane = new TabPane();

		BorderPane borderPane = new BorderPane();

		Tab tab = new Tab();
		tab.setText("Login");
		HBox hbox = new HBox();
		hbox.getChildren().add(new Label("Login"));
		hbox.setAlignment(Pos.CENTER);
		tab.setContent(hbox);
		tabPane.getTabs().add(tab);

		Tab tab2 = new Tab();
		tab2.setText("Add user");
		tab2.setContent(userController.getView());
		tabPane.getTabs().add(tab2);

		// bind to take available space
		borderPane.prefHeightProperty().bind(scene.heightProperty());
		borderPane.prefWidthProperty().bind(scene.widthProperty());

		borderPane.setCenter(tabPane);
		root.getChildren().add(borderPane);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
