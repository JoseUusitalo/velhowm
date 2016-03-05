package velho.controller;

import org.apache.log4j.Logger;

import javafx.scene.Node;
import velho.model.Product;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.model.interfaces.UIActionController;
import velho.view.AddProductView;
import velho.view.GenericTabView;
import velho.view.ProductDataView;

/**
 * Controls logging users in and out.
 *
 * @author Edward &amp; Jose Uusitalo
 */
public class ProductController implements UIActionController
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(ProductController.class.getName());
	/**
	 * The {@link AddProductView}.
	 */
	private AddProductView addProductView;

	/**
	 * The view in the tab itself.
	 */
	private GenericTabView listTab;

	/**
	 * The {@link UIController}.
	 */
	private UIController uiController;

	/**
	 * @see #setControllers(UIController)
	 */
	public ProductController(final UIController uiController)
	{
		this.uiController = uiController;
		this.addProductView = new AddProductView(this, uiController);
		listTab = new GenericTabView();
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
	 * Edits the view.
	 *
	 * @return null
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
	 * Saves the new product gets it from database.
	 *
	 * @param name of product
	 * @param brand of product
	 * @param category of product
	 * @param popularity of product
	 */
	@SuppressWarnings("static-method")
	public Product saveProduct(final int databaseID, final String name, final Object brand, final Object category, final int popularity)
	{
		ProductBrand bran = null;
		ProductCategory cat = null;

		if (brand instanceof String)
		{
			SYSLOG.trace("creating new brand from " + brand.toString());
			bran = new ProductBrand((String) brand);
		}
		if (category instanceof String)
		{
			SYSLOG.trace("creating new category from " + category.toString());
			try
			{
				cat = new ProductCategory((String) category);
			}
			catch (NoDatabaseLinkException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
	 * Changes the view in the product list tab to display the data from the given product.
	 * @param product
	 */
	public void showProductView(final Product product)
	{
		listTab.setView(new ProductDataView(this).getView(product));
	}

	@Override
	public void createAction(final Object data)
	{
		// SYSLOG.trace("Controller got from UI: " + data);
	}

	@Override
	public void updateAction(final Object data)
	{
		// SYSLOG.trace("Controller got from UI update: " +
		// ((ProductBoxSearchResultRow) data).getBox());
	}

	@Override
	public void addAction(final Object data)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAction(final Object data)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAction(final Object data)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void viewAction(final Object data)
	{
		listTab.setView(new ProductDataView(this).getView(((Product) data)));
	}
}
