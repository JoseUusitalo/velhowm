package velho.controller;

import org.apache.log4j.Logger;

import javafx.scene.Node;
import velho.controller.interfaces.UIActionController;
import velho.model.Product;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.view.AddProductView;
import velho.view.GenericTabView;
import velho.view.ProductDataView;

/**
 * Controller for handling {@link Product} objects
 *
 * @author Jose Uusitalo &amp; Edward Puustinen
 */
public class ProductController implements UIActionController
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(ProductController.class.getName());

	/**
	 * Apache log4j logger: User.
	 */
	private static final Logger USRLOG = Logger.getLogger("userLogger");

	/**
	 * The {@link AddProductView}.
	 */
	private AddProductView addProductView;

	/**
	 * The tab view for the product list view.
	 */
	private GenericTabView listTab;

	/**
	 * The {@link UIController}.
	 */
	private UIController uiController;

	/**
	 * The tab view for creating new objects and saving them to the list view.
	 */
	private GenericTabView addTab;

	/**
	 * @param uiController
	 */
	public ProductController(final UIController uiController)
	{
		this.uiController = uiController;
		this.addProductView = new AddProductView(this, uiController);
		listTab = new GenericTabView();
		addTab = new GenericTabView();
		showList();
	}

	/**
	 * Gets the view in the product list tab.
	 *
	 * @return the product list tab view
	 */
	public Node getTabView()
	{
		return listTab.getView();
	}

	/**
	 * Gets the product editing view.
	 *
	 * @return the product editing view
	 */
	public Node getProductEditView()
	{
		try
		{
			return addProductView.getView(true);
		}
		catch (NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}
		return null;
	}

	/**
	 * Saves the new or existing product to database and returns the updated object.
	 *
	 * @param databaseID database ID of the product (<code>-1</code> for a new one)
	 * @param name name of the of product
	 * @param brand brand of the product
	 * @param category category of the product
	 * @param popularity popularity of the product
	 */
	@SuppressWarnings("static-method")
	public Product saveProduct(final int databaseID, final String name, final Object brand, final Object category, final int popularity)
	{
		ProductBrand bran = null;
		ProductCategory cat = null;
		try
		{
			if (brand instanceof String)
			{
				SYSLOG.trace("creating new brand from " + brand.toString());

				bran = DatabaseController.getProductBrandByID(DatabaseController.save(new ProductBrand((String) brand)), true);
			}

			if (category instanceof String)
			{
				SYSLOG.trace("creating new category from " + category.toString());
				try
				{
					cat = DatabaseController.getProductCategoryByID(DatabaseController.save(new ProductCategory((String) category)), true);
				}
				catch (NoDatabaseLinkException e)
				{
					DatabaseController.tryReLink();
				}
			}
		}
		catch (NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}

		if (brand instanceof ProductBrand)
		{
			bran = (ProductBrand) brand;
			SYSLOG.trace("new brand is " + bran.toString());
		}

		if (category instanceof ProductCategory)
		{
			cat = (ProductCategory) category;
			SYSLOG.trace("new product category is " + cat.toString());
		}

		Product newProduct = new Product(databaseID, name, bran, cat, popularity);
		System.out.println(newProduct.toString());

		try
		{
			final int dbID = DatabaseController.save(newProduct);

			if (dbID < 0)
			{
				PopupController.error("Failed to save product data!");
				return null;
			}

			return DatabaseController.getProductByID(dbID, true);
		}
		catch (NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}

		return null;
	}

	/**
	 * Changes the view in the product list tab to the edit view.
	 *
	 * @param product {@link Product} to edit
	 */
	public void editProduct(final Product product)
	{
		try
		{
			listTab.setView(addProductView.getView(true));
			addProductView.setViewData(product);
		}
		catch (NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}
	}

	/**
	 * Gets the view for creating new products.
	 *
	 * @return the product creation view
	 */
	public Node getAddProductView()
	{
		try
		{
			return new AddProductView(this, uiController).getView(false);
		}
		catch (NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
			return null;
		}
	}

	/**
	 * Changes the view in the product list tab to the list.
	 */
	public void showList()
	{
		try
		{
			listTab.setView(ListController.getTableView(this, DatabaseController.getProductDataColumns(false, false), DatabaseController.getAllProducts()));
		}
		catch (NoDatabaseLinkException e)
		{
			listTab.setView(null);
			DatabaseController.tryReLink();
		}
	}

	/**
	 * Changes the view in the product add/edit list tab to the list.
	 */
	public void showCreatingListView()
	{
		try
		{
			addTab.setView(ListController.getTableView(this, DatabaseController.getProductDataColumns(false, false), DatabaseController.getAllProducts()));
		}
		catch (NoDatabaseLinkException e)
		{
			addTab.setView(null);
			DatabaseController.tryReLink();
		}
	}

	/**
	 * Changes the view in the product list tab to display the data from the given product.
	 *
	 * @param product product to display
	 */
	public void showProductView(final Product product)
	{
		listTab.setView(new ProductDataView(this).getView(product));
	}

	/**
	 * Gets a list of product search results.
	 *
	 * @return a view with the search results
	 */
	public Node getProductSearchResultsView()
	{
		SYSLOG.trace("Getting search results for product list.");
		return ListController.getTableView(this, DatabaseController.getProductDataColumns(true, false), DatabaseController.getObservableProductSearchResults());
	}

	@Override
	public void createAction(final Object data)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateAction(final Object data)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void addAction(final Object data)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeAction(final Object data)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAction(final Object data)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void viewAction(final Object data)
	{
		USRLOG.info("Viewing: " + ((Product) data).toString());
		listTab.setView(new ProductDataView(this).getView(((Product) data)));
	}

	public Node getAddStuffView()
	{
		return getAddProductView();
	}
}
