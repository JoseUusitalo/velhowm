package velho.controller;

import org.apache.log4j.Logger;

import javafx.scene.Node;
import velho.model.enums.Position;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.model.interfaces.UserRole;
import velho.view.MainWindow;

/**
 * The controller for the {@link MainWindow}.
 *
 * @author Jose Uusitalo
 */
public class UIController
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(UIController.class.getName());

	/**
	 * The {@link MainWindow}.
	 */
	private MainWindow mainView;

	/**
	 * The {@link UserController}.
	 */
	private UserController userController;

	/**
	 * The {@link ListController}.
	 */
	private ListController listController;

	/**
	 * The {@link ProductController}.
	 */
	private ProductController productController;

	/**
	 * The {@link SearchController}.
	 */
	private SearchController searchController;

	/**
	 * The {@link RemovalListController}.
	 */
	private RemovalListController removalListController;

	/**
	 * The {@link LogController}.
	 */
	private LogController logController;

	private ManifestController manifestController;

	public UIController(final MainWindow mainWindow, final ListController listController, final UserController userController, final RemovalListController removalListController, final SearchController searchController, final LogController logController, final ManifestController manifestController, final ProductController productController)
	{
		this.mainView = mainWindow;
		this.listController = listController;
		this.userController = userController;
		this.removalListController = removalListController;
		this.searchController = searchController;
		this.logController = logController;
		this.manifestController = manifestController;
		this.productController = productController;
	}

	/**
	 * Shows a view in the main window.
	 *
	 * @param position
	 *            {@link Position} to show the view in
	 * @param view
	 *            view to show
	 */
	public void setView(final Position position, final Node view)
	{
		switch (position)
		{
			case TOP:
				mainView.setTopView(view);
				break;
			case RIGHT:
				mainView.setRightView(view);
				break;
			case BOTTOM:
				mainView.setBottomView(view);
				break;
			case LEFT:
				mainView.setLeftView(view);
				break;
			case CENTER:
				mainView.setCenterView(view);
				break;
			default:
				SYSLOG.error("Unknown position '" + position.toString() + "'.");
		}
	}

	/**
	 * Shows the main menu as seen by the specified role.
	 *
	 * @param currentUserRole
	 *            {@link UserRole} viewing the main menu
	 */
	public void showMainMenu(final UserRole currentUserRole)
	{
		mainView.showMainMenu();

		/*
		 * What is shown in the UI depends on your role.
		 */

		switch (currentUserRole.getName())
		{
			case "Administrator":
			case "Manager":
				mainView.addTab("Add User", userController.getView());
				mainView.addTab("Logs", logController.getView());
				//$FALL-THROUGH$
			case "Logistician":
				mainView.addTab("Removal Lists", removalListController.getView());
				mainView.addTab("User List", getUserListView(currentUserRole));
				mainView.addTab("Product List", listController.getProductListView(DatabaseController.getProductDataColumns(false, false), DatabaseController.getObservableProducts()));
				mainView.addTab("Search", searchController.getSearchTabView());
				mainView.addTab("Product List Search", listController.getProductListSearchView());
				mainView.addTab("Manifests", manifestController.getView());
				mainView.addTab("Product Data View", productController.getView());
				break;
			default:
				SYSLOG.error("Unknown user role '" + currentUserRole.getName() + "'.");
		}
	}

	/**
	 * Creates the user list view. The list contents change depending on who is
	 * logged in.
	 *
	 * @param currentUserRole
	 *            the role of the user who is currently logged in
	 * @return the user list view
	 */
	private Node getUserListView(final UserRole currentUserRole)
	{
		/*
		 * What is shown in the user list depends on your role.
		 */
		try
		{
			switch (currentUserRole.getName())
			{
				case "Administrator":
				case "Manager":
					return listController.getUserListView(DatabaseController.getPublicUserDataColumns(true), DatabaseController.getObservableUsers());
				case "Logistician":
					return listController.getUserListView(DatabaseController.getPublicUserDataColumns(false), DatabaseController.getObservableUsers());
				default:
					SYSLOG.error("Unknown user role '" + currentUserRole.getName() + "'.");
			}
		} catch (NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}

		return null;

	}

	/**
	 * Resets the main menu to it's initial state.
	 */
	public void resetMainMenu()
	{
		mainView.destroy();
	}

	/**
	 * Resets all views to their initial state.
	 */
	public void destroyViews()
	{
		mainView.destroy();
		userController.destroyView();
	}
}
