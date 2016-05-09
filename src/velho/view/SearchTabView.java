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
import velho.controller.ExternalSystemsController;
import velho.controller.LocalizationController;
import velho.controller.SearchController;
import velho.controller.UIController;
import velho.controller.database.DatabaseController;
import velho.model.interfaces.GenericView;

/**
 * @author Edward Puustinen
 */
public class SearchTabView implements GenericView
{
	/**
	 * The root BorderPane for this view.
	 */
	private BorderPane bPane;

	private final SearchController searchController;

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

			final Button printButton = new Button(LocalizationController.getString("printButton"));
			final Button sendToScannerButton = new Button(LocalizationController.getString("sendToBarcodeScannerButton"));

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

			UIController.recordView(this);
		}
		return bPane;
	}

	/**
	 * Destroys the view.
	 */

	@Override
	public void recreate()
	{
		bPane = null;
		getView();
	}

	@Override
	public void destroy()
	{
		bPane = null;
	}
}
