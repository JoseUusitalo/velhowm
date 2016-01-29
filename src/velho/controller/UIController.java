package velho.controller;

import javafx.geometry.Pos;
import javafx.scene.Node;
import velho.view.MainWindow;

public class UIController
{
	private MainWindow mainView;

	public UIController()
	{

	}

	public UIController(final MainWindow mainWindow)
	{
		mainView = mainWindow;
	}

	public void setView(final Node view)
	{
		mainView.setView(view);
	}

	public void showMainMenu()
	{
		mainView.showMainMenu();
	}
}
