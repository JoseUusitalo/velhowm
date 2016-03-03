package velho.controller;

import javafx.scene.Node;
import velho.model.Product;
import velho.model.interfaces.UIActionController;
import velho.view.GenericTabView;
import velho.view.ProductDataView;

public class ProductController implements UIActionController
{
	/**
	 * The view in the tab itself.
	 */
	private GenericTabView productManagementView;

	public ProductController()
	{
		productManagementView = new GenericTabView();
		showList();
	}

	public Node getTabView()
	{

		return productManagementView.getView();
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
		productManagementView.setView(new ProductDataView(this).getView(((Product) data)));
		System.out.println(((Product) data).toString());

	}

	public void editProduct(final Product product)
	{
		System.out.println("testi" + product);

	}

	public void showList()
	{
		productManagementView.setView(ListController.getTableView(this, DatabaseController.getProductDataColumns(false, false), DatabaseController.getObservableProducts()));
	}
}
