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
import velho.controller.LocalizationController;
import velho.controller.UIController;
import velho.controller.UserController;
import velho.model.enums.UserRole;
import velho.model.interfaces.GenericView;

/**
 * The add user view class.
 *
 * @author Joona Silvennoinen
 */
public class AddUserView implements GenericView
{
	/**
	 * The add user controller.
	 */
	private final UserController controller;

	/**
	 * The grid panel.
	 */
	private GridPane grid;

	/**
	 * A set of user role names.
	 */
	private final Set<UserRole> roleSet;

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

			Label scenetitle = new Label(LocalizationController.getString("addUserInfoSceneTitle"));
			scenetitle.getStyleClass().add("centered-title");
			grid.add(scenetitle, 0, 0, 2, 1);

			Label userID = new Label(LocalizationController.getString("badgeIDInputFieldLabel"));
			grid.add(userID, 0, 1);

			final TextField badgeIDField = new TextField();
			grid.add(badgeIDField, 1, 1);
			badgeIDField.setId("badgeIDField");

			Label pinLabel = new Label(LocalizationController.getString("PINInputFieldLabel"));
			grid.add(pinLabel, 0, 2);

			final TextField pinField = new TextField();
			grid.add(pinField, 1, 2);
			pinField.setId("pinField");

			Label userFirstName = new Label(LocalizationController.getString("userFirstNameFieldLabel"));
			grid.add(userFirstName, 0, 3);

			final TextField userFnameField = new TextField();
			grid.add(userFnameField, 1, 3);
			userFnameField.setId("userFnameField");

			Label userLastName = new Label(LocalizationController.getString("userLastNameFieldLabel"));
			grid.add(userLastName, 0, 4);

			final TextField userLNameField = new TextField();
			grid.add(userLNameField, 1, 4);
			userLNameField.setId("userLNameField");

			Label userInfo = new Label(LocalizationController.getString("userRoleComboboxLabel"));
			grid.add(userInfo, 0, 5);

			final ComboBox<UserRole> listbox = new ComboBox<UserRole>();
			listbox.getItems().addAll(roleSet);
			listbox.getSelectionModel().selectFirst();
			grid.add(listbox, 1, 5);

			Button createButton = new Button(LocalizationController.getString("createUserButton"));
			grid.add(createButton, 0, 6);
			createButton.setId("createButton");

			/**
			 * Handles the button press event.
			 */
			createButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					controller.createUser(badgeIDField.getText(), pinField.getText(), userFnameField.getText(), userLNameField.getText(), listbox.getValue());
				}
			});
			UIController.recordView(this);
		}
		return grid;
	}

	@Override
	public void recreate()
	{
		grid = null;
		getView();
	}

	@Override
	public void destroy()
	{
		grid = null;
	}
}
