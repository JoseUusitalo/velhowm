package velho.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import velho.model.User;

/**
 * @author Jose Uusitalo &amp; Joona
 */
public class ListView
{
	private BorderPane pane;
	private ObservableList<User> datalist;
	private Map<String, String> columnNames;

	public ListView(final Map<String, String> columnMap, final ObservableList<User> datalist)
	{
		columnNames = columnMap;
		this.datalist = datalist;
	}

	public BorderPane getListView()
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
				cols.add(col);
			}

			tableView.setItems(datalist);
			tableView.getColumns().addAll(cols);
			pane.setCenter(tableView);
		}
		return pane;
	}

}