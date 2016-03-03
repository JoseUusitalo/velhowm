package velho.controller;

import org.apache.log4j.Logger;

import javafx.scene.Node;
import velho.model.Product;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.view.AddProductView;

/**
 * Controls logging users in and out.
 *
 * @author Edward &amp; Jose Uusitalo
 */
public class ProductController
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
	 * Adds new view.
	 */
	public ProductController()
	{
		this.addProductView = new AddProductView(this);
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

}
