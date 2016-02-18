package velho.controller;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import velho.model.ProductBoxSearchResultRow;
import velho.model.RemovalList;
import velho.model.RemovalListState;
import velho.model.interfaces.UIActionController;
import velho.view.RemovalListCreationView;
import velho.view.RemovalListManagementView;

/**
 * A class for managing {@link RemovalList} objects.
 *
 * @author Jose Uusitalo
 */
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

	/**
	 * The {@link SearchController}.
	 */
	private SearchController searchController;

	private RemovalListCreationView creationView;

	/**
	 * @throws NoDatabaseLinkException
	 */
	public RemovalListController(final ListController listController, final SearchController searchController)
	{
		this.listController = listController;
		this.searchController = searchController;
		managementView = new RemovalListManagementView(this, this.listController);
		creationView = new RemovalListCreationView(this, listController, searchController);
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

		System.out.println("Removal list is currently: " + newRemovalList.getObservableBoxes());

		managementView.setContent(getRemovalListCreationView());
	}

	/**
	 * Changes the removal list management view to the removal list browsing view.
	 */
	public void showBrowseRemovalListsView()
	{
		managementView.setBrowseListsButtonVisiblity(false);
		managementView
				.setContent(ListController.getTableView(this, DatabaseController.getRemovalListDataColumns(), DatabaseController.getObservableRemovalLists()));
	}

	/**
	 * Gets a view showing the removal list currently being created.
	 *
	 * @return view for the current removal list being created
	 */
	public ObservableList<Object> getCurrentRemovalListContents()
	{
		return newRemovalList.getObservableBoxes();
	}

	@Override
	public void createAction(final Object data)
	{
		System.out.println("Controller got from UI: " + data);
	}

	@Override
	public void updateAction(final Object data)
	{
		System.out.println("Controller got from UI: " + ((ProductBoxSearchResultRow) data).getBox());
	}

	@Override
	public void removeAction(final Object data)
	{
		System.out.println("Removing from new removal list: " + ((ProductBoxSearchResultRow) data).getBox());
		if (!newRemovalList.removeProductBox(((ProductBoxSearchResultRow) data).getBox()))
			PopupController.error("Action failed.");
	}

	@Override
	public void deleteAction(final Object data)
	{
		System.out.println("Controller got from UI delete: " + ((ProductBoxSearchResultRow) data).getBox());
	}

	@Override
	public void addAction(final Object data)
	{
		System.out.println("Adding to new removal list: " + ((ProductBoxSearchResultRow) data).getBox());
		if (!newRemovalList.addProductBox(((ProductBoxSearchResultRow) data).getBox()))
			PopupController.error("Action failed.");
	}

	public BorderPane getSearchResults()
	{
		System.out.println("Getting search result list: " + DatabaseController.getObservableProductSearchResults());
		return (BorderPane) ListController.getTableView(this, DatabaseController.getProductSearchDataColumns(true, false),
				DatabaseController.getObservableProductSearchResults());
	}

	public void saveNewRemovalList()
	{
		System.out.println("Saving List : " + newRemovalList.getObservableBoxes());

	}

	public void setNewRemovalListState(final RemovalListState newState)
	{
		System.out.println("New state is : " + newState);
	}
}
