package velho.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import velho.controller.DatabaseController;
import velho.controller.ExternalSystemsController;
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
			VBox top = new VBox();
			top.setBackground(new Background(new BackgroundFill(Paint.valueOf("EEEEEE"), null, null)));

			bPane = new BorderPane();
			GridPane uus = (GridPane) searchController.getSearchView();
			uus.setPadding(new Insets(0, 10, 10, 10));

			HBox buttonsBox = new HBox(10);
			// TODO: Use CSS.
			buttonsBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("EEEEEE"), null, null)));
			buttonsBox.setPadding(new Insets(0, 10, 10, 10));

			Button printButton = new Button("Print");
			Button sendToScannerButton = new Button("Send to BarcodeScanner ");

			printButton.setMaxWidth(Double.MAX_VALUE);
			printButton.setAlignment(Pos.CENTER);
			sendToScannerButton.setMaxWidth(Double.MAX_VALUE);
			sendToScannerButton.setAlignment(Pos.CENTER);

			printButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					ExternalSystemsController.sendDataToPrinter(DatabaseController.getProductSearchResultViewList());
				}
			});

			sendToScannerButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					ExternalSystemsController.sendDataToBarcodeScanner(DatabaseController.getProductSearchResultViewList());
				}
			});

			buttonsBox.getChildren().addAll(printButton, sendToScannerButton);

			top.getChildren().addAll(uus, buttonsBox);

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
