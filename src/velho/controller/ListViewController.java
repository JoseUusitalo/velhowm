package velho.controller;

import java.util.List;

import javafx.scene.Node;
import velho.view.AddUserView;
import velho.view.ListView;

public class ListViewController
{
	private ListView view;

	public ListViewController()
	{
		view = new ListView(this, null);

	}
	public Node getView()
	{
		return view.getListView();
	}
}
