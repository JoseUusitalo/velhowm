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
import velho.controller.ListController;
import velho.controller.LoginController;
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
	private ObservableList<User> datalist;

	/**
	 * Data columns.
	 */
	private Map<String, String> columnNames;

	/**
	 * The {@link ListController}.
	 */
	private ListController listController;

	/**
	 * @param columnMap
	 * @param datalist
	 */
	public ListView(final ListController listController, final Map<String, String> columnMap, final ObservableList<User> datalist)
	{
		this.listController = listController;
		columnNames = columnMap;
		this.datalist = datalist;
	}

	/**
	 * Gets the table view.
	 *
	 * @return a table view of data
	 */
	public BorderPane getTableView()
	{
		if (pane == null)
		{
			pane = new BorderPane();

			TableView<User> tableView = new TableView<User>();

			List<TableColumn<User, String>> cols = new ArrayList<TableColumn<User, String>>();

			for (final String key : columnNames.keySet())
			{
				TableColumn<User, String> col = new TableColumn<User, String>(columnNames.get(key));
				col.setCellValueFactory(new PropertyValueFactory<User, String>(key));
				col.setSortType(TableColumn.SortType.ASCENDING);

				// Hide database ID columns
				if (key.contains("databaseID"))
					col.setVisible(false);

				if (key.equals("deleteButton"))
				{
					col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<User, String>, ObservableValue<String>>()
					{
						@Override
						public ObservableValue<String> call(TableColumn.CellDataFeatures<User, String> p)
						{
							return new SimpleStringProperty(p.getValue(), key);
						}
					});

					// Adding the button to the cell
					col.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>()
					{
						@Override
						public TableCell<User, String> call(TableColumn<User, String> p)
						{
							TableCellButton button = new TableCellButton("Delete");
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
	 * A table cell with built-in a button.
	 *
	 * @author Jose Uusitalo
	 */
	private class TableCellButton extends TableCell<User, String>
	{
		/**
		 * The button itself.
		 */
		private Button button;

		private TableCellButton(final String text)
		{
			button = new Button(text);
			button.setFont(new Font(12));

			// When the button is pressed
			button.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(ActionEvent t)
				{
					// Get selected User object and Send information to list controller.
					listController.removeUser(TableCellButton.this.getTableView().getItems().get(TableCellButton.this.getIndex()));
				}
			});
		}

		// Display button only if the row is not empty.
		@Override
		protected void updateItem(String string, boolean empty)
		{
			super.updateItem(string, empty);
			if (!empty)
			{
				User rowUser = TableCellButton.this.getTableView().getItems().get(TableCellButton.this.getIndex());

				System.out.println(rowUser.getRole() + " " + LoginController.getCurrentUser().getRole().compareTo(rowUser.getRole()) + " "
						+ LoginController.getCurrentUser().getRole());
				if (LoginController.getCurrentUser().getRole().compareTo(rowUser.getRole()) <= 0)
					setGraphic(button);
			}
			else
				setGraphic(null);
		}
	}
}