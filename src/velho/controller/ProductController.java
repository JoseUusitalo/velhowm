package velho.controller;

import javafx.scene.Node;
import velho.view.ProductEditView;

/**
 * Controls logging users in and out.
 *
 * @author Edward &amp; Jose Uusitalo
 */
public class ProductController
{

	/**
	 * The {@link ProductEditView}.
	 */
	private static ProductEditView productEditView;

	/**
	 * The {@link UIController}.
	 */
	private static UIController uiController;

	public ProductController()
	{
		this.productEditView = new ProductEditView(this);
	}

	public Node getProductEditView()
	{
		return productEditView.getProductView();
	}

}
