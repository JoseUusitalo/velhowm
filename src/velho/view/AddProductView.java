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
	private Spinner<Integer> popularity;

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

			final GridPane grid = new GridPane();

			nameField = new TextField();
			nameField.setPromptText("Product name");
			nameField.setPrefWidth(MainWindow.WINDOW_WIDTH / 5);
			nameField.setMaxWidth(Double.MAX_VALUE);
			grid.add(nameField, 1, 0);

			brandList = new ComboBox<Object>();
			// TODO make it so that you dont need to press enter
			brandList.setPromptText("Brand");
			brandList.getItems().addAll(DatabaseController.getAllProductBrands());
			brandList.setMaxWidth(Double.MAX_VALUE);
			brandList.getSelectionModel().selectFirst();
			brandList.setEditable(true);
			grid.add(brandList, 2, 0);

			categoryList = new ComboBox<Object>();
			categoryList.setEditable(true);
			// TODO make it so that you dont need to press enter
			categoryList.setPromptText("Category");
			categoryList.getItems().addAll(DatabaseController.getAllProductCategories());
			categoryList.setMaxWidth(Double.MAX_VALUE);
			categoryList.getSelectionModel().selectFirst();
			grid.add(categoryList, 3, 0);

			Label popularityLabel = new Label("Popularity: ");
			popularityLabel.setAlignment(Pos.CENTER_RIGHT);
			grid.add(popularityLabel, 4, 0);

			popularity = new Spinner<Integer>();
			// popularity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 10000));
			popularity.setEditable(true);
			popularity.getEditor().getTextFormatter();
			grid.add(popularity, 5, 0);

			popularity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 10000, Integer.parseInt("-1")));
			popularity.setEditable(true);

			final EventHandler<KeyEvent> keyboardHandler = new EventHandler<KeyEvent>()
			{
				@Override
				public void handle(final KeyEvent event)
				{
					try
					{
						if (Integer.parseInt(popularity.getEditor().textProperty().get()) < -1)
						{
							throw new NumberFormatException();
						}
					}
					catch (NumberFormatException e)
					{
						popularity.getEditor().textProperty().set("-1");
					}
				}
			};

			popularity.getEditor().addEventHandler(KeyEvent.KEY_RELEASED, keyboardHandler);

			final Button cancelButton = new Button("Cancel");

			cancelButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					productController.showList();
				}
			});

			grid.add(cancelButton, 6, 0);

			Button saveButton = new Button("Save");

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
			grid.add(saveButton, 7, 0);

			grid.setHgap(10);
			grid.getStyleClass().add("standard-padding");

			bPane.setCenter(grid);
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
