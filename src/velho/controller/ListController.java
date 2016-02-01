package velho.controller;

import java.util.Map;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import velho.model.User;
import velho.view.ListView;

/**
 * A class for controlling the creation of lists.
 * 
 * @author Jose Uusitalo
 */
public class ListController
{
	/**
	 * Gets a new table view.
	 * 
	 * @param columnMap a map of column values and names
	 * @param datalist the {@link ObservableList} of data to show
	 * @return a new table view
	 */
	public static Node getView(final Map<String, String> columnMap, final ObservableList<User> datalist)
	{
		ListView list = new ListView(columnMap, datalist);
		return list.getTableView();
	}
}
