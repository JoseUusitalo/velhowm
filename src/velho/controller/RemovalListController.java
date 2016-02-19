package velho.controller;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import velho.model.Manager;
import velho.model.ProductBoxSearchResultRow;
import velho.model.RemovalList;
import velho.model.RemovalListState;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.model.interfaces.UIActionController;
import velho.view.RemovalListCreationView;
import velho.view.RemovalListManagementView;
import velho.view.RemovalListTabView;
import velho.view.ViewRemovalListView;

/**
 * A class for managing {@link RemovalList} objects.
 *
 * @author Jose Uusitalo
 */
public class RemovalListController implements UIActionController
{
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

	/**
	 * The creation view for removal lists.
	 */
	private RemovalListCreationView creationView;

	/**
	 * The removal list browsing view.
	 */
	private Node browseView;

	/**
	 * The view in the tab itself.
	 */
	private RemovalListTabView tabView;

	/**
	 * @param listController
	 * @param searchController
	 */
	public RemovalListController(final SearchController searchController)
	{
		this.searchController = searchController;
		tabView = new RemovalListTabView();
	}

	@Override
	public void createAction(final Object data)
	{
		// System.out.println("Controller got from UI: " + data);
	}

	@Override
	public void updateAction(final Object data)
	{
		// System.out.println("Controller got from UI update: " + ((ProductBoxSearchResultRow) data).getBox());
	}

	@Override
	public void removeAction(final Object data)
	{
		System.out.println("Removing from new removal list: " + ((ProductBoxSearchResultRow) data).getBox());
		if (!newRemovalList.removeProductBox(((ProductBoxSearchResultRow) data).getBox()))
			PopupController.error("Failed to remove product box from removal list.");
	}

	@Override
	public void deleteAction(final Object data)
	{
		try
		{
			System.out.println("Deleting removal list " + data);
			if (!DatabaseController.deleteRemovalListByID(((RemovalList) data).getDatabaseID()))
				PopupController.error("Deleting removal list failed.");
		}
		catch (final NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}
	}

	@Override
	public void addAction(final Object data)
	{
		System.out.println("Adding to new removal list: " + ((ProductBoxSearchResultRow) data).getBox());
		if (!newRemovalList.addProductBox(((ProductBoxSearchResultRow) data).getBox()))
			PopupController.error("That product box is already on the removal list.");
	}

	@Override
	public void viewAction(final Object data)
	{
		System.out.println("Viewing removal list " + data);

		if (LoginController.userRoleIsGreaterOrEqualTo(new Manager()))
			managementView.setContent(new ViewRemovalListView((RemovalList) data, this).getView());
		else
			tabView.setView(new ViewRemovalListView((RemovalList) data, this).getView());
	}

	/**
	 * Gets the view for managing and viewing existing removal lists.
	 *
	 * @return the removal list management view
	 */
	public Node getView()
	{
		// Create the view.
		tabView.getView();

		// Managers and greater see the management view.
		if (LoginController.userRoleIsGreaterOrEqualTo(new Manager()))
		{
			managementView = new RemovalListManagementView(this);
			creationView = new RemovalListCreationView(this, this.searchController);

			// Initially the browsing view is shown.
			if (managementView.getView().getCenter() == null)
				showBrowseRemovalListsView();

			tabView.setView(managementView.getView());
		}
		else
		{
			// Others see just the browsing view.
			try
			{
				if (browseView == null)
					browseView = ListController.getTableView(this, DatabaseController.getRemovalListDataColumns(),
							DatabaseController.getObservableRemovalLists());
			}
			catch (final NoDatabaseLinkException e)
			{
				DatabaseController.tryReLink();
			}
			tabView.setView(browseView);
		}

		// Return it.
		return tabView.getView();
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
	 * Changes the removal list management view to the removal list creation
	 * view.
	 */
	public void showNewRemovalListView()
	{
		// Create a new removal list if it does not exist.
		if (newRemovalList == null)
			newRemovalList = new RemovalList();

		if (managementView.getContent().equals(getRemovalListCreationView()))
		{
			System.out.println("Resetting new removal list.");
			newRemovalList.reset();
		}
		else
		{
			System.out.println("Setting new removal list view.");
			managementView.setContent(getRemovalListCreationView());
		}

		System.out.println("Removal list is currently: " + newRemovalList.getObservableBoxes());
	}

	/**
	 * Changes the removal list management view to the removal list browsing view for Managers and greater.
	 * Other see the plain browsing view.
	 */
	public void showBrowseRemovalListsView()
	{
		try
		{
			if (browseView == null)
			{
				System.out.println("Creating removal list browsing view.");
				browseView = ListController.getTableView(this, DatabaseController.getRemovalListDataColumns(), DatabaseController.getObservableRemovalLists());
			}

			System.out.println("Removal list browsing: " + DatabaseController.getObservableRemovalLists());
		}
		catch (final NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}

		// Managers and greater see the management view.
		if (LoginController.userRoleIsGreaterOrEqualTo(new Manager()))
		{
			managementView.setBrowseListsButtonVisiblity(false);
			managementView.setContent(browseView);
		}
		else
		{
			// Others see just the browsing view.
			tabView.setView(browseView);
		}
	}

	public BorderPane getSearchResultsListView()
	{
		System.out.println("Getting search result list: " + DatabaseController.getObservableProductSearchResults());
		return (BorderPane) ListController.getTableView(this, DatabaseController.getProductSearchDataColumns(true, false),
				DatabaseController.getObservableProductSearchResults());
	}

	public BorderPane getNewRemovalListView()
	{
		System.out.println("Getting new removal list: " + newRemovalList.getObservableBoxes());

		return (BorderPane) ListController.getTableView(this, DatabaseController.getProductSearchDataColumns(false, true), newRemovalList.getObservableBoxes());
	}

	public void saveNewRemovalList()
	{
		if (newRemovalList.getObservableBoxes().size() > 0)
		{
			try
			{
				System.out.println("Saving List : " + newRemovalList.getObservableBoxes());

				if (newRemovalList.saveToDatabase())
				{
					DatabaseController.clearSearchResults();

					/*
					 * I would much rather create a new object rather than reset the old one but I can't figure out why
					 * it doesn't work. When a new removal list object is created the UI new list view never updates
					 * after that even though I'm refreshing the view after creating the object.
					 */
					newRemovalList.reset();

					creationView.refresh();
				}
				else
				{
					PopupController.error("Removal list saving failed.");
				}
			}
			catch (final NoDatabaseLinkException e)
			{
				DatabaseController.tryReLink();
			}
		}
		else
		{
			PopupController.info("Please add some product boxes first.");
		}
	}

	public void setNewRemovalListState(final RemovalListState newState)
	{
		System.out.println("New state is : " + newState);
		newRemovalList.setState(newState);
	}

	@SuppressWarnings("static-method")
	public void updateRemovalListState(final RemovalList removalList, final RemovalListState state)
	{
		removalList.setState(state);

		try
		{
			if (!DatabaseController.updateRemovalList(removalList))
				PopupController.error("Unable to save removal list state.");
		}
		catch (final NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}
	}
}
