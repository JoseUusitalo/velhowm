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
import velho.controller.ProductController;
import velho.controller.UIController;
import velho.model.Product;
import velho.model.exceptions.NoDatabaseLinkException;

public class AddBrandView
{

	/**
	 * The product controller.
	 */
	private ProductController productController;

	/**
	 * The border panel.
	 */
	private BorderPane bPane;

	/**
	 * The hidden database ID spinner.
	 */
	private Spinner<Integer> databaseID;

	/**
	 * A text field.
	 */
	private TextField nameField;

	/**
	 * A label for brand
	 */
	private Label brandLabel;
	/**
	 * A combobox for brand list.
	 */
	private ComboBox<Object> brandList;

	/**
	 * A combobox for category list.
	 */
	private ComboBox<Object> categoryList;

	/**
	 * A spinner that shows the product popularity.
	 */
	private Spinner<Integer> popularity;

	/**
	 * @param productController
	 * @param uiController
	 */
	public AddBrandView(final ProductController productController, final UIController uiController)
	{
		this.productController = productController;
	}

	/**
	 * Creates BorderPane for the get Add Product tab.
	 *
	 * @param editProduct the product to modify
	 *
	 * @return the bPane
	 * @throws NoDatabaseLinkException to get the data
	 */
	public BorderPane getView(final boolean editProduct)
	{
		if (bPane == null)
		{
			bPane = new BorderPane();

			final GridPane grid = new GridPane();

			databaseID = new Spinner<Integer>();
			databaseID.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, Integer.parseInt("-1")));

			brandLabel = new Label();
			brandLabel.setText("Brand: ");
			brandLabel.setAlignment(Pos.BASELINE_RIGHT);
			brandLabel.setMinWidth(MainWindow.WINDOW_WIDTH / 10);
			grid.add(brandLabel, 1, 0);

			nameField = new TextField();
			nameField.setPromptText("Brand name");
			nameField.setPrefWidth(MainWindow.WINDOW_WIDTH / 5);
			nameField.setMaxWidth(Double.MAX_VALUE);
			grid.add(nameField, 2, 0);

			Button saveButton = new Button("Save");

			saveButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					productController.saveBrand(nameField.getText());
				}
			});
			grid.add(saveButton, 3, 0);

			grid.setHgap(10);
			grid.getStyleClass().add("standard-padding");

			bPane.setCenter(grid);
		}
		return bPane;
	}

	/**
	 * Saves data to database.
	 *
	 * @param product the product to view
	 */
	public void setViewData(final Product product)
	{
		databaseID.getValueFactory().setValue(product.getDatabaseID());
		nameField.setText(product.getName());
		brandList.getSelectionModel().select(product.getBrand());
		categoryList.getSelectionModel().select(product.getCategory());
		popularity.getValueFactory().setValue(product.getPopularity());
	}
}
