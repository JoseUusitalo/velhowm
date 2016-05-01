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
import velho.controller.UserController;
import velho.model.enums.UserRole;

/**
 * The add user view class.
 *
 * @author Joona Silvennoinen
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

	/**
	 * A set of user role names.
	 */
	private Set<UserRole> roleSet;

	/**
	 * @param mcontroller
	 * @param rolelist
	 */
	public AddUserView(final UserController mcontroller, final Set<UserRole> rolelist)
	{
		roleSet = rolelist;
		controller = mcontroller;
		grid = null;
	}

	/**
	 * Creates a grid for adding a new user.
	 *
	 * @return the grid
	 */
	public GridPane getView()
	{
		if (grid == null)
		{
			grid = new GridPane();

			grid.setAlignment(Pos.CENTER);
			grid.setHgap(10);
			grid.setVgap(10);

			Label scenetitle = new Label("Add user info");
			scenetitle.getStyleClass().add("centered-title");
			grid.add(scenetitle, 0, 0, 2, 1);

			Label userID = new Label("Badge ID:");
			grid.add(userID, 0, 1);

			final TextField badgeIDField = new TextField();
			grid.add(badgeIDField, 1, 1);

			Label pinLabel = new Label("PIN:");
			grid.add(pinLabel, 0, 2);

			final TextField pinField = new TextField();
			grid.add(pinField, 1, 2);

			Label userFirstName = new Label("First name:");
			grid.add(userFirstName, 0, 3);

			final TextField userFnameField = new TextField();
			grid.add(userFnameField, 1, 3);

			Label userLastName = new Label("Last name:");
			grid.add(userLastName, 0, 4);

			final TextField userLNameField = new TextField();
			grid.add(userLNameField, 1, 4);

			Label userInfo = new Label("User role:");
			grid.add(userInfo, 0, 5);

			final ComboBox<UserRole> listbox = new ComboBox<UserRole>();
			listbox.getItems().addAll(roleSet);
			listbox.getSelectionModel().selectFirst();
			grid.add(listbox, 1, 5);

			Button createButton = new Button("Create user");
			grid.add(createButton, 0, 6);

			/**
			 * Handles the button press event.
			 */
			createButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent e)
				{
					controller.createUser(badgeIDField.getText(), pinField.getText(), userFnameField.getText(), userLNameField.getText(), listbox.getValue());
				}
			});
		}
		return grid;
	}

	/**
	 * Destroys the view.
	 */
	public void destroy()
	{
		grid = null;
	}
}
