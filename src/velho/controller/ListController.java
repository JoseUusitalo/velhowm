package velho.controller;

import java.util.Map;

import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.Node;
import velho.model.Product;
import velho.model.User;
import velho.view.ListView;

/**
 * A class for controlling the creation of lists.
 *
 * @author Jose Uusitalo
 */
public class ListController
{
	private UserController userController;

	public ListController(final UserController userController)
	{
		this.userController = userController;
	}

	/**
	 * Gets a new table view.
	 *
	 * @param columnMap a map of column values and names
	 * @param dataList the {@link ObservableList} of data to show
	 * @return a new table view
	 */
	public Node getUserListView(final Map<String, String> columnMap, final ObservableList<Object> dataList)
	{
		ListView list = new ListView(this, columnMap, dataList);
		return list.getUserTableView();
	}

	/**
	 * Gets a new table view.
	 *
	 * @param columnMap a map of column values and names
	 * @param dataMap the {@link ObservableMap} of data to show
	 * @return a new table view
	 */
	public Node getProductListView(final Map<String, String> columnMap, final ObservableList<Object> observableList)
	{
		ListView list = new ListView(this, columnMap, observableList);
		return list.getUserTableView();
	}

	/**
	 * Attemps to remove a user from the database.
	 *
	 * @param databaseID database ID of the user to remove
	 */
	public void removeUser(final User user)
	{
		userController.removeUser(user);
	}
}
