package velho.view;

import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import velho.controller.ListController;

/**
 * A view for searching multiple products at once.
 *
 * @author Jose Uusitalo
 */
public class ProductListSearch
{
	/**
	 * The root border pane.
	 */
	private BorderPane pane;

	/**
	 * The {@link ListController}.
	 */
	private ListController listController;

	public ProductListSearch(final ListController listController)
	{
		this.listController = listController;
	}

	/**
	 * Gets the product list search view.
	 *
	 * @return a search view for searching multiple products at once
	 */
	public BorderPane getProductListSearch(final BorderPane list)
	{
		if (pane == null)
		{
			pane = new BorderPane();
			TextArea textArea = new TextArea();
			textArea.setPromptText("Please type one product code or name per line. "
					+ "To Search for multiple products of the same type, type the number of products you want and a colon before the product name or ID. "
					+ "Empty lines and redundant spaces are ignored.");
			textArea.setPrefWidth(MainWindow.WINDOW_WIDTH / 3);

			textArea.setOnKeyReleased(new EventHandler<KeyEvent>()
			{
				@Override
				public void handle(final KeyEvent event)
				{
					if (event.getCode() == KeyCode.ENTER)
					{
						listController.searchByProductList(textArea.getText());
					}
				}
			});

			pane.setLeft(textArea);
			pane.setCenter(list);
		}
		return pane;
	}
}