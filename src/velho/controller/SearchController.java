package velho.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.Node;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.model.enums.DatabaseTable;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.view.SearchTabView;
import velho.view.SearchView;

public class SearchController
{
	private SearchTabView searchTabView;

	private ListController listController;

	public SearchController(final ListController listController)
	{
		this.listController = listController;
		this.searchTabView = new SearchTabView(this);
	}

	public void productSearch(final String nameField, final Integer productCountField, final Integer popularityField, final Object productBrand, final Object productCategory, final LocalDate localDate, final LocalDate localDate2)
	{
		final List<String> where = new ArrayList<String>();

		if (nameField != null && !nameField.isEmpty())
		{
			System.out.println(nameField.isEmpty());
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
		System.out.println(where.toString());

		try
		{
			DatabaseController.searchProductBox(where);
		} catch (NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}
	}

	public void productSearch(final String limits, final String nameField, final Integer productCountField, final Integer popularityField,
			final Object productBrand, final Object productCategory, final LocalDate localDate, final LocalDate localDate2)
	{
		final List<String> where = new ArrayList<String>();

		if (nameField != null && !nameField.isEmpty())
		{
			System.out.println(nameField.isEmpty());
			where.add("products.name = '" + nameField + "'");
		}

		if (productCountField != null)
		{
			where.add("containers.product_count = " + productCountField.intValue());
		}

		if (popularityField != null)
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
			// where.add("containers.expiration_date >= " + localDate);
		}

		if (localDate2 != null)
		{
			// where.add("containers.expiration_date <= " + localDate2);
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
			System.out.println(limits + " Search: " + where.toString());
		}

		try
		{
			DatabaseController.searchProductBox(where, joins);
		}
		catch (NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}
	}

	public Node getSearchView()
	{
		try
		{
			return new SearchView(this, DatabaseController.getAllProductBrands(), DatabaseController.getAllProductCategories()).getView();
		} catch (NoDatabaseLinkException e)
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
		catch (NoDatabaseLinkException e)
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
		// TODO: Temporarily showing all products
		// FIXME: Product boxes!
		System.out.println("Getting search results for removal list.");
		return listController.getProductListView(DatabaseController.getProductSearchDataColumns(false, false), DatabaseController.getObservableProductSearchResults());

	}
}
