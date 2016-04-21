package velho.controller;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import javafx.scene.Node;
import velho.model.enums.Position;
import velho.model.enums.UserRole;
import velho.model.interfaces.GenericView;
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
	 * The {@link SearchController}.
	 */
	private SearchController searchController;

	/**
	 * The {@link RemovalListController}.
	 */
	private RemovalListController removalListController;

	/**
	 * The {@link ProductController}.
	 */
	private ProductController productController;

	/**
	 * The {@link LogController}.
	 */
	private LogController logController;

	/**
	 * The {@link ManifestController}.
	 */
	private ManifestController manifestController;

	/**
	 * The {@link RemovalPlatformController}.
	 */
	private RemovalPlatformController removalPlatformController;

	private static Set<GenericView> viewSet = new HashSet<GenericView>();

	/**
	 * @param mainWindow
	 * @param userController
	 * @param removalListController
	 * @param searchController
	 * @param logController
	 * @param manifestController
	 * @param productController
	 * @param removalPlatformController
	 */
	public void setControllers(final MainWindow mainWindow, final UserController userController, final RemovalListController removalListController, final SearchController searchController, final LogController logController, final ManifestController manifestController, final ProductController productController, final RemovalPlatformController removalPlatformController)
	{
		this.mainView = mainWindow;
		this.userController = userController;
		this.removalListController = removalListController;
		this.searchController = searchController;
		this.productController = productController;
		this.logController = logController;
		this.manifestController = manifestController;
		this.removalPlatformController = removalPlatformController;
	}

	/**
	 * Shows a view in the main window.
	 *
	 * @param position {@link Position} to show the view in
	 * @param view view to show
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
	 * @param currentUserRole {@link UserRole} viewing the main menu
	 */
	public void showMainMenu(final UserRole currentUserRole)
	{
		mainView.showMainMenu();

		/*
		 * What is shown in the tabs depends on your role.
		 */

		switch (currentUserRole)
		{
			case ADMINISTRATOR:
			case MANAGER:
				mainView.addTab(LocalizationController.getString("addUserTab"), userController.getView());
				mainView.addTab(LocalizationController.getString("addLogsTab"), logController.getView());
				//$FALL-THROUGH$
			case LOGISTICIAN:
				mainView.addTab(LocalizationController.getString("addSearchTab"), searchController.getSearchTabView());
				mainView.addTab(LocalizationController.getString("addProductListSearchTab"), searchController.getProductListSearchView());
				mainView.addTab(LocalizationController.getString("addManifestsTab"), manifestController.getView());
				mainView.addTab(LocalizationController.getString("addRemovalListsTab"), removalListController.getView());
				mainView.addTab(LocalizationController.getString("addProductTab"), productController.getAddProductView());
				mainView.addTab(LocalizationController.getString("addProductListTab"), productController.getTabView());
				mainView.addTab(LocalizationController.getString("addUserListTab"), getUserListView(currentUserRole));
				break;
			case GUEST:
				break;
			default:
				SYSLOG.error("Unknown user role '" + currentUserRole.getName() + "'.");
		}

		/*
		 * Check the state the of the removal platform when the main menu is
		 * shown after user has logged in.
		 */
		removalPlatformController.checkWarning();
	}

	/**
	 * Creates the user list view. The list contents change depending on who is
	 * logged in.
	 *
	 * @param currentUserRole the role of the user who is currently logged in
	 * @return the user list view
	 */
	private Node getUserListView(final UserRole currentUserRole)
	{
		/*
		 * What is shown in the user list depends on your role.
		 */
		switch (currentUserRole)
		{
			case ADMINISTRATOR:
			case MANAGER:
				return ListController.getTableView(userController, DatabaseController.getPublicUserDataColumns(true), DatabaseController.getAllUsers());
			case LOGISTICIAN:
				return ListController.getTableView(userController, DatabaseController.getPublicUserDataColumns(false), DatabaseController.getAllUsers());
			case GUEST:
				break;
			default:
				SYSLOG.error("Unknown user role '" + currentUserRole.getName() + "'.");
		}

		return null;
	}

	/**
	 * Resets the main menu to it's initial state.
	 */
	public void resetMainMenu()
	{
		mainView.reCreate();
	}

	public void reCreateAllViews()
	{
		SYSLOG.debug("recreating all views");
		Set<GenericView> temp = new HashSet<GenericView>(viewSet);
		for (GenericView view : temp)
		{
			view.reCreate();
		}
		showMainMenu(LoginController.getCurrentUser().getRole());
	}

	/**
	 * Forcibly selects the specified tab and changes the view in the main
	 * window to that tab.
	 *
	 * @param tabName name of the tab
	 */
	public void selectTab(final String tabName)
	{
		mainView.selectTab(tabName);
	}

	public static void recordView(final GenericView view)
	{
		viewSet.add(view);
	}

}
