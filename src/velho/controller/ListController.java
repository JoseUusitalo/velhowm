package velho.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import velho.model.User;
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
	 * The {@link UserController}.
	 */
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

	/**
	 * Gets the view for searching for multiple products at once.
	 *
	 * @return product list search view
	 */
	public Node getProductListSearchView()
	{
		ProductListSearch searchView = new ProductListSearch(this);
		ListView listView = new ListView(this, DatabaseController.getProductSearchDataColumns(), null);

		return searchView.getProductListSearch(listView.getUserTableView());
	}

	/**
	 * Searches the database for the given products.
	 *
	 * @param products a string of product names or IDs (one per line)
	 * @return
	 */
	public Map<String, Integer> searchByProductList(final String products)
	{
		String[] productStringLines = products.split("\n");
		Map<String, Integer> productData = new LinkedHashMap<String, Integer>();

		// Convert lines to a map.
		for (final String line : productStringLines)
		{
			// Count first, then product.
			String[] possibleProductAndCount = line.split(":");
			int count = 1;
			String productString = null;

			if (possibleProductAndCount.length == 1)
			{
				// Example: 'Product A'
				productString = possibleProductAndCount[0].trim();
			}
			else if (possibleProductAndCount.length == 2)
			{
				// Example: '15: Product B'
				// Example: 'Product C: Long Name'
				try
				{
					count = Integer.valueOf(possibleProductAndCount[0]);
					productString = possibleProductAndCount[1].trim();
				}
				catch (NumberFormatException e)
				{
					// Count remains 1.
					productString = (possibleProductAndCount[0] + ":" + possibleProductAndCount[1]).trim();
				}
			}
			else
			{
				// Example: '90: Product C: The Better Version'
				// Example: 'Product D: Cool 'n Stuff: Too Many Colons Version'
				int start = 0;
				try
				{
					count = Integer.valueOf(possibleProductAndCount[0]);
					start = 1;
				}
				catch (NumberFormatException e)
				{
					// Count remains 1.
				}

				// Rebuild the product string.
				StringBuffer sb = new StringBuffer();
				int length = possibleProductAndCount.length;

				for (int i = start; i < length; i++)
				{
					if (start == 1 && i == 1)
					{
						// Left trim spaces because the first element was a number.
						sb.append(possibleProductAndCount[i].replaceAll("^\\s+", ""));
					}
					else
						sb.append(possibleProductAndCount[i]);

					if (i < length - 1)
						sb.append(":");
				}
				productString = sb.toString();
			}

			// If the product already exists, add the new count to the previous count.
			if (productData.containsKey(productString))
				productData.put(productString, productData.get(productString) + count);
			else
				productData.put(productString, count);
		}

		// Return the data for unit testing.
		return productData;
	}
}
