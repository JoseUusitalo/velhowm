package velho.view;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import velho.controller.DatabaseController;
import velho.controller.ProductController;
import velho.model.Product;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.model.interfaces.GenericView;
import velho.view.components.TableCellDeleteButton;

/**
 * @author Edward Puustinen
 */
public class ProductTabView implements GenericView
{
	/**
	 * ProductCntroller neeeded when saving to database
	 */
	private final ProductController productController;

	/**
	 * Makes the Categories tab call for table and make it viewable
	 */
	private final TableView<Object> table;

	/**
	 * this is a VBox we use like a grid
	 */
	private VBox vbox;

	/**
	 * Makes the Categories and ObservableList
	 */
	private ObservableList<Object> data = DatabaseController.getAllProducts();

	private ObservableList<Object> brandList;

	private ObservableList<Object> categoryList;

	/**
	 * Adds info to Product Controller about brands
	 *
	 * @param productController Product Controller handles the database work
	 * @param uiController links UIController to the productController
	 */
	public ProductTabView(final ProductController productController)
	{
		this.productController = productController;
		this.table = new TableView<Object>();
	}

	/**
	 * VBox grid view make it visible
	 *
	 * @param observableList
	 *
	 * @param categoryLiset
	 *
	 * @return the VBox
	 */
	public VBox getView(final ObservableList<Object> brandsList, final ObservableList<Object> categoriesList)
	{
		if (vbox == null)
		{
			this.brandList = brandsList;
			this.categoryList = categoriesList;

			HBox hb = new HBox();

			table.setEditable(true);

			table.setItems(data);

			final Callback<TableColumn<Object, Object>, TableCell<Object, Object>> cellFactory = (final TableColumn<Object, Object> p) -> new EditingCell();
			final TableColumn<Object, Object> nameColumn = new TableColumn<Object, Object>("Name");

			nameColumn.setMinWidth(100);
			nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
			nameColumn.setCellFactory(cellFactory);

			nameColumn.setOnEditCommit((final CellEditEvent<Object, Object> event) ->
			{
				final Product editProduct = ((Product) event.getTableView().getItems().get(event.getTablePosition().getRow()));
				editProduct.setName(event.getNewValue().toString());
				DatabaseController.saveOrUpdate(editProduct);
			});
			table.getColumns().add(nameColumn);

			ObservableList<Object> cbValues = DatabaseController.getAllProductBrands();
			TableColumn<Object, Object> brand = new TableColumn<Object, Object>("Brand");

			brand.setCellValueFactory(new PropertyValueFactory<>("brand"));
			brand.setCellFactory(ComboBoxTableCell.forTableColumn(cbValues));
			brand.setOnEditCommit((final CellEditEvent<Object, Object> t) ->
			{
				final Product editProduct = ((Product) t.getTableView().getItems().get(t.getTablePosition().getRow()));
				editProduct.setBrand((ProductBrand) t.getNewValue());
				DatabaseController.saveOrUpdate(editProduct);
			});
			table.getColumns().add(brand);

			ObservableList<Object> cbCategoryValues = DatabaseController.getAllProductCategories();
			TableColumn<Object, Object> category = new TableColumn<Object, Object>("Category");

			category.setCellValueFactory(new PropertyValueFactory<>("category"));
			category.setCellFactory(ComboBoxTableCell.forTableColumn(cbCategoryValues));
			category.setOnEditCommit((final CellEditEvent<Object, Object> t) ->
			{
				final Product editProduct = ((Product) t.getTableView().getItems().get(t.getTablePosition().getRow()));
				editProduct.setCategory((ProductCategory) t.getNewValue());
				DatabaseController.saveOrUpdate(editProduct);
			});
			table.getColumns().add(category);

			final TableColumn<Object, String> deleteColumn = new TableColumn<Object, String>("");
			deleteColumn.setCellValueFactory(new PropertyValueFactory<Object, String>(""));
			deleteColumn.setSortType(TableColumn.SortType.ASCENDING);

			deleteColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object, String>, ObservableValue<String>>()
			{
				@Override
				public ObservableValue<String> call(final TableColumn.CellDataFeatures<Object, String> p)
				{
					return new SimpleStringProperty(p.getValue(), "Delete");
				}
			});

			deleteColumn.setCellFactory(new Callback<TableColumn<Object, String>, TableCell<Object, String>>()
			{
				@Override
				public TableCell<Object, String> call(final TableColumn<Object, String> p)
				{
					final TableCellDeleteButton button = new TableCellDeleteButton(productController, "Delete");
					button.setAlignment(Pos.CENTER);
					return button;
				}
			});
			table.getColumns().add(deleteColumn);

			final Label productLabel = new Label("Product Name: ");
			final TextField productTextField = new TextField();

			final Label brandLabel = new Label("Brand: ");
			final ComboBox<Object> brandItem = new ComboBox<Object>();
			brandItem.getItems().addAll(brandsList);
			brandItem.getSelectionModel().selectFirst();

			final Label categoryLabel = new Label("Category: ");
			final ComboBox<Object> categoryItem = new ComboBox<Object>();
			categoryItem.getItems().addAll(categoriesList);
			categoryItem.getSelectionModel().selectFirst();

			final Button addButton = new Button("Create");
			addButton.setOnAction((final ActionEvent e) ->
			{
				final Product saveProduct = new Product(productTextField.getText(), (ProductBrand) brandItem.getValue(),
						(ProductCategory) categoryItem.getValue());
				System.out.println("New product: " + saveProduct);
				DatabaseController.saveOrUpdate(saveProduct);
			});

			hb.getChildren().addAll(productLabel, productTextField, brandLabel, brandItem, categoryLabel, categoryItem, addButton);
			hb.setSpacing(10);
			hb.setAlignment(Pos.CENTER_LEFT);

			vbox = new VBox();
			vbox.setSpacing(5);
			vbox.setPadding(new Insets(10, 0, 0, 10));
			vbox.getChildren().addAll(table, hb);

		}
		return vbox;
	}

	/**
	 * Enables editing a cell, nameley the textField
	 *
	 * @author Edward
	 */
	class EditingCell extends TableCell<Object, Object>
	{

		private TextField textField;

		public EditingCell()
		{
		}

		@Override
		public void startEdit()
		{
			if (!isEmpty())
			{
				super.startEdit();
				createTextField();
				setText(null);
				setGraphic(textField);
				textField.selectAll();
			}
		}

		@Override
		public void cancelEdit()
		{
			super.cancelEdit();

			setText(getItem().toString());
			setGraphic(null);
		}

		@Override
		public void updateItem(final Object item, final boolean empty)
		{
			super.updateItem(item, empty);

			if (empty)
			{
				setText(null);
				setGraphic(null);
			}
			else
			{
				if (isEditing())
				{
					if (textField != null)
					{
						textField.setText(getString());
					}
					setText(null);
					setGraphic(textField);
				}
				else
				{
					setText(getString());
					setGraphic(null);
				}
			}
		}

		private void createTextField()
		{
			textField = new TextField(getString());
			textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
			textField.focusedProperty().addListener((final ObservableValue<? extends Boolean> arg0, final Boolean arg1, final Boolean arg2) ->
			{
				if (!arg2)
				{
					commitEdit(textField.getText());
				}
			});
		}

		private String getString()
		{
			return getItem() == null ? "" : getItem().toString();
		}
	}

	class SpinnerCell extends TableCell<Object, Object>
	{

		private Spinner<Integer> spinner;

		public SpinnerCell()
		{
		}

		@Override
		public void startEdit()
		{
			if (!isEmpty())
			{
				super.startEdit();
				createSpinner();
				setText("");
				setGraphic(spinner);
			}
		}

		@Override
		public void cancelEdit()
		{
			super.cancelEdit();

			setText(getItem().toString());
			setGraphic(null);
		}

		@Override
		public void updateItem(final Object item, final boolean empty)
		{
			super.updateItem(item, empty);

			if (empty)
			{
				setText(null);
				setGraphic(null);
			}
			else
			{
				if (isEditing())
				{
					if (spinner != null)
					{
						spinner.getValueFactory().setValue(getValue());
					}
					setText(null);
					setGraphic(spinner);
				}
				else
				{

					setGraphic(null);

					if (getItem() == null)
					{
						setText("");
					}
					else
					{
						setText(getValue().toString());
					}
				}
			}
		}

		private void createSpinner()
		{
			spinner = new Spinner<Integer>();
			spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, getValue()));
			spinner.setEditable(true);
			spinner.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
			spinner.focusedProperty().addListener((final ObservableValue<? extends Boolean> arg0, final Boolean arg1, final Boolean arg2) ->
			{
				if (!arg2)
				{
					commitEdit(spinner.getValue());
				}
			});
		}

		private Integer getValue()
		{
			return (Integer) getItem();
		}
	}

	class DatePickerCell extends TableCell<Object, Object>
	{

		private DatePicker datePicker;

		public DatePickerCell()
		{
		}

		@Override
		public void startEdit()
		{
			if (!isEmpty())
			{
				super.startEdit();
				createTextField();
				setText("");
				setGraphic(datePicker);
			}
		}

		@Override
		public void cancelEdit()
		{
			super.cancelEdit();

			setText(getItem().toString());
			setGraphic(null);
		}

		@Override
		public void updateItem(final Object item, final boolean empty)
		{
			super.updateItem(item, empty);

			if (empty)
			{
				setText(null);
				setGraphic(null);
			}
			else
			{
				if (isEditing())
				{
					if (datePicker != null)
					{
						datePicker.setValue(getDateValue());
					}
					setText(null);
					setGraphic(datePicker);
				}
				else
				{

					setGraphic(null);

					if (getItem() == null)
					{
						setText("");
					}
					else
					{
						setText(getDateValue().toString());
					}
				}
			}
		}

		private void createTextField()
		{
			datePicker = new DatePicker(getDateValue());
			datePicker.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
			datePicker.focusedProperty().addListener((final ObservableValue<? extends Boolean> arg0, final Boolean arg1, final Boolean arg2) ->
			{
				if (!arg2)
				{
					commitEdit(datePicker.getValue());
				}
			});
		}

		private LocalDate getDateValue()
		{
			if (getItem() == null)
				return null;

			if (getItem() instanceof LocalDate)
				return (LocalDate) getItem();

			Date aDate = ((Date) getItem());
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate date = LocalDate.parse(df.format(aDate), formatter);
			return date;
		}
	}

	@Override
	public void recreate()
	{
		vbox = null;
		// TODO: Use old data.
		getView(brandList, categoryList);
	}

	@Override
	public void destroy()
	{
		vbox = null;
	}
}
