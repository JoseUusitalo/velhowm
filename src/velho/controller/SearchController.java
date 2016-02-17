package velho.controller;

import java.time.LocalDate;

import javafx.scene.Node;
import velho.view.SearchView;

public class SearchController
{
	public SearchController()
	{
	}

	public void productSearch(final String nameField, final int productCountField, final int popularityField, final String brandbox, final String categorybox,
			final LocalDate localDate, final LocalDate localDate2)
	{
		System.out
				.println(nameField + " " + productCountField + " " + popularityField + " " + brandbox + " " + categorybox + " " + localDate + " " + localDate2);
	}

	public Node getView()
	{
		return new SearchView(this).getView();
	}
}
