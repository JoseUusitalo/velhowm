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
 * The singleton controller for the {@link MainWindow}.
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
	 * The {@link ManifestController}.
	 */
	private ManifestController manifestController;

	/**
	 * The {@link RemovalPlatformController}.
	 */
	private RemovalPlatformController removalPlatformController;

	/**
	 * The {@link CSVController}.
	 */
	private CSVController csvController;

	/**
	 * The main set of views in this application.
	 */
	private static Set<GenericView> viewSet = new HashSet<GenericView>();

	/**
	 * A private inner class holding the class instance.
	 *
	 * @author Jose Uusitalo
	 */
	private static class Holder
	{
		/**
		 * The only instance of {@link UIController}.
		 */
		private static final UIController INSTANCE = new UIController();
	}

	/**
	 */
	private UIController()
	{
		// No need to instantiate this class.
	}

	/**
	 * Gets the instance of the {@link UIController}.
	 *
	 * @return the UI controller
	 */
	public static synchronized UIController getInstance()
	{
		return Holder.INSTANCE;
	}

	public void setControllers(final MainWindow mainWindow, final ManifestController manifestController,
			final RemovalPlatformController removalPlatformController, final CSVController csvController)
	{
		// TODO: remove
		this.mainView = mainWindow;
		this.manifestController = manifestController;
		this.removalPlatformController = removalPlatformController;
		this.csvController = csvController;
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
				mainView.addTab(LocalizationController.getInstance().getString("addUserTab"), UserController.getInstance().getView());
				mainView.addTab(LocalizationController.getInstance().getString("addLogsTab"), LogController.getInstance().getView());
				//$FALL-THROUGH$
			case LOGISTICIAN:
				mainView.addTab(LocalizationController.getInstance().getString("addUserListTab"),
						UserController.getInstance().getUserListView(currentUserRole));
				mainView.addTab(LocalizationController.getInstance().getString("csvTab"), csvController.getCSVView());
				mainView.addTab(LocalizationController.getInstance().getString("addBrandsTab"), ProductController.getInstance().getBrandsTab());
				mainView.addTab(LocalizationController.getInstance().getString("addProductTypesTab"), ProductController.getInstance().getProductTypesTab());
				mainView.addTab(LocalizationController.getInstance().getString("addCategoriesTab"), ProductController.getInstance().getCategoryTab());
				mainView.addTab(LocalizationController.getInstance().getString("addProductListTab"), ProductController.getInstance().getProductTabView());
				mainView.addTab(LocalizationController.getInstance().getString("addProductBoxesTab"), ProductController.getInstance().getProductBoxesTab());
				mainView.addTab(LocalizationController.getInstance().getString("addManifestsTab"), manifestController.getView());
				mainView.addTab(LocalizationController.getInstance().getString("addRemovalListsTab"), RemovalListController.getInstance().getView());
				//$FALL-THROUGH$
			case GUEST:
				mainView.addTab(LocalizationController.getInstance().getString("addSearchTab"), SearchController.getInstance().getSearchTabView());
				mainView.addTab(LocalizationController.getInstance().getString("addProductListSearchTab"),
						SearchController.getInstance().getProductListSearchView());
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
	 * Resets the main menu to it's initial state.
	 */
	public void resetMainMenu()
	{
		mainView.recreate();
	}

	public void recreateAllViews()
	{
		SYSLOG.debug("recreating all views");
		Set<GenericView> temp = new HashSet<GenericView>(viewSet);
		for (GenericView view : temp)
		{
			view.recreate();
		}
		showMainMenu(LoginController.getInstance().getCurrentUser().getRole());
	}

	@SuppressWarnings("static-method")
	public void destroyAllViews()
	{
		SYSLOG.debug("Destroying all views.");
		Set<GenericView> temp = new HashSet<GenericView>(viewSet);
		for (GenericView view : temp)
		{
			view.destroy();
		}
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

	@SuppressWarnings("static-method")
	public void recordView(final GenericView view)
	{
		viewSet.add(view);
	}
}
