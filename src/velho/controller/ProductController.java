package velho.controller;

import javafx.scene.Node;
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
		return addProductView.getProductView();
	}

}
