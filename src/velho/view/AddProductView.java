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
import javafx.scene.input.KeyEvent;
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
	 * Adds the product view.
	 * Has to be manually inputed in the UIController at "switch (currentUserRole.getName())".
	 *
	 * @param productController makes it view able
	 */

	public AddProductView(final ProductController productController)
	{
		this.productController = productController;
	}

	/**
	 * Creates BorderPane for the get Add Product tab.
	 *
	 * @return the bPane
	 * @throws NoDatabaseLinkException to get the data
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
			// popularity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 10000));
			popularity.setEditable(true);
			popularity.getEditor().getTextFormatter();
			mid.add(popularity, 5, 0);

			popularity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 10000, Integer.parseInt("-1")));
			popularity.setEditable(true);

			EventHandler<KeyEvent> enterKeyEventHandler;

			enterKeyEventHandler = new EventHandler<KeyEvent>()
			{
				@Override
				public void handle(final KeyEvent event)
				{
					try
					{
						Integer.parseInt(popularity.getEditor().textProperty().get());
					}
					catch (NumberFormatException e)
					{
						popularity.getEditor().textProperty().set("-1");
					}
				}
			};

			// note: use KeyEvent.KEY_PRESSED, because KeyEvent.KEY_TYPED is to late, spinners
			// SpinnerValueFactory reached new value before key released an SpinnerValueFactory will
			// throw an exception
			popularity.getEditor().addEventHandler(KeyEvent.KEY_RELEASED, enterKeyEventHandler);

			saveButton = new Button("Save");

			/**
			 * Handles the button press event.
			 */
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
	 * Saves data to database.
	 */

	public void setData(final Product product)
	{
		nameField.setText(product.getName());
		brandList.getSelectionModel().select(product.getBrand());
		categoryList.getSelectionModel().select(product.getCategory());
		popularity.getEditor().setText(String.valueOf(product.getPopularity()));
	}
}
