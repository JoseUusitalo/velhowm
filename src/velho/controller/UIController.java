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
	 * @param mainWindow
	 * @param listController
	 */
	public UIController(final MainWindow mainWindow)
	{
		mainView = mainWindow;
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

		switch (currentUserRole.getName())
		{
			case "Administrator":
			case "Manager":
				mainView.addTab("Add User", userController.getView());
				//$FALL-THROUGH$
			case "Logistician":
				mainView.addTab("User List", getUserListView());
				break;
			default:
				System.out.println("Unknown user role.");
		}
	}

	/**
	 * Creates the user list view.
	 *
	 * @return the user list view
	 */
	private Node getUserListView()
	{
		try
		{
			return ListController.getView(DatabaseController.getPublicUserDataColumns(), DatabaseController.getPublicUserDataList());
		}
		catch (NoDatabaseLinkException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Attaches the {@link UserController} to this controller.
	 *
	 * @param userController
	 */
	public void setUserController(final UserController userController)
	{
		this.userController = userController;
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
