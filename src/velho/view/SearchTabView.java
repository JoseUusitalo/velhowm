package velho.view;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import velho.controller.SearchController;

public class SearchTabView

{
	/**
	 * The root BorderPane for this view.
	 */
	private BorderPane bPane;
	private SearchController searchController;

	public SearchTabView(final SearchController searchController)
	{
		this.searchController = searchController;
	}

	/**
	 * Gets the login view.
	 *
	 * @return the login view BorderPane
	 */
	public BorderPane getView()
	{
		if (bPane == null)
		{
			bPane = new BorderPane();
			GridPane uus = (GridPane) searchController.getSearchView();
			uus.setPadding(new Insets(0, 10, 10, 10));
			bPane.setTop(uus);
			bPane.setCenter(searchController.getResultsView());
		}
		return bPane;
	}

	/**
	 * Destroys the view.
	 */

	public void destroy()
	{
		bPane = null;
	}
}
