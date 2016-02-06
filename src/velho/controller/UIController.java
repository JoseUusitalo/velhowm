package velho.controller;

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
	 * @param mainWindow
	 * @param listController
	 */
	public UIController(final MainWindow mainWindow, final ListController listController, final UserController userController)
	{
		mainView = mainWindow;
		this.listController = listController;
		this.userController = userController;
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
				// Impossible.
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
		 * What is shown in the UI depends on your role.
		 */

		switch (currentUserRole.getName())
		{
			case "Administrator":
			case "Manager":
				mainView.addTab("Add User", userController.getView());
				//$FALL-THROUGH$
			case "Logistician":
				mainView.addTab("User List", getUserListView(currentUserRole));
				mainView.addTab("Product List", listController.getProductListView(DatabaseController.getPublicProductDataColumns(), DatabaseController.getPublicProductDataList()));
				break;
			default:
				System.out.println("Unknown user role.");
		}
	}

	/**
	 * Creates the user list view.
	 * The list contents change depending on who is logged in.
	 *
	 * @param currentUserRole the role of the user who is currently logged in
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
					return listController.getUserListView(DatabaseController.getPublicUserDataColumns(true), DatabaseController.getPublicUserDataList());
				case "Logistician":
					return listController.getUserListView(DatabaseController.getPublicUserDataColumns(false), DatabaseController.getPublicUserDataList());
				default:
					System.out.println("Unknown user role.");
			}
		}
		catch (NoDatabaseLinkException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
