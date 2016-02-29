package velho.controller;

import org.apache.log4j.Logger;

import javafx.scene.Node;
import velho.model.interfaces.UIActionController;
import velho.view.ProductDataView;

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
	 * The removal list browsing view.
	 */
	private Node browseView;

	/**
	 * The view in the tab itself.
	 */
	private ProductDataView tabView;

	public ProductController()
	{
		tabView = new ProductDataView();
	}

	public Node getView()
	{
		return tabView.getView();
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
}
