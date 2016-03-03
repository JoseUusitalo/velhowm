package velho.controller;

import org.apache.log4j.Logger;

import javafx.scene.Node;
import velho.model.Product;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.model.interfaces.UIActionController;
import velho.view.AddProductView;
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
	 * The {@link UIController}.
	 */
	private static UIController uiController;

	/**
	 * The view in the tab itself.
	 */
	private ProductDataView productDataView;

	/**
	 * Adds new view.
	 */
	public ProductController()
	{
		this.addProductView = new AddProductView(this);
		productDataView = new ProductDataView(this);
	}

	public Node getView()
	{
		try
		{
			return productDataView.getView(DatabaseController.getProductByID(1));
		}
		catch (NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
			return null;
		}
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
			return addProductView.getProductView();
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
	public void saveProduct(final String name, final Object brand, final Object category, final int popularity)
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

		Product newProduct = new Product(name, bran, cat, popularity);
		System.out.println(newProduct.toString());
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
		// TODO Auto-generated method stub

	}

	public void editProduct(final Product product)
	{
		System.out.println("testi" + product);

	}
}
