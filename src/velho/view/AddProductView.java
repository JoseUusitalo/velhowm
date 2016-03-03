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
import velho.model.Product;
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
	private TextField nameField;
	private ComboBox<Object> brandList;
	private ComboBox<Object> categoryList;
	private Label popularityLabel;
	private Spinner<Integer> popularity;
	private Button saveButton;

	/**
	 * adds the product view
	 *
	 * @param productController
	 */

	public AddProductView(final ProductController productController)
	{
		this.productController = productController;
	}

	/**
	 * creates BorderPane
	 *
	 * @return
	 * @throws NoDatabaseLinkException
	 */
	public BorderPane getProductView() throws NoDatabaseLinkException
	{
		if (bPane == null)
		{

			bPane = new BorderPane();

			final GridPane mid = new GridPane();

			nameField = new TextField();
			nameField.setPromptText("Product name");
			nameField.setPrefWidth(MainWindow.WINDOW_WIDTH / 5);
			nameField.setMaxWidth(Double.MAX_VALUE);
			mid.add(nameField, 1, 0);

			brandList = new ComboBox<Object>();
			// TODO make it so that you dont need to press enter
			brandList.setPromptText("Brand");
			brandList.getItems().addAll(DatabaseController.getAllProductBrands());
			brandList.setMaxWidth(Double.MAX_VALUE);
			brandList.getSelectionModel().selectFirst();
			brandList.setEditable(true);
			mid.add(brandList, 2, 0);

			categoryList = new ComboBox<Object>();
			categoryList.setEditable(true);
			// TODO make it so that you dont need to press enter
			categoryList.setPromptText("Category");
			categoryList.getItems().addAll(DatabaseController.getAllProductCategories());
			categoryList.setMaxWidth(Double.MAX_VALUE);
			categoryList.getSelectionModel().selectFirst();
			mid.add(categoryList, 3, 0);

			popularityLabel = new Label("Popularity: ");
			popularityLabel.setAlignment(Pos.CENTER_RIGHT);
			mid.add(popularityLabel, 4, 0);

			popularity = new Spinner<Integer>();
			popularity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 10000));
			mid.add(popularity, 5, 0);

			saveButton = new Button("Save");

			saveButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					Object brand = brandList.valueProperty().getValue();
					Object category = categoryList.valueProperty().getValue();
					productController.saveProduct(nameField.getText(), brand, category, popularity.getValue().intValue());
				}
			});
			mid.add(saveButton, 6, 0);

			mid.setHgap(10);
			mid.getStyleClass().add("standard-padding");

			bPane.setCenter(mid);
			setData(DatabaseController.getProductByID(1));

		}
		return bPane;
	}

	/**
	 * saves data to database
	 */

	public void setData(final Product product)
	{
		nameField.setText(product.getName());
		brandList.getSelectionModel().select(product.getBrand());
		categoryList.getSelectionModel().select(product.getCategory());
		popularity.getEditor().setText(String.valueOf(product.getPopularity()));
	}
}
