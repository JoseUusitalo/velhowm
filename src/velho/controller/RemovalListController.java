package velho.controller;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import velho.model.RemovalList;
import velho.model.interfaces.UIActionController;
import velho.view.RemovalListCreationView;
import velho.view.RemovalListManagementView;

public class RemovalListController implements UIActionController
{
	/**
	 * The {@link ListController}.
	 */
	private ListController listController;

	/**
	 * The management view.
	 */
	private RemovalListManagementView managementView;

	/**
	 * Current new removal list.
	 */
	private RemovalList newRemovalList;

	private SearchController searchController;

	/**
	 * @throws NoDatabaseLinkException
	 */
	public RemovalListController(final ListController listController, final SearchController searchController)
	{
		this.listController = listController;
		this.searchController = searchController;
		managementView = new RemovalListManagementView(this, this.listController);
	}

	/**
	 * Gets the view for managing and viewing existing removal lists.
	 *
	 * @return the removal list management view
	 */
	public Node getRemovalListManagementView()
	{
		// Initially the browsing view is shown.
		if (managementView.getView().getCenter() == null)
			showBrowseRemovalListsView();
		return managementView.getView();
	}

	/**
	 * Gets the removal list creation view.
	 *
	 * @return view for creating new removal lists
	 */
	private Node getRemovalListCreationView()
	{
		managementView.setBrowseListsButtonVisiblity(true);
		RemovalListCreationView creationView = new RemovalListCreationView(this, listController, searchController);
		return creationView.getView();
	}

	/**
	 * Changes the removal list management view to the removal list creation view.
	 */
	public void showNewRemovalListView()
	{
		// Create a new removal list if it does not exist.
		if (newRemovalList == null)
			newRemovalList = new RemovalList();

		managementView.setContent(getRemovalListCreationView());
	}

	/**
	 * Changes the removal list management view to the removal list browsing view.
	 */
	public void showBrowseRemovalListsView()
	{
		managementView.setBrowseListsButtonVisiblity(false);
		managementView
				.setContent(ListController.getTableView(this, DatabaseController.getRemovalListDataColumns(), DatabaseController.getRemovalListsViewList()));
	}

	/**
	 * Gets a view showing the removal list currently being created.
	 *
	 * @return view for the current removal list being created
	 */
	public BorderPane getCurrentRemovalListView()
	{
		System.out.println("Showing current new removal list.");
		return (BorderPane) ListController.getTableView(this, DatabaseController.getPublicProductDataColumns(false, true), newRemovalList.getBoxes());
	}

	@Override
	public void createAction(final Object data)
	{
		System.out.println("Controller got from UI: " + data);
	}

	@Override
	public void updateAction(final Object data)
	{
		System.out.println("Controller got from UI: " + data);
	}

	@Override
	public void removeAction(final Object data)
	{
		System.out.println("Controller got from UI: " + data);
	}

	@Override
	public void deleteAction(final Object data)
	{
		System.out.println("Controller got from UI: " + data);
	}

	@Override
	public void addAction(final Object data)
	{
		System.out.println("Controller got from UI: " + data);
	}

	public BorderPane getSearchResults()
	{
		// FIXME: temporarily showing all products
		return (BorderPane) ListController.getTableView(this, DatabaseController.getPublicProductDataColumns(true, false),
				DatabaseController.getPublicProductDataList());
	}
}
