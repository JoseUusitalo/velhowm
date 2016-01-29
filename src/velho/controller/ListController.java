package velho.controller;

import javafx.scene.Node;
import velho.view.ListView;

public class ListController
{
	private ListView view;

	public ListController()
	{
		view = new ListView(this, null);

	}
	
	public Node getView()
	{
		return view.getListView();
	}
}
