package velho.controller;

import javafx.scene.Node;
import velho.model.RemovalList;
import velho.view.RemovalListCreationView;
import velho.view.RemovalListManagementView;

public class RemovalListController
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
	 * @throws NoDatabaseLinkException
	 */
	public RemovalListController(final ListController listController)
	{
		this.listController = listController;
		managementView = new RemovalListManagementView(this, this.listController);
	}

	/**
	 * Gets the view for managing and viewing existing removal lists.
	 *
	 * @return the removal list management view
	 */
	public Node getRemovalListManagementView()
	{
		return managementView.getView();
	}

	/**
	 * Changes the removal list management view to the removal list creation view.
	 */
	public void showNewRemovalListView()
	{
		// Create a new removal list if it does not exist.
		if (newRemovalList == null)
			newRemovalList = new RemovalList();

		managementView.getView().setCenter(getRemovalListCreationView());
	}

	/**
	 * Changes the removal list management view to the removal list browsing view.
	 */
	public void showBrowseRemovalListsView()
	{
		managementView.getView().setCenter(listController.getRemovalListView());
	}

	/**
	 * Gets the removal list creation view.
	 *
	 * @return view for creating new removal lists
	 */
	private Node getRemovalListCreationView()
	{
		System.out.println("Showing removal list creation view.");
		RemovalListCreationView creationView = new RemovalListCreationView(this, listController);
		return creationView.getView();
	}

	/**
	 * Gets a view showing the removal list currently being created.
	 *
	 * @return view for the current removal list being created
	 */
	public Node getCurrentRemovalListView()
	{
		System.out.println("Showing current new removal list.");
		return listController.getTableView(DatabaseController.getPublicProductDataColumns(false), newRemovalList.getObservableList());
	}
}
