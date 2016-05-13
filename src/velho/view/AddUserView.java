package velho.view;

import java.util.Set;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
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
	private final UserController userController;

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
	public AddUserView(final UserController userController, final Set<UserRole> rolelist)
	{
		this.roleSet = rolelist;
		this.userController = userController;
		this.grid = null;
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

			Label scenetitle = new Label(LocalizationController.getInstance().getString("addUserInfoSceneTitle"));
			scenetitle.getStyleClass().add("centered-title");
			grid.add(scenetitle, 0, 0, 2, 1);

			Label userID = new Label(LocalizationController.getInstance().getString("badgeIDInputFieldLabel"));
			grid.add(userID, 0, 1);

			final TextField badgeIDField = new TextField();
			grid.add(badgeIDField, 1, 1);
			badgeIDField.setId("badgeIDField");
			badgeIDField.setTooltip(new Tooltip(LocalizationController.getInstance().getString("tooltipBadgeIDField")));

			Label pinLabel = new Label(LocalizationController.getInstance().getString("PINInputFieldLabel"));
			grid.add(pinLabel, 0, 2);

			final TextField pinField = new TextField();
			grid.add(pinField, 1, 2);
			pinField.setId("pinField");
			pinField.setTooltip(new Tooltip(LocalizationController.getInstance().getString("tooltipPinField")));

			Label userFirstName = new Label(LocalizationController.getInstance().getString("userFirstNameFieldLabel"));
			grid.add(userFirstName, 0, 3);

			final TextField userFnameField = new TextField();
			grid.add(userFnameField, 1, 3);
			userFnameField.setId("userFnameField");
			userFnameField.setTooltip(new Tooltip(LocalizationController.getInstance().getString("tooltipFirstNameField")));

			Label userLastName = new Label(LocalizationController.getInstance().getString("userLastNameFieldLabel"));
			grid.add(userLastName, 0, 4);

			final TextField userLNameField = new TextField();
			grid.add(userLNameField, 1, 4);
			userLNameField.setId("userLNameField");
			userLNameField.setTooltip(new Tooltip(LocalizationController.getInstance().getString("tooltipLastNameField")));

			Label userInfo = new Label(LocalizationController.getInstance().getString("userRoleComboboxLabel"));
			grid.add(userInfo, 0, 5);

			final ComboBox<UserRole> listbox = new ComboBox<UserRole>();
			listbox.getItems().addAll(roleSet);
			listbox.getSelectionModel().selectFirst();
			grid.add(listbox, 1, 5);

			Button createButton = new Button(LocalizationController.getInstance().getString("createUserButton"));
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
					userController.createUser(badgeIDField.getText(), pinField.getText(), userFnameField.getText(), userLNameField.getText(),
							listbox.getValue());
				}
			});
			UIController.getInstance().recordView(this);
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
