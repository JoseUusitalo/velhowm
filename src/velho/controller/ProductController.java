package velho.controller;

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
	 * The {@link AddProductView}.
	 */
	private AddProductView addProductView;

	/**
	 * The {@link UIController}.
	 */
	private static UIController uiController;

	public ProductController()
	{
		this.addProductView = new AddProductView(this);
	}

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

	public void saveProduct(final String name, final Object brand, final Object category, final int popularity)
	{
		ProductBrand bran = null;
		ProductCategory cat = null;
		if (brand instanceof String)
		{
			bran = new ProductBrand((String) brand);
		}
		if (category instanceof String)
		{
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
		}
		if (category instanceof ProductCategory)
		{
			cat = (ProductCategory) category;
		}

		Product newProduct = new Product(name, bran, cat, popularity);
		System.out.println(newProduct.toString());
	}

}
