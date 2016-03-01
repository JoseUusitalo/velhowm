package velho.view;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import velho.controller.ProductController;

/**
 * Creates tab for "Product Edit View"
 *
 * @author Edward
 *
 */
public class AddProductView
{
	private ProductController productController;
	private BorderPane bPane;

	public AddProductView(final ProductController productController)
	{
		this.productController = productController;
	}

	public BorderPane getProductView()
	{
		if (bPane == null)
		{

			bPane = new BorderPane();
			final GridPane productPane = new GridPane();

			bPane.setCenter(productPane);

			final GridPane mid = new GridPane();

			final Label productLabel = new Label("Product ID");
			productLabel.setAlignment(Pos.CENTER);
			productLabel.setMaxWidth(Double.MAX_VALUE);
			productLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
			mid.add(productLabel, 0, 0);

			final TextArea textField = new TextArea();
			textField.setPromptText("This is the TextField");
			textField.setPrefWidth(MainWindow.WINDOW_WIDTH / 5);
			textField.setMaxWidth(Double.MAX_VALUE);
			mid.add(textField, 0, 2);

			final ComboBox<Object> brandList = new ComboBox<Object>();
			brandList.setPromptText("Brand");
			mid.add(brandList, 0, 4);

			final ComboBox<Object> categoryList = new ComboBox<Object>();
			categoryList.setPromptText("Category");
			mid.add(categoryList, 0, 5);

			final Label popularityLabel = new Label("Popularity");
			popularityLabel.setAlignment(Pos.CENTER);
			popularityLabel.setMaxWidth(Double.MAX_VALUE);
			popularityLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
			mid.add(popularityLabel, 0, 7);

			final Button saveButton = new Button("Save");
			mid.add(saveButton, 0, 9);

			bPane.setCenter(mid);

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
