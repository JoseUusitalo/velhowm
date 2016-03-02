package velho.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import velho.controller.DatabaseController;
import velho.controller.ProductController;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.model.exceptions.NoDatabaseLinkException;

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

	public BorderPane getProductView() throws NoDatabaseLinkException
	{
		if (bPane == null)
		{

			bPane = new BorderPane();

			final GridPane mid = new GridPane();

			final TextField nameField = new TextField();
			nameField.setPromptText("Product name");
			nameField.setPrefWidth(MainWindow.WINDOW_WIDTH / 5);
			nameField.setMaxWidth(Double.MAX_VALUE);
			mid.add(nameField, 1, 0);

			final ComboBox<Object> brandList = new ComboBox<Object>();
			brandList.setPromptText("Brand");
			brandList.getItems().addAll(DatabaseController.getAllProductBrands());
			brandList.setMaxWidth(Double.MAX_VALUE);
			brandList.getSelectionModel().selectFirst();
			mid.add(brandList, 2, 0);

			final ComboBox<Object> categoryList = new ComboBox<Object>();
			categoryList.setPromptText("Category");
			categoryList.getItems().addAll(DatabaseController.getAllProductCategories());
			categoryList.setMaxWidth(Double.MAX_VALUE);
			categoryList.getSelectionModel().selectFirst();
			mid.add(categoryList, 3, 0);

			final Label popularityLabel = new Label("Popularity: ");
			popularityLabel.setAlignment(Pos.CENTER_RIGHT);
			mid.add(popularityLabel, 4, 0);

			Spinner<Integer> popularity = new Spinner<Integer>();
			popularity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 10000));
			mid.add(popularity, 5, 0);

			final Button saveButton = new Button("Save");

			saveButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					productController.saveProduct(nameField.getText(), (ProductBrand) brandList.getSelectionModel().getSelectedItem(), (ProductCategory) categoryList.getSelectionModel().getSelectedItem(), popularity.getValue().intValue());
				}
			});
			mid.add(saveButton, 6, 0);

			mid.setHgap(10);
			mid.getStyleClass().add("standard-padding");

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
