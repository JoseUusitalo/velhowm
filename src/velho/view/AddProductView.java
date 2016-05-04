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
import velho.controller.LocalizationController;
import velho.controller.ProductController;
import velho.controller.UIController;
import velho.model.Product;
import velho.model.interfaces.GenericView;

/**
 * Creates tab for "Product Edit View".
 *
 * @author Edward Puustinen
 */
public class AddProductView implements GenericView
{
	/**
	 * The product controller.
	 */
	private final ProductController productController;

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
	 * A label for Add Product
	 */
	private Label productLabel;

	/**
	 * A combobox for brand list.
	 */
	private ComboBox<Object> brandList;

	/**
	 * A combobox for category list.
	 */
	private ComboBox<Object> categoryList;

	/**
	 * The {@link UIController}.
	 */
	private final UIController uiController;

	/**
	 * @param productController
	 * @param uiController
	 */
	public AddProductView(final ProductController productController, final UIController uiController)
	{
		this.productController = productController;
		this.uiController = uiController;
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

			productLabel = new Label();
			productLabel.setText("Product: ");
			productLabel.setAlignment(Pos.BASELINE_RIGHT);
			productLabel.setMinWidth(MainWindow.WINDOW_WIDTH / 10);
			grid.add(productLabel, 1, 0);

			nameField = new TextField();
			nameField.setPromptText(LocalizationController.getString("promtTextProductName"));
			nameField.setPrefWidth(MainWindow.WINDOW_WIDTH / 5);
			nameField.setMaxWidth(Double.MAX_VALUE);
			grid.add(nameField, 2, 0);

			brandList = new ComboBox<Object>();
			// TODO make it so that you dont need to press enter
			brandList.setPromptText(LocalizationController.getString("promtTextBrand"));
			brandList.getItems().addAll(DatabaseController.getAllProductBrands());
			brandList.setMaxWidth(Double.MAX_VALUE);

			/*
			 * TODO: Fix combobox selection mechanic breaking on the second try
			 * because the selection is converted from
			 * object to string.
			 */
			// brandList.setEditable(true);

			brandList.getSelectionModel().selectFirst();
			grid.add(brandList, 3, 0);

			categoryList = new ComboBox<Object>();
			// TODO make it so that you dont need to press enter
			categoryList.setPromptText(LocalizationController.getString("promtTextCategory"));
			categoryList.getItems().addAll(DatabaseController.getAllProductCategories());
			categoryList.setMaxWidth(Double.MAX_VALUE);

			/*
			 * TODO: Fix combobox selection mechanic breaking on the second try
			 * because the selection is converted from
			 * object to string.
			 */
			// categoryList.setEditable(true);

			categoryList.getSelectionModel().selectFirst();
			grid.add(categoryList, 4, 0);

			final Button cancelButton = new Button(LocalizationController.getString("cancelButtonBackToList"));

			cancelButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					// productController.showList();
					uiController.selectTab(LocalizationController.getString("productListTabName"));
				}
			});
			grid.add(cancelButton, 6, 0);

			Button saveButton = new Button(LocalizationController.getString("saveButton"));

			saveButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					Object brand = brandList.valueProperty().getValue();
					Object category = categoryList.valueProperty().getValue();

					final Product newProduct = productController.saveProduct(databaseID.getValueFactory().getValue().intValue(), nameField.getText(), brand, category);
					if (editProduct)
						productController.showProductView(newProduct);
				}
			});
			grid.add(saveButton, 5, 0);

			grid.setHgap(10);
			grid.getStyleClass().add("standard-padding");

			bPane.setCenter(grid);
			UIController.recordView(this);
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
	}

	@Override
	public void recreate()
	{
		bPane = null;
		getView(false);

	}

	@Override
	public void destroy()
	{
		bPane = null;
	}
}
