package velho.view;

import java.util.Set;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import velho.controller.UserController;

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
	private UserController controller;
	/**
	 * The grid panel.
	 */
	private GridPane grid;
	private Set<String> rolenameSet;

	public AddUserView(UserController mcontroller, Set<String> rolelist)
	{
		rolenameSet = rolelist;
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

			TextField pinField = new TextField();

			TextField badgeIDField = new TextField();
			grid.add(badgeIDField, 1, 3);

			Label userInfo = new Label("User role:");
			grid.add(userInfo, 0, 4);

			final ComboBox<String> listbox = new ComboBox<String>();
			listbox.getItems().addAll(rolenameSet);
			listbox.getSelectionModel().selectFirst();
			grid.add(listbox, 1, 4);

			Button createButton = new Button("Create user");
			grid.add(createButton, 0, 5);

			createButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override public void handle(ActionEvent e)
				{
					controller.addUser(badgeIDField.getText(), pinField.getText(), userFnameField.getText(), userLNameField.getText(), listbox.getValue());
				}
			});
		}
		return grid;
	}
}
