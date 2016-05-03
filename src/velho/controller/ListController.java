package velho.controller;

import java.util.Arrays;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.Node;
import velho.controller.interfaces.UIActionController;
import velho.view.ListView;

/**
 * A class for controlling the creation of generic lists.
 *
 * @author Jose Uusitalo
 */
public abstract class ListController
{
	/**
	 * Gets a view for displaying tabular data with the specified columns and data.
	 *
	 * @param columnMap map of column names and their values
	 * @param data data to display
	 * @return a table view of the given data
	 */
	public static Node getTableView(final UIActionController parentController, final Map<String, String> columnMap, final ObservableSet<Object> data)
	{
		return new ListView(parentController, columnMap, FXCollections.observableArrayList(Arrays.asList(data.toArray()))).getView();
	}

	/**
	 * Gets a view for displaying tabular data with the specified columns and list of data.
	 *
	 * @param columnMap map of column names and their values
	 * @param data data to display
	 * @return a table view of the given data
	 */
	public static Node getTableView(final UIActionController parentController, final Map<String, String> columnMap, final ObservableList<Object> data)
	{
		return new ListView(parentController, columnMap, data).getView();
	}
}
