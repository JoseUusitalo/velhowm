package velho.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import velho.controller.DatabaseController;
import velho.controller.ExternalSystemsController;
import velho.controller.SearchController;

/**
 *
 * @author Joona Silvennoinen
 */
public class SearchTabView
{
	/**
	 * The root BorderPane for this view.
	 */
	private BorderPane bPane;

	/**
	 * Calls SearchControllers
	 */
	private SearchController searchController;

	/**
	 * Gets the information to SearchTabView form SearchController
	 * 
	 * @param searchController
	 */
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
			final VBox top = new VBox();
			top.getStyleClass().add("standard-background-color");

			bPane = new BorderPane();
			final GridPane searchPane = (GridPane) searchController.getSearchView();
			searchPane.setPadding(new Insets(0, 10, 10, 10));

			final HBox buttonsBox = new HBox(10);
			buttonsBox.setPadding(new Insets(0, 10, 10, 10));

			final Button printButton = new Button("Print");
			final Button sendToScannerButton = new Button("Send to BarcodeScanner ");

			printButton.setMaxWidth(Double.MAX_VALUE);
			printButton.setAlignment(Pos.CENTER);
			sendToScannerButton.setMaxWidth(Double.MAX_VALUE);
			sendToScannerButton.setAlignment(Pos.CENTER);

			printButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					ExternalSystemsController.sendDataToPrinter(DatabaseController.getObservableProductSearchResults());
				}
			});

			sendToScannerButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					ExternalSystemsController.sendDataToBarcodeScanner(DatabaseController.getObservableProductSearchResults());
				}
			});

			buttonsBox.getChildren().addAll(printButton, sendToScannerButton);

			top.getChildren().addAll(searchPane, buttonsBox);

			bPane.setTop(top);
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
