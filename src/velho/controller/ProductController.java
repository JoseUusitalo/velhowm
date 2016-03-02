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
	private static AddProductView addProductView;

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

	public void saveProduct(final String name, final ProductBrand brand, final ProductCategory cat, final int popularity)
	{
		Product names = new Product(name, brand, cat, popularity);
		System.out.println(names.toString());
	}

}
