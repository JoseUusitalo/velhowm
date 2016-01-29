package velho.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import velho.controller.ListViewController;
import velho.model.Manager;
import velho.model.User;

public class ListView
{
	private ListViewController listController;
	private BorderPane pane;
	private ObservableList<User> datalist;
	private List<String> columnNames;

	public ListView(ListViewController listController, ObservableList<User> list)
	{
		columnNames = new ArrayList<String>(Arrays.asList("First name", "Last name"));
		this.datalist = list;
		// TODO: Dev code.
		this.datalist = FXCollections.observableArrayList(new User(12, "Joona", "Silv", new Manager()), new User(50, "Joona2", "Silv2", new Manager()));
		this.listController = listController;
		pane = null;
	}

	public BorderPane getListView()
	{
		if (pane == null)
		{
			pane = new BorderPane();

			List<TableColumn<User, String>> cols = new ArrayList<TableColumn<User, String>>();

			for (int i = 0; i < columnNames.size(); i++)
			{
				TableColumn<User, String> col = new TableColumn<User, String>(columnNames.get(i));
				col.setCellValueFactory(new PropertyValueFactory<User, String>(columnNames.get(i)));
				cols.add(col);
			}

			TableView<User> tableView = new TableView<User>();
			tableView.setItems(datalist);
			tableView.getColumns().addAll(cols);
			pane.setCenter(tableView);

		}
		return pane;
	}

}