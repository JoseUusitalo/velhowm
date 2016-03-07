package velho.controller;

import org.apache.log4j.Logger;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import velho.model.Manager;
import velho.model.ProductBoxSearchResultRow;
import velho.model.RemovalList;
import velho.model.RemovalListState;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.model.interfaces.UIActionController;
import velho.view.GenericTabView;
import velho.view.RemovalListCreationView;
import velho.view.RemovalListManagementView;
import velho.view.RemovalListView;

/**
 * A class for managing {@link RemovalList} objects.
 *
 * @author Jose Uusitalo
 */
public class RemovalListController implements UIActionController
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(RemovalListController.class.getName());

	/**
	 * Apache log4j logger: User.
	 */
	private static final Logger USRLOG = Logger.getLogger("userLogger");

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
	private GenericTabView tabView;

	/**
	 * @param listController
	 * @param searchController
	 */
	public RemovalListController(final SearchController searchController)
	{
		this.searchController = searchController;
		tabView = new GenericTabView();
	}

	/**
	 * Gets the view for managing and viewing existing removal lists.
	 *
	 * @return the removal list management view
	 */
	public Node getView()
	{
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
	private Node getRemovalListCreationView() throws NoDatabaseLinkException
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
		try
		{
			// Create a new removal list if it does not exist.
			if (newRemovalList == null)
				newRemovalList = new RemovalList();

			if (managementView.getContent().equals(getRemovalListCreationView()))
			{
				USRLOG.info("Resetting new removal list.");
				newRemovalList.reset();
			}
			else
			{
				SYSLOG.trace("Setting new removal list view.");
				managementView.setContent(getRemovalListCreationView());
			}

			SYSLOG.trace("Removal list is currently: " + newRemovalList.getObservableBoxes());
		}
		catch (NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}
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
				SYSLOG.trace("Creating removal list browsing view.");
				browseView = ListController.getTableView(this, DatabaseController.getRemovalListDataColumns(), DatabaseController.getObservableRemovalLists());
			}

			SYSLOG.trace("Removal list browsing: " + DatabaseController.getObservableRemovalLists());
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

	/**
	 * Gets a view showing the search results for adding/removing product boxes to/from a new removal list.
	 *
	 * @return the search results
	 */
	public BorderPane getSearchResultsListView()
	{
		SYSLOG.trace("Getting search result list: " + DatabaseController.getObservableProductSearchResults());
		return (BorderPane) ListController.getTableView(this, DatabaseController.getProductSearchDataColumns(true, false),
				DatabaseController.getObservableProductSearchResults());
	}

	/**
	 * Gets the view for creating a new removal list.
	 *
	 * @return the view for creating a new removal list
	 */
	public BorderPane getNewRemovalListView()
	{
		SYSLOG.trace("Getting new removal list: " + newRemovalList.getObservableBoxes());

		return (BorderPane) ListController.getTableView(this, DatabaseController.getProductSearchDataColumns(false, true), newRemovalList.getObservableBoxes());
	}

	/**
	 * Saves the new removal list.
	 */
	public void saveNewRemovalList()
	{
		if (newRemovalList.getObservableBoxes().size() > 0)
		{
			try
			{
				SYSLOG.info("Saving Removal List: " + newRemovalList.getBoxes());

				if (DatabaseController.save(newRemovalList) > 0)
				{
					DatabaseController.clearSearchResults();

					USRLOG.info("Created a new Removal List: " + newRemovalList.getBoxes());

					/*
					 * I would much rather create a new object rather than reset the old one but I can't figure out why
					 * it doesn't work.
					 * When a new removal list object is created the UI new list view never updates after that even
					 * though I'm refreshing the view after creating the object.
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

	/**
	 * Changes the new removal list state.
	 *
	 * @param newState the new {@link RemovalListState}
	 */
	public void setNewRemovalListState(final RemovalListState newState)
	{
		SYSLOG.trace("New state is : " + newState);
		newRemovalList.setState(newState);
	}

	/**
	 * Changes the state of the specified removal list.
	 *
	 * @param removalList the {@link RemovalList} to modify
	 * @param newState the new {@link RemovalListState}
	 */
	@SuppressWarnings("static-method")
	public void updateRemovalListState(final RemovalList removalList, final RemovalListState state)
	{
		removalList.setState(state);

		try
		{
			if (DatabaseController.save(removalList) < 0)
				PopupController.error("Unable to save removal list state.");
		}
		catch (final NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}
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
	public void removeAction(final Object data)
	{
		USRLOG.debug("Removing from new removal list: " + ((ProductBoxSearchResultRow) data).getBox());
		if (!newRemovalList.removeProductBox(((ProductBoxSearchResultRow) data).getBox()))
		{
			USRLOG.debug("Failed to remove product box from removal list.");
			PopupController.error("Failed to remove product box from removal list.");
		}
	}

	@Override
	public void deleteAction(final Object data)
	{
		try
		{
			if (!DatabaseController.deleteRemovalListByID(((RemovalList) data).getDatabaseID()))
			{
				USRLOG.debug("Failed to deleting removal list.");
				PopupController.error("Failed to deleting removal list.");
			}
			else
				USRLOG.info("Deleted removal list: " + ((RemovalList) data).toString());
		}
		catch (final NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}
	}

	@Override
	public void addAction(final Object data)
	{
		USRLOG.debug("Adding to new removal list: " + ((ProductBoxSearchResultRow) data).getBox());
		if (!newRemovalList.addProductBox(((ProductBoxSearchResultRow) data).getBox()))
		{
			USRLOG.trace("Product box is already on the list.");
			PopupController.error("That product box is already on the removal list.");
		}
	}

	@Override
	public void viewAction(final Object data)
	{
		USRLOG.info("Viewing removal list " + data);

		try
		{
			if (LoginController.userRoleIsGreaterOrEqualTo(new Manager()))

				managementView.setContent(new RemovalListView((RemovalList) data, this).getView());

			else
				tabView.setView(new RemovalListView((RemovalList) data, this).getView());
		}
		catch (NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}
	}
}
