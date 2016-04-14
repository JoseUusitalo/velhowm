package velho.view;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import velho.controller.DatabaseController;
import velho.controller.ProductController;
import velho.controller.UIController;
import velho.model.Product;
import velho.model.ProductBrand;
import velho.model.ProductType;
import velho.model.exceptions.NoDatabaseLinkException;

public class AddCategoryView
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

	private Label categoryLabel;

	/**
	 * A combobox for brand list.
	 */
	private ComboBox<Object> brandList;

	/**
	 * A combobox for category list.
	 */
	private ComboBox<Object> typeList;

	/**
	 * A spinner that shows the product popularity.
	 */
	private Spinner<Integer> popularity;

	// *************************************************

	/**
	 * Makes the Brands tab call for table and make it viewable
	 */
	private final TableView<Object> table = new TableView<Object>();

	/**
	 * making the Category an observable list
	 */
	private ObservableList<Object> data = DatabaseController.getAllProductCategories();

	// *************************************************

	/**
	 * @param productController
	 * @param uiController
	 */
	public AddCategoryView(final ProductController productController, final UIController uiController)
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

			// *************************************************

			table.setEditable(true);

			TableColumn<Object, String> nameColumn = new TableColumn<Object, String>("Name");

			nameColumn.setMinWidth(100);
			nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

			nameColumn.setOnEditCommit((final CellEditEvent<Object, String> t) ->
			{
				final ProductBrand editBrand = ((ProductBrand) t.getTableView().getItems().get(t.getTablePosition().getRow()));
				editBrand.setName(t.getNewValue());
				productController.saveBrand(editBrand);
			});
			table.setItems(data);
			table.getColumns().add(nameColumn);

			// *************************************************

			final GridPane grid = new GridPane();

			databaseID = new Spinner<Integer>();
			databaseID.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, Integer.parseInt("-1")));

			categoryLabel = new Label();
			categoryLabel.setText("Category: ");
			categoryLabel.setAlignment(Pos.BASELINE_RIGHT);
			categoryLabel.setMinWidth(MainWindow.WINDOW_WIDTH / 10);
			grid.add(categoryLabel, 1, 0);

			nameField = new TextField();
			nameField.setPromptText("Category");
			nameField.setPrefWidth(MainWindow.WINDOW_WIDTH / 5);
			nameField.setMaxWidth(Double.MAX_VALUE);
			grid.add(nameField, 2, 0);

			typeList = new ComboBox<Object>();
			// TODO make it so that you dont need to press enter
			typeList.setPromptText("Product Type");
			typeList.getItems().addAll(DatabaseController.getAllProductTypes());
			typeList.setMaxWidth(Double.MAX_VALUE);

			typeList.getSelectionModel().selectFirst();
			grid.add(typeList, 3, 0);

			Button saveButton = new Button("Save");

			saveButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					ProductType categoryType = (ProductType) typeList.valueProperty().getValue();
					productController.saveCategory(nameField.getText(), categoryType);
				}
			});
			grid.add(saveButton, 4, 0);

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
		typeList.getSelectionModel().select(product.getCategory());
		popularity.getValueFactory().setValue(product.getPopularity());
	}
}
