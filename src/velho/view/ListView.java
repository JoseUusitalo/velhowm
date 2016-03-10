package velho.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.util.Callback;
import velho.controller.LoginController;
import velho.controller.interfaces.UIActionController;
import velho.model.User;

/**
 * A class for creating lists and tables of data.
 *
 * @author Jose Uusitalo &amp; Joona
 */
public class ListView
{
	/**
	 * The root border pane.
	 */
	private BorderPane pane;

	/**
	 * The list of data to show.
	 */
	private ObservableList<Object> datalist;

	/**
	 * Data columns.
	 */
	private Map<String, String> columnNames;

	/**
	 * Parent controller.
	 */
	private UIActionController parentController;

	/**
	 * @param columnMap
	 * @param datalist
	 */
	public ListView(final UIActionController parentController, final Map<String, String> columnMap, final ObservableList<Object> datalist)
	{
		this.parentController = parentController;
		columnNames = columnMap;
		this.datalist = datalist;
	}

	/**
	 * Gets the table view of data given to this ListView.
	 *
	 * @return a table view of data
	 */
	public BorderPane getView()
	{
		if (pane == null)
		{
			pane = new BorderPane();

			final TableView<Object> tableView = new TableView<Object>();

			final List<TableColumn<Object, String>> cols = new ArrayList<TableColumn<Object, String>>();

			for (final String key : columnNames.keySet())
			{
				final TableColumn<Object, String> col = new TableColumn<Object, String>(columnNames.get(key));
				col.setCellValueFactory(new PropertyValueFactory<Object, String>(key));
				col.setSortType(TableColumn.SortType.ASCENDING);

				// Handle custom delete button list cell.
				if (key.equals("deleteButton"))
				{
					col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object, String>, ObservableValue<String>>()
					{
						@Override
						public ObservableValue<String> call(final TableColumn.CellDataFeatures<Object, String> p)
						{
							return new SimpleStringProperty(p.getValue(), key);
						}
					});

					// Adding the button to the cell
					col.setCellFactory(new Callback<TableColumn<Object, String>, TableCell<Object, String>>()
					{
						@Override
						public TableCell<Object, String> call(final TableColumn<Object, String> p)
						{
							final TableCellDeleteButton button = new TableCellDeleteButton(parentController, "Delete");
							button.setAlignment(Pos.CENTER);
							return button;
						}
					});
				}

				// Handle custom add button list cell.
				if (key.equals("addButton"))
				{
					col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object, String>, ObservableValue<String>>()
					{
						@Override
						public ObservableValue<String> call(final TableColumn.CellDataFeatures<Object, String> p)
						{
							return new SimpleStringProperty(p.getValue(), key);
						}
					});

					// Adding the button to the cell
					col.setCellFactory(new Callback<TableColumn<Object, String>, TableCell<Object, String>>()
					{
						@Override
						public TableCell<Object, String> call(final TableColumn<Object, String> p)
						{
							final TableCellAddButton button = new TableCellAddButton(parentController, "+");
							button.setAlignment(Pos.CENTER);
							return button;
						}
					});
				}

				// Handle custom remove button list cell.
				if (key.equals("removeButton"))
				{
					col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object, String>, ObservableValue<String>>()
					{
						@Override
						public ObservableValue<String> call(final TableColumn.CellDataFeatures<Object, String> p)
						{
							return new SimpleStringProperty(p.getValue(), key);
						}
					});

					// Adding the button to the cell
					col.setCellFactory(new Callback<TableColumn<Object, String>, TableCell<Object, String>>()
					{
						@Override
						public TableCell<Object, String> call(final TableColumn<Object, String> p)
						{
							final TableCellRemoveButton button = new TableCellRemoveButton(parentController, "-");
							button.setAlignment(Pos.CENTER);
							return button;
						}
					});
				}

				// Handle custom view button table cell.
				if (key.equals("viewButton"))
				{
					col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object, String>, ObservableValue<String>>()
					{
						@Override
						public ObservableValue<String> call(final TableColumn.CellDataFeatures<Object, String> p)
						{
							return new SimpleStringProperty(p.getValue(), key);
						}
					});

