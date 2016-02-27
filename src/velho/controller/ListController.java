package velho.controller;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.Node;
import velho.model.User;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.model.interfaces.UIActionController;
import velho.view.ListView;
import velho.view.ProductListSearch;

/**
 * A class for controlling the creation of lists.
 *
 * @author Jose Uusitalo
 */
public class ListController
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(ListController.class.getName());

	/**
	 * The {@link UserController}.
	 */
	private UserController userController;

	/**
	 * @param userController
	 */
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
		final ListView list = new ListView(userController, columnMap, dataList);
		return list.getView();
	}

	/**
	 * Gets a new table view.
	 *
	 * @param columnMap a map of column values and names
	 * @param dataMap the {@link ObservableMap} of data to show
	 * @return a new table view
	 */
	@SuppressWarnings("static-method")
	public Node getProductListView(final Map<String, String> columnMap, final ObservableList<Object> observableList)
	{
		final ListView list = new ListView(null, columnMap, observableList);
		return list.getView();
	}

	/**
	 * Attemps to remove a user from the database.
	 *
	 * @param databaseID database ID of the user to remove
	 */
	public void removeUser(final User user)
	{
		userController.deleteUser(user);
	}

	/**
	 * Gets the view for searching for multiple products at once.
	 *
	 * @return product list search view
	 */
	public Node getProductListSearchView()
	{
		final ProductListSearch searchView = new ProductListSearch(this);
		final ListView listView = new ListView(null, DatabaseController.getProductSearchDataColumns(false, false),
				DatabaseController.getObservableProductSearchResults());

		return searchView.getView(listView.getView());
	}

	/**
	 * Gets a view for displaying tabular data with the specified columns and data.
	 *
	 * @param columnMap map of columns and their values
	 * @param data data to display
	 * @return a table view of the given data
	 */
	public static Node getTableView(final UIActionController parentController, final Map<String, String> columnMap, final ObservableList<Object> data)
	{
		final ListView listView = new ListView(parentController, columnMap, data);
		return listView.getView();
	}

	/**
	 * Gets a view for displaying tabular data with the specified columns and data.
	 *
	 * @param columnMap map of columns and their values
	 * @param data data to display
	 * @return a table view of the given data
	 */
	public static Node getTableView(final UIActionController parentController, final Map<String, String> columnMap, final ObservableSet<Object> data)
	{
		final ObservableList<Object> list = FXCollections.observableArrayList(Arrays.asList(data.toArray()));

		final ListView listView = new ListView(parentController, columnMap, list);
		return listView.getView();
	}

	public Node getProductSearchResultsView()
	{
		// TODO: Temporarily showing all products
		SYSLOG.trace("Getting search results for removal list.");
		return getProductListView(DatabaseController.getPublicProductDataColumns(true, false), DatabaseController.getObservableProducts());
	}

	/**
	 * Searches the database for the given products.
	 *
	 * @param products a string of product names or IDs (one per line)
	 * @return
	 */
	@SuppressWarnings("static-method")
	public Map<Integer, Integer> searchByProductList(final String products)
	{
		final String[] productStringLines = products.split("\n");
		final Map<Integer, Integer> productID_BoxSize = new LinkedHashMap<Integer, Integer>();
		Integer productID = -1;
		Object[] countName = null;

		// Convert lines to a map.
		for (final String line : productStringLines)
		{
			countName = parseProductLine(line);

			if (!((String) countName[1]).isEmpty())
			{
				// Convert all names to IDs for easier processing.
				try
				{
					productID = Integer.parseInt((String) countName[1]);
				}
				catch (final NumberFormatException e)
				{
					try
					{
						productID = DatabaseController.getProductIDFromName((String) countName[1]);
					}
					catch (final NoDatabaseLinkException e1)
					{
						DatabaseController.tryReLink();
					}
				}

				// If the product already exists, add the new count to the previous count.
				if (productID_BoxSize.containsKey(productID))
				{
					productID_BoxSize.put(productID, productID_BoxSize.get(productID) + ((int) countName[0]));
				}
				else
				{
					productID_BoxSize.put(productID, (int) countName[0]);
				}
			}
		}

		// Search the database for the products.
		try
		{
			DatabaseController.searchProductBoxByDataList(productID_BoxSize);
		}
		catch (final NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}

		// Return the data for unit testing.
		return productID_BoxSize;
	}

	/**
	 * Parses a line of product information in the format:
	 * <p>
	 * <code>&lt;an integer&gt; : &lt;product ID or name&gt;</code>
	 * </p>
	 * @param line String to parse
	 * @return an object array where the first element is the integer and the second element is the product name
	 */
	public static Object[] parseProductLine(final String line)
	{
		final Object[] countName = new Object[2];
		countName[0] = 1;

		// Count first, then product string.
		final String[] possibleProductAndCount = line.split(":");

		if (possibleProductAndCount.length == 1)
		{
			// Example: 'Product A'
			countName[1] = possibleProductAndCount[0].trim();
		}
		else if (possibleProductAndCount.length == 2)
		{
			// Example: '15: Product B'
			// Example: 'Product C: Long Name'
			try
			{
				countName[0] = Integer.valueOf(possibleProductAndCount[0].trim());
				countName[1] = possibleProductAndCount[1].trim();
			}
			catch (final NumberFormatException e)
			{
				// Count remains 1.
				countName[1] = (possibleProductAndCount[0] + ":" + possibleProductAndCount[1]).trim();
			}
		}
		else
		{
			// Example: '90: Product C: The Better Version'
			// Example: 'Product D: Cool 'n Stuff: Too Many Colons Version'
			int start = 0;
			try
			{
				countName[0] = Integer.valueOf(possibleProductAndCount[0].trim());
				start = 1;
			}
			catch (final NumberFormatException e)
			{
				// Count remains 1.
			}

			// Rebuild the product string.
			final StringBuffer sb = new StringBuffer();
			final int length = possibleProductAndCount.length;

			for (int i = start; i < length; i++)
			{
				if (start == i)
				{
					// Left trim spaces from the first element.
					// This assumes that no product name can begin with a space character.
					sb.append(possibleProductAndCount[i].replaceAll("^\\s+", ""));
				}
				else
					sb.append(possibleProductAndCount[i]);

				if (i < length - 1)
					sb.append(":");
			}
			countName[1] = sb.toString();
		}

		return countName;
	}

	/**
	 * Performs an add action for the given data depending on the type of the object.
	 *
	 * @param object data to process
	 */
	@SuppressWarnings("static-method")
	public void addData(final Object object)
	{
		SYSLOG.trace("OBJECT FROM ADD BUTTON: " + object);
	}
}
