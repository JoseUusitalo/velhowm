package velho.view;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import velho.controller.AddUserController;

/**
 * The add user view class.
 * 
 * @author Joona
 *
 */
public class AddUserView
{
	/**
	 * The add user controller.
	 */
	private AddUserController controller;
	/**
	 * The grid panel.
	 */
	private GridPane grid;

	public AddUserView(AddUserController mcontroller)
	{
		controller = mcontroller;
		grid = null;
	}

	/**
	 * Creates a grid for adding a new user.
	 * @return
	 */
	public GridPane getAddUserView()
	{
		if (grid == null)
		{
			grid = new GridPane();

			grid.setAlignment(Pos.CENTER);
			grid.setHgap(10);
			grid.setVgap(10);

			Label scenetitle = new Label("Add user info");
			scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
			grid.add(scenetitle, 0, 0, 2, 1);

			Label userFirstName = new Label("First name:");
			grid.add(userFirstName, 0, 1);

			TextField userFnameField = new TextField();
			grid.add(userFnameField, 1, 1);

			Label userLastName = new Label("Last name:");
			grid.add(userLastName, 0, 2);

			TextField userLNameField = new TextField();
			grid.add(userLNameField, 1, 2);

			Label userID = new Label("User ID:");
			grid.add(userID, 0, 3);

			TextField userIDField = new TextField();
			grid.add(userIDField, 1, 3);

			Label userInfo = new Label("User role:");
			grid.add(userInfo, 0, 4);

			ComboBox userRole = new ComboBox();
			grid.add(userRole, 1, 4);

			Button createButton = new Button("Create user");
			grid.add(createButton, 0, 5);

			createButton.setOnAction(new EventHandler<ActionEvent>()
			{

				@Override public void handle(ActionEvent e)
				{
					controller.addUser(userIDField.getText(), userFnameField.getText(), userLNameField.getText(), userRole.getValue());
				}
			});
		}
		return grid;
	}
}