					// Adding the button to the cell
					col.setCellFactory(new Callback<TableColumn<Object, String>, TableCell<Object, String>>()
					{

						@Override
						public TableCell<Object, String> call(final TableColumn<Object, String> p)
						{
							final TableCellViewButton button = new TableCellViewButton(parentController, "View");
							button.setAlignment(Pos.CENTER);
							return button;
						}
					});
				}

				cols.add(col);
			}

			tableView.setItems(datalist);
			tableView.getColumns().addAll(cols);
			pane.setCenter(tableView);
		}
		return pane;
	}

	/**
	 * A table cell with a built-in delete button.
	 *
	 * @author Jose Uusitalo
	 */
	private class TableCellDeleteButton extends TableCell<Object, String>
	{
		/**
		 * The button itself.
		 */
		private Button button;

		/**
		 * The controller to send information to when this button is pressed.
		 */
		private UIActionController controller;

		/**
		 * @param text
		 *            button text
		 */
		private TableCellDeleteButton(final UIActionController parentController, final String text)
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
				public void handle(final ActionEvent t)
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
				final Object rowObject = TableCellDeleteButton.this.getTableView().getItems().get(TableCellDeleteButton.this.getIndex());

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

	/**
	 * A table cell with a built-in add button.
	 *
	 * @author Jose Uusitalo
	 */
	private class TableCellAddButton extends TableCell<Object, String>
	{
		/**
		 * The button itself.
		 */
		private Button button;

		/**
		 * The controller to send information to when this button is pressed.
		 */
		protected UIActionController controller;

		/**
		 * @param text
		 *            button text
		 */
		private TableCellAddButton(final UIActionController parentController, final String text)
		{
			this.controller = parentController;
			button = new Button(text);
			button.setFont(new Font(12));

			button.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent t)
				{
					// Send information to parent controller.
					controller.addAction(TableCellAddButton.this.getTableView().getItems().get(TableCellAddButton.this.getIndex()));
				}
			});
		}

		@Override
		protected void updateItem(final String string, final boolean empty)
		{
			super.updateItem(string, empty);

			// Display button only if the row is not empty.
			if (!empty)
				setGraphic(button);
			else
				setGraphic(null);
		}
	}

	/**
	 * A table cell with a built-in remove button.
	 *
	 * @author Jose Uusitalo
	 */
	private class TableCellRemoveButton extends TableCell<Object, String>
	{
		/**
		 * The button itself.
		 */
		private Button button;

		/**
		 * The controller to send information to when this button is pressed.
		 */
		protected UIActionController controller;

		/**
		 * @param parentController controller to call the method from
		 * @param text button text
		 */
		private TableCellRemoveButton(final UIActionController parentController, final String text)
		{
			this.controller = parentController;
			button = new Button(text);
			button.setFont(new Font(12));

			button.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent t)
				{
					// Send information to parent controller.
					controller.removeAction(TableCellRemoveButton.this.getTableView().getItems().get(TableCellRemoveButton.this.getIndex()));
				}
			});
		}

		@Override
		protected void updateItem(final String string, final boolean empty)
		{
			super.updateItem(string, empty);

			// Display button only if the row is not empty.
			if (!empty)
				setGraphic(button);
			else
				setGraphic(null);
		}
	}

	/**
	 * A table cell with a built-in add button.
	 *
	 * @author Jose Uusitalo
	 */
	private class TableCellViewButton extends TableCell<Object, String>
	{
		/**
		 * The button itself.
		 */
		private Button button;

		/**
		 * The controller to send information to when this button is pressed.
		 */
		protected UIActionController controller;

		/**
		 * @param parentController controller to call the method from
		 * @param text button text
		 */
		private TableCellViewButton(final UIActionController parentController, final String text)
		{
			this.controller = parentController;
			button = new Button(text);
			button.setFont(new Font(12));

			button.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent t)
				{
					// Send information to parent controller.
					controller.viewAction(TableCellViewButton.this.getTableView().getItems().get(TableCellViewButton.this.getIndex()));
				}
			});
		}

		@Override
		protected void updateItem(final String string, final boolean empty)
		{
			super.updateItem(string, empty);

			// Display button only if the row is not empty.
			if (!empty)
				setGraphic(button);
			else
				setGraphic(null);
		}
	}
}