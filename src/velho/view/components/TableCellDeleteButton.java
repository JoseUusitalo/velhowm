package velho.view.components;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.text.Font;
import velho.controller.LoginController;
import velho.controller.interfaces.UIActionController;
import velho.model.User;

/**
 * A table cell with a built-in delete button.
 *
 * @author Jose Uusitalo
 */
public class TableCellDeleteButton extends TableCell<Object, String>
{
	/**
	 * The button itself.
	 */
	private final Button button;

	/**
	 * The controller to send information to when this button is pressed.
	 */
	private final UIActionController controller;

	/**
	 * @param text button text
	 */
	public TableCellDeleteButton(final UIActionController parentController, final String text)
	{
		this.controller = parentController;
		button = new Button(text);
		button.setFont(new Font(12));

		/**
		 * Handles the button press event.
		 */
		button.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(final ActionEvent event)
			{
				// Get selected object and send information to parent
				// controller.
				controller.deleteAction(TableCellDeleteButton.this.getTableView().getItems().get(TableCellDeleteButton.this.getIndex()));
			}
		});
	}

	/**
	 *
	 */
	@Override
	protected void updateItem(final String string, final boolean empty)
	{
		super.updateItem(string, empty);

		// Display button only if the row is not empty.
		if (!empty)
		{
			final Object rowObject = getTableView().getItems().get(getIndex());

			if (rowObject instanceof User)
			{
				// Permission check.

				// Show delete button for users that have a lower or equal
				// role than current user.
				if (((User) rowObject).getRole().compareTo(LoginController.getCurrentUser().getRole()) <= 0)
					setGraphic(button);
			}
			else
			{
				setGraphic(button);
			}
		}
		else
			setGraphic(null);
	}
}
