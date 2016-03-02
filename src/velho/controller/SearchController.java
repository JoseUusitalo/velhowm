package velho.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import javafx.scene.Node;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.model.enums.DatabaseTable;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.view.SearchTabView;
import velho.view.SearchView;

/**
 * A class handling searching the database for data.
 *
 * @author Jose Uusitalo &amp; Joona
 */
public class SearchController
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(SearchController.class.getName());

	private SearchTabView searchTabView;

	private ListController listController;

	public SearchController(final ListController listController)
	{
		this.listController = listController;
		this.searchTabView = new SearchTabView(this);
	}

	@SuppressWarnings("static-method")
	public void productSearch(final String limits, final String nameField, final Integer productCountField, final Integer popularityField,
			final Object productBrand, final Object productCategory, final LocalDate localDate, final LocalDate localDate2)
	{
		final List<String> where = new ArrayList<String>();

		if (nameField != null && !nameField.isEmpty())
		{
			where.add("products.name = '" + nameField + "'");
		}

		if (productCountField != null && productCountField >= 0)
		{
			where.add("containers.product_count = " + productCountField.intValue());
		}

		if (popularityField != null && popularityField >= 0)
		{
			where.add("products.popularity = " + popularityField.intValue());
		}

		if (productBrand != null)
		{
			where.add("products.brand = " + ((ProductBrand) productBrand).getDatabaseID());
		}

		if (productCategory != null)
		{
			where.add("products.category = " + ((ProductCategory) productCategory).getDatabaseID());
		}

		if (localDate != null)
		{
			where.add("containers.expiration_date >= '" + localDate + "'");
		}

		if (localDate2 != null)
		{
			where.add("containers.expiration_date <= '" + localDate2 + "'");
		}

		final Map<DatabaseTable, String> joins = new LinkedHashMap<DatabaseTable, String>();

		if (limits != null)
		{
			switch (limits)
			{
				case "removal-list":
					joins.put(DatabaseTable.REMOVALLIST_PRODUCTBOXES, "containers.container_id = removallist_productboxes.productbox");

					// Only finds products that are not already on a removal list.
					where.add("removallist_productboxes.removallist IS NULL");
					break;
				default:
					break;
			}
			SYSLOG.debug(limits + " Conditions: " + where.toString());
		}
		else
		{
			SYSLOG.debug("Conditions: " + where.toString());
		}

		try
		{
			DatabaseController.searchProductBox(where, joins);
		}
		catch (final NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}
	}

	public Node getSearchView()
	{
		try
		{
			return new SearchView(this, DatabaseController.getAllProductBrands(), DatabaseController.getAllProductCategories()).getView();
		}
		catch (final NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
			return null;
		}
	}

	public Node getSearchView(final String limits)
	{
		try
		{
			return new SearchView(this, limits, DatabaseController.getAllProductBrands(), DatabaseController.getAllProductCategories()).getView();
		}
		catch (final NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
			return null;
		}
	}

	public Node getSearchTabView()
	{
		return searchTabView.getView();
	}

	public Node getResultsView()
	{
		SYSLOG.trace("Getting search results for removal list.");
		return listController.getProductListView(DatabaseController.getProductSearchDataColumns(false, false),
				DatabaseController.getObservableProductSearchResults());
	}
}
