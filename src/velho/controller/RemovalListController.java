package velho.controller;

import org.apache.log4j.Logger;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import velho.controller.database.DatabaseController;
import velho.controller.interfaces.UIActionController;
import velho.model.ProductBoxSearchResultRow;
import velho.model.RemovalList;
import velho.model.RemovalListState;
import velho.model.enums.UserRole;
import velho.view.GenericTabView;
import velho.view.ListView;
import velho.view.RemovalListCreationView;
import velho.view.RemovalListManagementView;
import velho.view.RemovalListView;

/**
 * The singleton controller for managing {@link RemovalList} objects.
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
	private final GenericTabView tabView;

	/**
	 * A private inner class holding the class instance.
	 *
	 * @author Jose Uusitalo
	 */
	private static class Holder
	{
		/**
		 * The only instance of {@link RemovalListController}.
		 */
		private static final RemovalListController INSTANCE = new RemovalListController();
	}

	/**
	 */
	private RemovalListController()
	{
		tabView = new GenericTabView();
	}

	/**
	 * Gets the instance of the {@link RemovalListController}.
	 *
	 * @return the removal list controller
	 */
	public static synchronized RemovalListController getInstance()
	{
		return Holder.INSTANCE;
	}

	/**
	 * Gets the view for managing and viewing existing removal lists.
	 *
	 * @return the removal list management view
	 */
	public Node getView()
	{
		// Managers and greater see the management view.
		if (LoginController.getInstance().userRoleIsGreaterOrEqualTo(UserRole.MANAGER))
		{
			managementView = new RemovalListManagementView();
			creationView = new RemovalListCreationView();

			// Initially the browsing view is shown.
			if (managementView.getView().getCenter() == null)
				showBrowseRemovalListsView();

			tabView.setView(managementView.getView());
		}
		else
		{
			// Others see just the browsing view.
			if (browseView == null)
				browseView = ListController.getTableView(this, DatabaseController.getInstance().getRemovalListDataColumns(),
						DatabaseController.getInstance().getAllRemovalLists());
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
			newRemovalList = new RemovalList(DatabaseController.getInstance().getRemovalListStateByID(1));

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

	/**
	 * Changes the removal list management view to the removal list browsing
	 * view for Managers and greater.
	 * Other see the plain browsing view.
	 */
	public void showBrowseRemovalListsView()
	{
		if (browseView == null)
		{
			SYSLOG.trace("Creating removal list browsing view.");
			browseView = ListController.getTableView(this, DatabaseController.getInstance().getRemovalListDataColumns(),
					DatabaseController.getInstance().getAllRemovalLists());
		}

		SYSLOG.trace("Removal list browsing: " + DatabaseController.getInstance().getAllRemovalLists());

		// Managers and greater see the management view.
		if (LoginController.getInstance().userRoleIsGreaterOrEqualTo(UserRole.MANAGER))
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
	 * Gets a view showing the search results for adding/removing product boxes
	 * to/from a new removal list.
	 *
	 * @return the search results
	 */
	public BorderPane getSearchResultsListView()
	{
		SYSLOG.trace("Getting search result list: " + DatabaseController.getInstance().getObservableProductSearchResults());
		return (BorderPane) ListController.getTableView(this, DatabaseController.getInstance().getProductSearchDataColumns(true, false),
				DatabaseController.getInstance().getObservableProductSearchResults());
	}

	/**
	 * Gets the view for creating a new removal list.
	 *
	 * @return the view for creating a new removal list
	 */
	public BorderPane getNewRemovalListView()
	{
		SYSLOG.trace("Getting new removal list: " + newRemovalList.getObservableBoxes());

		return (BorderPane) ListController.getTableView(this, DatabaseController.getInstance().getProductSearchDataColumns(false, true),
				newRemovalList.getObservableBoxes());
	}

	/**
	 * Saves the new removal list.
	 */
	public void saveNewRemovalList()
	{
		if (newRemovalList.getObservableBoxes().size() > 0)
		{
			SYSLOG.info("Saving Removal List: " + newRemovalList.getBoxes());

			if (DatabaseController.getInstance().saveOrUpdate(newRemovalList) > 0)
			{
				DatabaseController.getInstance().clearSearchResults();

				USRLOG.info("Created a new Removal List: " + newRemovalList.getBoxes());

				/*
				 * I would much rather create a new object rather than reset the
				 * old one but I can't figure out why
				 * it doesn't work.
				 * When a new removal list object is created the UI new list
				 * view never updates after that even
				 * though I'm refreshing the view after creating the object.
				 */
				newRemovalList.reset();

				creationView.refresh();
			}
			else
			{
				PopupController.getInstance().error(LocalizationController.getInstance().getString("removalListSavingFailPopUp"));
			}
		}
		else
		{
			PopupController.getInstance().info(LocalizationController.getInstance().getString("addProductBoxesFirstNotice"));
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
	 * @param state the new {@link RemovalListState}
	 */
	@SuppressWarnings("static-method")
	public void updateRemovalListState(final RemovalList removalList, final RemovalListState state)
	{
		removalList.setState(state);

		if (DatabaseController.getInstance().saveOrUpdate(removalList) < 1)
			PopupController.getInstance().error(LocalizationController.getInstance().getString("unableToSaveRemovalListStateNotice"));
	}

	@Override
	public void createAction(final Object data)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateAction(final Object data)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeAction(final Object data)
	{
		USRLOG.debug("Removing from new removal list: " + ((ProductBoxSearchResultRow) data).getBox());
		if (!newRemovalList.removeProductBox(((ProductBoxSearchResultRow) data).getBox()))
		{
			USRLOG.debug("Failed to remove product box from removal list.");
			PopupController.getInstance().error(LocalizationController.getInstance().getString("failedToRemoveProductBoxFromRemovalListNotice"));
		}
	}

	@Override
	public void deleteAction(final Object data)
	{
		if (PopupController.getInstance().confirmation(LocalizationController.getInstance().getString("removalListDeletationConfirmation")))
		{
			DatabaseController.getInstance().deleteRemovalList((RemovalList) data);
			USRLOG.info("Deleted removal list: " + ((RemovalList) data).toString());
		}
	}

	@Override
	public void addAction(final Object data)
	{
		USRLOG.debug("Adding to new removal list: " + ((ProductBoxSearchResultRow) data).getBox());
		if (!newRemovalList.addProductBox(((ProductBoxSearchResultRow) data).getBox()))
		{
			USRLOG.trace("Product box is already on the list.");
			PopupController.getInstance().error(LocalizationController.getInstance().getString("productBoxExistsOnRemovalList"));
		}
	}

	@Override
	public void viewAction(final Object data)
	{
		USRLOG.info("Viewing removal list " + data);

		if (LoginController.getInstance().userRoleIsGreaterOrEqualTo(UserRole.MANAGER))
			managementView.setContent(new RemovalListView((RemovalList) data).getView());
		else
			tabView.setView(new RemovalListView((RemovalList) data).getView());
	}

	@Override
	public void recreateViews(final ListView node)
	{
		browseView = null;
		showBrowseRemovalListsView();
	}
}
