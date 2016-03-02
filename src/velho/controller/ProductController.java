package velho.controller;

import javafx.scene.Node;
import velho.model.Product;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.model.interfaces.UIActionController;
import velho.view.ProductDataView;

public class ProductController implements UIActionController
{
	/**
	 * The view in the tab itself.
	 */
	private ProductDataView productDataView;

	public ProductController()
	{
		productDataView = new ProductDataView(this);
	}

	public Node getView()
	{
		try
		{
			return productDataView.getView(DatabaseController.getProductByID(1));
		} catch (NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
			return null;
		}
	}

	@Override public void createAction(final Object data)
	{
		// SYSLOG.trace("Controller got from UI: " + data);
	}

	@Override public void updateAction(final Object data)
	{
		// SYSLOG.trace("Controller got from UI update: " + ((ProductBoxSearchResultRow) data).getBox());
	}

	@Override public void addAction(final Object data)
	{
		// TODO Auto-generated method stub

	}

	@Override public void removeAction(final Object data)
	{
		// TODO Auto-generated method stub

	}

	@Override public void deleteAction(final Object data)
	{
		// TODO Auto-generated method stub

	}

	@Override public void viewAction(final Object data)
	{
		// TODO Auto-generated method stub

	}

	public void editProduct(final Product product)
	{
		System.out.println("testi" + product);

	}
}
