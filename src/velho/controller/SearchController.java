package velho.controller;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import velho.controller.database.DatabaseController;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.view.ProductListSearchView;
import velho.view.SearchTabView;
import velho.view.SearchView;

/**
 * A class handling searching the database for data.
 *
 * @author Jose Uusitalo &amp; Joona Silvennoinen
 */
public class SearchController
{
	/**
	 * The view in the tab itself.
	 */
	private final SearchTabView searchTabView;

	/**
	 */
	public SearchController()
	{
		this.searchTabView = new SearchTabView(this);
	}

	/**
	 * Searches the database with the given arguments.
	 * Setting any value to <code>null</code> will not include it in the search.
	 *
	 * @param limits internal string representing the search target
	 * @param name the name of the product
	 * @param productCount how many products of this type of find in the product boxes
	 * @param brand the {@link ProductBrand}
	 * @param category the {@link ProductCategory}
	 * @param expiresStart the expiration date range start
	 * @param expiresEnd the expiration date range end
	 */
	@SuppressWarnings("static-method")

	public void productBoxSearch(final boolean canBeInRemovalList, final String identifier, final Integer productCount, final Object brand,
			final Object category, final LocalDate expiresStart, final LocalDate expiresEnd)
	{
		try
		{
			DatabaseController.getInstance().searchProductBox(identifier, productCount.intValue(), (ProductBrand) brand, (ProductCategory) category,
					expiresStart, expiresEnd, canBeInRemovalList);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Gets the search view.
	 *
	 * @return the search view
	 */
	public Node getSearchView()
	{
		return new SearchView(this, true, DatabaseController.getInstance().getAllProductBrands(), DatabaseController.getInstance().getAllProductCategories())
				.getView();
	}

	/**
	 * Gets a specific type of search view.
	 *
	 * @param limits an internal string representing the type of search view to create
	 * @return the search view
	 */
	public Node getSearchView(final boolean canBeInRemovalList)
	{
		return new SearchView(this, canBeInRemovalList, DatabaseController.getInstance().getAllProductBrands(),
				DatabaseController.getInstance().getAllProductCategories()).getView();
	}

	/**
	 * Gets the view in the search tab.
	 *
	 * @return the tab view
	 */
	public Node getSearchTabView()
	{
		return searchTabView.getView();
	}

	/**
	 * Gets the view for displaying search results.
	 *
	 * @return the search results view
	 */
	@SuppressWarnings("static-method")
	public Node getResultsView()
	{
		//@formatter:off
		return ListController.getTableView(ProductController.getInstance(),
										   DatabaseController.getInstance().getProductSearchDataColumns(false, false),
										   DatabaseController.getInstance().getObservableProductSearchResults());
		//@formatter:on
	}

	/**
	 * Gets the view for searching for multiple products at once.
	 *
	 * @return product list search view
	 */
	public Node getProductListSearchView()
	{
		final ProductListSearchView searchView = new ProductListSearchView(this);
		//@formatter:off
		return searchView.getView((BorderPane) ListController.getTableView(ProductController.getInstance(),
										  								   DatabaseController.getInstance().getProductSearchDataColumns(false, false),
										  								   DatabaseController.getInstance().getObservableProductSearchResults()));
		//@formatter:on
	}

	/**
	 * Searches the database for the given products.
	 *
	 * @param products a string of product names or IDs (one per line)
	 * @return A map of product ID and box sizes processed from the given list of products. (Used only in unit testing.)
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
					productID = DatabaseController.getInstance().getProductByName((String) countName[1]).getDatabaseID();
				}

				// If the product already exists, add the new count to the
				// previous count.
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
		DatabaseController.getInstance().searchProductBoxByDataList(productID_BoxSize);

		// Return the data for unit testing.
		return productID_BoxSize;
	}

	/**
	 * Parses a line of product information in the format:
	 * <p>
	 * <code>&lt;an integer&gt; : &lt;product ID or name&gt;</code>
	 * </p>
	 *
	 * @param line String to parse
	 * @return an object array where the first element is the integer and the
	 * second element is the product name
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
			final StringBuilder strbuilder = new StringBuilder();
			final int length = possibleProductAndCount.length;

			for (int i = start; i < length; i++)
			{
				if (start == i)
				{
					// Left trim spaces from the first element.
					// This assumes that no product name can begin with a space
					// character.
					strbuilder.append(possibleProductAndCount[i].replaceAll("^\\s+", ""));
				}
				else
					strbuilder.append(possibleProductAndCount[i]);

				if (i < length - 1)
					strbuilder.append(":");
			}
			countName[1] = strbuilder.toString();
		}

		return countName;
	}
}
