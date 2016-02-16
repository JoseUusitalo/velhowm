package velho.controller;

import java.time.LocalDate;

import javafx.scene.Node;
import velho.view.SearchView;

public class SearchController
{
	/**
	 * The add user view.
	 */
	private SearchView view;

	public SearchController()
	{
		view = new SearchView(this);
	}

	public void productSearch(final String nameField, final int productCountField, final int popularityField, final String brandbox, final String categorybox,
			final LocalDate localDate, final LocalDate localDate2)
	{
		System.out
				.println(nameField + " " + productCountField + " " + popularityField + " " + brandbox + " " + categorybox + " " + localDate + " " + localDate2);
	}

	public Node getView()
	{
		return view.getView();
	}
}
