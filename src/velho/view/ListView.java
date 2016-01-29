package velho.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import velho.controller.ListViewController;
import velho.controller.UIController;
import velho.controller.UserController;
import velho.model.Manager;
import velho.model.User;

public class ListView
{
	private ListViewController controller;

	private BorderPane pane;
	private ObservableList<User> datalist;
	private List<String> columnNames;

	public ListView(ListViewController listController, ObservableList list)
	{
		columnNames = new ArrayList<String>(Arrays.asList("First name", "Last name"));
		this.datalist = list;
		this.datalist = FXCollections.observableArrayList(new User(12, "Joona", "Silv", new Manager("Juu")), new User(50, "Joona2", "Silv2", new Manager("Juu2")));
		controller = listController;
		pane = null;
	}

	public BorderPane getListView()
	{
		if (pane == null)
		{
			pane = new BorderPane();

			List<TableColumn> cols = new ArrayList<TableColumn>();
			
			for(int i=0; i<columnNames.size(); i++)
			{
				TableColumn<User, String> col = new TableColumn<User, String>(columnNames.get(1));
				col.setCellValueFactory(
		                new PropertyValueFactory<User, String>(columnNames.get(1)));
				cols.add(col);
			}
			
			TableView tableView = new TableView();
			tableView.setItems(datalist);
			tableView.getColumns().addAll(cols);		
			pane.setCenter(tableView);
			
		}
		return pane;
	}

}