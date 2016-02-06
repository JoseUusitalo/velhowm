package velho.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
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
import velho.model.Product;
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
	 * The {@link ListController}.
	 */
	private ListController listController;

	/**
	 * The map of data to show.
	 */
	private ObservableMap<Integer, Product> dataMap;

	/**
	 * @param columnMap
	 * @param datalist
	 */
	public ListView(final ListController listController, final Map<String, String> columnMap, final ObservableList<Object> datalist)
	{
		this.listController = listController;
		columnNames = columnMap;
		this.datalist = datalist;
	}

	/**
	 * Gets the user table view.
	 *
	 * @return a table view of users
	 */
	public BorderPane getUserTableView()
	{
		if (pane == null)
		{
			pane = new BorderPane();

			TableView<Object> tableView = new TableView<Object>();

			List<TableColumn<Object, String>> cols = new ArrayList<TableColumn<Object, String>>();

			for (final String key : columnNames.keySet())
			{
				TableColumn<Object, String> col = new TableColumn<Object, String>(columnNames.get(key));
				col.setCellValueFactory(new PropertyValueFactory<Object, String>(key));
				col.setSortType(TableColumn.SortType.ASCENDING);

				// Hide database ID columns
				if (key.contains("databaseID"))
					col.setVisible(false);

				if (key.equals("deleteButton"))
				{
					col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object, String>, ObservableValue<String>>()
					{
						@Override
						public ObservableValue<String> call(TableColumn.CellDataFeatures<Object, String> p)
						{
							return new SimpleStringProperty(p.getValue(), key);
						}
					});

					// Adding the button to the cell
					col.setCellFactory(new Callback<TableColumn<Object, String>, TableCell<Object, String>>()
					{
						@Override
						public TableCell<Object, String> call(TableColumn<Object, String> p)
						{
							TableCellDeleteButton button = new TableCellDeleteButton("Delete");
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
	 * Gets the product table view.
	 *
	 * @return a table view of products
	 */
	public BorderPane getProductTableView()
	{
		if (pane == null)
		{
			pane = new BorderPane();

			TableView<Object> tableView = new TableView<Object>();

			List<TableColumn<Object, String>> cols = new ArrayList<TableColumn<Object, String>>();

			for (final String key : columnNames.keySet())
			{
				TableColumn<Object, String> col = new TableColumn<Object, String>(columnNames.get(key));
				col.setCellValueFactory(new PropertyValueFactory<Object, String>(key));
				col.setSortType(TableColumn.SortType.ASCENDING);

				// Hide database ID columns
				if (key.contains("databaseID"))
					col.setVisible(false);

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
	private class TableCellDeleteButton extends TableCell<Object, String>
	{
		/**
		 * The button itself.
		 */
		private Button button;

		private TableCellDeleteButton(final String text)
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
					listController.removeUser((User) TableCellDeleteButton.this.getTableView().getItems().get(TableCellDeleteButton.this.getIndex()));
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
				// Permission check.
				User rowUser = (User) TableCellDeleteButton.this.getTableView().getItems().get(TableCellDeleteButton.this.getIndex());

				if (rowUser.getRole().compareTo(LoginController.getCurrentUser().getRole()) <= 0)
					setGraphic(button);
			}
			else
				setGraphic(null);
		}
	}
}