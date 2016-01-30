package velho.controller;

import java.util.Map;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import velho.model.User;
import velho.view.ListView;

public class ListController
{
	public static Node getView(final Map<String, String> columnMap, final ObservableList<User> datalist)
	{
		ListView list = new ListView(columnMap, datalist);
		return list.getListView();
	}
}
