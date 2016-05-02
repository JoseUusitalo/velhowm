package velho.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import velho.controller.DatabaseController;
import velho.controller.ExternalSystemsController;
import velho.controller.LocalizationController;
import velho.controller.SearchController;
import velho.controller.UIController;
import velho.model.interfaces.GenericView;

/**
 * A view for searching multiple products at once.
 *
 * @author Jose Uusitalo
 */
public class ProductListSearch implements GenericView
{
	/**
	 * The root border pane.
	 */
	private BorderPane pane;

	/**
	 * The {@link SearchController}.
	 */
	private SearchController searchController;

	/**
	 * Searches the list of products
	 *
	 * @param searchController embodies SearchController
	 */
	public ProductListSearch(final SearchController searchController)
	{
		this.searchController = searchController;
	}

	/**
	 * Gets the product list search view.
	 *
	 * @param list the view of the search results
	 * @return a search view for searching multiple products at once
	 */
	public BorderPane getView(final BorderPane list)
	{
		if (pane == null)
		{
			pane = new BorderPane();
			VBox left = new VBox();
			Button printButton = new Button(LocalizationController.getString("printButton"));
			Button sendToScannerButton = new Button(LocalizationController.getString("sendToScannerButton"));

			final TextArea textArea = new TextArea();
			textArea.setPromptText((LocalizationController.getString("productSearchByNameOrCodeFirstRowText"))
					+ (LocalizationController.getString("productSearchByNameOrCodeSecondRowText"))
					+ (LocalizationController.getString("productSearchByNameOrCodeThirdRowText")));
			textArea.setPrefWidth(MainWindow.WINDOW_WIDTH / 5);
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

			left.getChildren().addAll(textArea, printButton, sendToScannerButton);

			VBox.setVgrow(textArea, Priority.ALWAYS);

			textArea.setOnKeyReleased(new EventHandler<KeyEvent>()
			{
				@Override
				public void handle(final KeyEvent event)
				{
					if (event.getCode() == KeyCode.ENTER)
					{
						searchController.searchByProductList(textArea.getText());
					}
				}
			});

			pane.setLeft(left);
			pane.setCenter(list);
			UIController.recordView(this);
		}
		return pane;
	}

	@Override
	public void recreate()
	{
		pane = null;
		getView(null);
	}

	@Override
	public void destroy()
	{
		pane = null;
	}
}
