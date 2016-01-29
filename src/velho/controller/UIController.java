package velho.controller;

import javafx.scene.Node;
import velho.model.enums.Position;
import velho.model.interfaces.UserRole;
import velho.view.MainWindow;

public class UIController
{
	private MainWindow mainView;
	private UserController userController;
	private ListViewController listController;

	public UIController()
	{

	}

	public UIController(final MainWindow mainWindow, final ListViewController listController)
	{
		mainView = mainWindow;
		this.listController = listController;
	}

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
				mainView.addTab("User List", listController.getView());
				break;
			default:
				System.out.println("Unknown user role.");
		}
	}

	public void setUserController(final UserController userController)
	{
		this.userController = userController;
	}

	public void resetMainMenu()
	{
		mainView.resetMainMenu();
	}
}
