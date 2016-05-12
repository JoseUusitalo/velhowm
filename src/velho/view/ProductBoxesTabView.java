package velho.view;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
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
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
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
import velho.model.ProductBox;
import velho.model.interfaces.GenericView;
import velho.view.components.TableCellDeleteButton;

/**
 * @author Edward Puustinen
 */
public class ProductBoxesTabView implements GenericView
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
	private ObservableList<Object> data = DatabaseController.getInstance().getAllProductBoxes();

	private ObservableList<Object> pList;

	/**
	 * Adds info to Product Controller about brands
	 *
	 * @param productController Product Controller handles the database work
	 * @param uiController links UIController to the productController
	 */
	public ProductBoxesTabView(final ProductController productController)
	{
		this.productController = productController;
		this.table = new TableView<Object>();
	}

	/**
	 * VBox grid view make it visible
	 *
	 * @param productList
	 *
	 * @return the VBox
	 */
	public VBox getView(final ObservableList<Object> productList)
	{
		if (vbox == null)
		{
			table.getColumns().clear();

			pList = productList;
			HBox hb = new HBox();

			table.setEditable(true);

			table.setItems(data);

			Callback<TableColumn<Object, Object>, TableCell<Object, Object>> cellFactory = (final TableColumn<Object, Object> p) -> new SpinnerCell();

			TableColumn<Object, Object> sizeColumn = new TableColumn<Object, Object>(LocalizationController.getInstance().getString("publicRemovalTableHeaderSize"));

			sizeColumn.setMinWidth(100);
			sizeColumn.setCellValueFactory(new PropertyValueFactory<>("productCount"));
			sizeColumn.setCellFactory(cellFactory);

			sizeColumn.setOnEditCommit((final CellEditEvent<Object, Object> t) ->
			{
				final ProductBox productBox = ((ProductBox) t.getTableView().getItems().get(t.getTablePosition().getRow()));
				productBox.setProductCount((int) t.getNewValue());
				productController.saveProductBox(productBox);
			});
			table.getColumns().add(sizeColumn);

			Callback<TableColumn<Object, Object>, TableCell<Object, Object>> cellFactory2 = (final TableColumn<Object, Object> p) -> new SpinnerCell();

			TableColumn<Object, Object> maxSizeColumn = new TableColumn<Object, Object>(LocalizationController.getInstance().getString("maxSizeTabName"));

			maxSizeColumn.setMinWidth(100);
			maxSizeColumn.setCellValueFactory(new PropertyValueFactory<>("maxSize"));
			maxSizeColumn.setCellFactory(cellFactory2);

			maxSizeColumn.setOnEditCommit((final CellEditEvent<Object, Object> t) ->
			{
				final ProductBox productBox = ((ProductBox) t.getTableView().getItems().get(t.getTablePosition().getRow()));
				productBox.setMaxSize((int) t.getNewValue());
				productController.saveProductBox(productBox);
			});
			table.getColumns().add(maxSizeColumn);

			ObservableList<Object> cbValues = DatabaseController.getInstance().getAllProducts();
			TableColumn<Object, Object> product = new TableColumn<Object, Object>(LocalizationController.getInstance().getString("productProduct"));

			product.setCellValueFactory(new PropertyValueFactory<>("product"));
			product.setCellFactory(ComboBoxTableCell.forTableColumn(cbValues));
			product.setOnEditCommit((final CellEditEvent<Object, Object> t) ->
			{
				final ProductBox editProductBox = ((ProductBox) t.getTableView().getItems().get(t.getTablePosition().getRow()));
				editProductBox.setProduct((Product) t.getNewValue());
				productController.saveProductBox(editProductBox);
			});
			table.getColumns().add(product);

			Callback<TableColumn<Object, Object>, TableCell<Object, Object>> pickerFactory = (final TableColumn<Object, Object> p) -> new DatePickerCell();
			TableColumn<Object, Object> datePickerColumn = new TableColumn<>(LocalizationController.getInstance().getString("expirationDateExpirationDate"));
			datePickerColumn.setMinWidth(150);
			datePickerColumn.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));
			datePickerColumn.setCellFactory(pickerFactory);
			datePickerColumn.setOnEditCommit((final CellEditEvent<Object, Object> t) ->
			{
				final ProductBox productBox = ((ProductBox) t.getTableView().getItems().get(t.getTablePosition().getRow()));

				LocalDate saveDate = ((LocalDate) t.getNewValue());

				Date date = null;
				if (saveDate != null)
				{
					date = Date.from(saveDate.atTime(0, 0).toInstant(ZoneOffset.of("Z")));
				}
				productBox.setExpirationDate(date);
				productController.saveProductBox(productBox);
			});
			table.getColumns().add(datePickerColumn);

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
					final TableCellDeleteButton button = new TableCellDeleteButton(productController, (LocalizationController.getInstance().getString("buttonDelete")));
					button.setAlignment(Pos.CENTER);
					return button;
				}
			});
			table.getColumns().add(deleteColumn);

			final Label maxSizeLabel = new Label(LocalizationController.getInstance().getString("maxProductsLabel"));
			final Spinner<Integer> productBoxMaxSize = new Spinner<Integer>();
			productBoxMaxSize.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE));
			productBoxMaxSize.setMaxWidth(maxSizeColumn.getPrefWidth());

			final Label sizeLabel = new Label(LocalizationController.getInstance().getString("productCountLabel"));
			final Spinner<Integer> productBoxSize = new Spinner<Integer>();
			final IntegerSpinnerValueFactory sizeFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, productBoxMaxSize.getValue());
			sizeFactory.maxProperty().bind(productBoxMaxSize.getValueFactory().valueProperty());
			productBoxSize.setValueFactory(sizeFactory);
			productBoxSize.setMaxWidth(sizeColumn.getPrefWidth());

			final Label productLabel = new Label(LocalizationController.getInstance().getString("productPromtTextAddProductView"));
			final ComboBox<Object> productItem = new ComboBox<Object>();
			productItem.getItems().clear();
			productItem.getItems().addAll(pList);
			productItem.getSelectionModel().selectFirst();
			productItem.setMaxWidth(product.getPrefWidth());

			final Label calendarLabel = new Label(LocalizationController.getInstance().getString("expirationDateLabel"));
			final DatePicker expirationDate = new DatePicker();

			final Button addButton = new Button(LocalizationController.getInstance().getString("buttonCreate"));
			addButton.setOnAction((final ActionEvent e) ->
			{
				Date date = null;
				if (expirationDate.getValue() != null)
					date = Date.from(expirationDate.getValue().atTime(0, 0).toInstant(ZoneOffset.of("Z")));
				final ProductBox saveProductBox = new ProductBox((Product) productItem.getValue(), productBoxMaxSize.getValue(), productBoxSize.getValue(),
						date);
				System.out.println("New product box: " + saveProductBox);
				productController.saveProductBox(saveProductBox);
			});

			hb.getChildren().addAll(sizeLabel, productBoxSize, maxSizeLabel, productBoxMaxSize, productLabel, productItem, calendarLabel, expirationDate,
					addButton);
			hb.setSpacing(3);
			hb.setSpacing(10);
			hb.setAlignment(Pos.CENTER_LEFT);

			vbox = new VBox();
			vbox.setSpacing(5);
			vbox.setPadding(new Insets(10, 0, 0, 10));
			vbox.getChildren().addAll(table, hb);

			UIController.recordView(this);
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
		getView(pList);
	}

	@Override
	public void destroy()
	{
		vbox = null;
	}
}
