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
import velho.controller.LocalizationController;
import velho.controller.ProductController;
import velho.controller.UIController;
import velho.controller.database.DatabaseController;
import velho.model.Product;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.model.interfaces.GenericView;
import velho.view.components.TableCellDeleteButton;

/**
 * @author Edward Puustinen &amp; Jose Uusitalo
 */
public class ProductTabView implements GenericView
{
	/**
	 * Makes the Categories tab call for table and make it viewable
	 */
	private final TableView<Object> table;

	/**
	 * this is a VBox we use like a grid
	 */
	private VBox vbox;

	/**
	 * An observable list of database products.
	 */
	private final ObservableList<Object> productList;

	/**
	 * An observable list of database brands.
	 */
	private ObservableList<Object> brandList;

	/**
	 * An observable list of database categories.
	 */
	private ObservableList<Object> categoryList;

	/**
	 * @param products
	 */
	public ProductTabView(final ObservableList<Object> products)
	{
		this.productList = products;
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

			final HBox hbox = new HBox();

			table.setEditable(true);

			table.setItems(productList);
			table.getColumns().clear();

			final Callback<TableColumn<Object, Object>, TableCell<Object, Object>> cellFactory = (final TableColumn<Object, Object> col) -> new EditingCell();
			final TableColumn<Object, Object> nameColumn = new TableColumn<Object, Object>("Name");

			nameColumn.setMinWidth(100);
			nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
			nameColumn.setCellFactory(cellFactory);

			nameColumn.setOnEditCommit((final CellEditEvent<Object, Object> event) ->
			{
				final Product editProduct = ((Product) event.getTableView().getItems().get(event.getTablePosition().getRow()));
				editProduct.setName(event.getNewValue().toString());
				DatabaseController.getInstance().saveOrUpdate(editProduct);
			});
			table.getColumns().add(nameColumn);

			ObservableList<Object> cbValues = DatabaseController.getInstance().getAllProductBrands();
			TableColumn<Object, Object> brand = new TableColumn<Object, Object>(
					LocalizationController.getInstance().getString("publicProductSearchTableHeaderBrand"));

			brand.setCellValueFactory(new PropertyValueFactory<>("brand"));
			brand.setCellFactory(ComboBoxTableCell.forTableColumn(cbValues));

			brand.setOnEditCommit((final CellEditEvent<Object, Object> event) ->
			{
				final Product editProduct = ((Product) event.getTableView().getItems().get(event.getTablePosition().getRow()));
				editProduct.setBrand((ProductBrand) event.getNewValue());
				DatabaseController.getInstance().saveOrUpdate(editProduct);
			});

			table.getColumns().add(brand);

			ObservableList<Object> cbCategoryValues = DatabaseController.getInstance().getAllProductCategories();
			TableColumn<Object, Object> category = new TableColumn<Object, Object>(
					LocalizationController.getInstance().getString("publicProductSearchTableHeaderCategory"));

			category.setCellValueFactory(new PropertyValueFactory<>("category"));
			category.setCellFactory(ComboBoxTableCell.forTableColumn(cbCategoryValues));
			category.setOnEditCommit((final CellEditEvent<Object, Object> event) ->
			{
				final Product editProduct = ((Product) event.getTableView().getItems().get(event.getTablePosition().getRow()));
				editProduct.setCategory((ProductCategory) event.getNewValue());
				DatabaseController.getInstance().saveOrUpdate(editProduct);
			});
			table.getColumns().add(category);

			final TableColumn<Object, String> deleteColumn = new TableColumn<Object, String>("");
			deleteColumn.setCellValueFactory(new PropertyValueFactory<Object, String>(""));
			deleteColumn.setSortType(TableColumn.SortType.ASCENDING);

			deleteColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object, String>, ObservableValue<String>>()
			{
				@Override
				public ObservableValue<String> call(final TableColumn.CellDataFeatures<Object, String> celldata)
				{
					return new SimpleStringProperty(celldata.getValue(), "Delete");
				}
			});

			deleteColumn.setCellFactory(new Callback<TableColumn<Object, String>, TableCell<Object, String>>()
			{
				@Override
				public TableCell<Object, String> call(final TableColumn<Object, String> col)
				{
					final TableCellDeleteButton button = new TableCellDeleteButton(ProductController.getInstance(),
							LocalizationController.getInstance().getString("publicProductTableDeleteButton"));
					button.setAlignment(Pos.CENTER);
					return button;
				}
			});
			table.getColumns().add(deleteColumn);

			final Label productLabel = new Label(LocalizationController.getInstance().getString("productNameLabel"));
			final TextField productTextField = new TextField();

			final Label brandLabel = new Label(LocalizationController.getInstance().getString("productBrandLabel"));
			final ComboBox<Object> brandItem = new ComboBox<Object>();
			brandItem.getItems().addAll(brandsList);
			brandItem.getSelectionModel().selectFirst();

			final Label categoryLabel = new Label(LocalizationController.getInstance().getString("productCategoryLabel"));
			final ComboBox<Object> categoryItem = new ComboBox<Object>();
			categoryItem.getItems().addAll(categoriesList);
			categoryItem.getSelectionModel().selectFirst();

			final Button addButton = new Button(LocalizationController.getInstance().getString("buttonCreate"));
			addButton.setOnAction((final ActionEvent event) ->
			{
				final Product saveProduct = new Product(productTextField.getText(), (ProductBrand) brandItem.getValue(),
						(ProductCategory) categoryItem.getValue());
				DatabaseController.getInstance().saveOrUpdate(saveProduct);
			});

			hbox.getChildren().addAll(productLabel, productTextField, brandLabel, brandItem, categoryLabel, categoryItem, addButton);
			hbox.setSpacing(10);
			hbox.setAlignment(Pos.CENTER_LEFT);

			vbox = new VBox();
			vbox.setSpacing(5);
			vbox.setPadding(new Insets(10, 0, 0, 10));
			vbox.getChildren().addAll(table, hbox);

			UIController.getInstance().recordView(this);
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

			final Date aDate = ((Date) getItem());
			final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			return LocalDate.parse(dateFormat.format(aDate), formatter);
		}
	}

	@Override
	public void recreate()
	{
		vbox = null;
		getView(brandList, categoryList);
	}

	@Override
	public void destroy()
	{
		vbox = null;
	}
}
