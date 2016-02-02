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
	private UserController userController;

	public ListController(final UserController userController)
	{
		this.userController = userController;
	}

	/**
	 * Gets a new table view.
	 *
	 * @param columnMap a map of column values and names
	 * @param datalist the {@link ObservableList} of data to show
	 * @return a new table view
	 */
	public Node getView(final Map<String, String> columnMap, final ObservableList<User> datalist)
	{
		ListView list = new ListView(this, columnMap, datalist);
		return list.getTableView();
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
