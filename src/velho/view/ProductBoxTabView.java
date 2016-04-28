package velho.view;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import velho.controller.DatabaseController;
import velho.controller.ProductController;
import velho.controller.UIController;
import velho.model.ProductBox;
import velho.model.ProductCategory;

public class ProductBoxTabView
{
	/**
	 * ProductCntroller neeeded when saving to database
	 */
	private ProductController productController;

	/**
	 * Makes the Categories tab call for table and make it viewable
	 */
	private final TableView<Object> table = new TableView<Object>();

	/**
	 * this is a VBox we use like a grid
	 */
	private VBox vbox;

	/**
	 * Makes the Categories and ObservableList
	 */
	private ObservableList<Object> data = DatabaseController.getAllProductBoxes();

	/**
	 * Adds info to Product Controller about brands
	 *
	 * @param productController Product Controller handles the database work
	 * @param uiController links UIController to the productController
	 */
	public ProductBoxTabView(final ProductController productController, final UIController uiController)
	{
		this.productController = productController;
	}

	/**
	 * VBox grid view make it visible
	 *
	 * @return the VBox
	 */
	public VBox getView()
	{
		if (vbox == null)
		{
			HBox hb = new HBox();

			table.setEditable(true);

			Callback<TableColumn<Object, Object>, TableCell<Object, Object>> cellFactory = (final TableColumn<Object, Object> p) -> new EditingCell();

			TableColumn<Object, Object> maxSizeColumn = new TableColumn<Object, Object>("Max Size");

			maxSizeColumn.setMinWidth(100);
			maxSizeColumn.setCellValueFactory(new PropertyValueFactory<>("maxSize"));
			maxSizeColumn.setCellFactory(cellFactory);

			maxSizeColumn.setOnEditCommit((final CellEditEvent<Object, Object> t) ->
			{
				final ProductCategory editCategory = ((ProductCategory) t.getTableView().getItems().get(t.getTablePosition().getRow()));
				editCategory.setName(t.getNewValue().toString());
				productController.saveProductCategory(editCategory);
			});
			table.setItems(data);
			table.getColumns().add(maxSizeColumn);

			Callback<TableColumn<Object, Object>, TableCell<Object, Object>> pickerFactory = (final TableColumn<Object, Object> p) -> new DatePickerCell();
			TableColumn<Object, Object> datePickerColumn = new TableColumn<>("Expiration Date");
			datePickerColumn.setMinWidth(150);
			datePickerColumn.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));
			datePickerColumn.setCellFactory(pickerFactory);
			datePickerColumn.setOnEditCommit((final CellEditEvent<Object, Object> t) ->
			{
				final ProductBox productBox = ((ProductBox) t.getTableView().getItems().get(t.getTablePosition().getRow()));

				long saveDate = ((LocalDate) t.getNewValue()).toEpochDay();
				Date date = new Date(saveDate);
				productBox.setExpirationDate(date);
				System.out.println("new exp date " + productBox.getExpirationDate());
				productController.saveProductBox(productBox);
			});
			table.getColumns().add(datePickerColumn);

			final TextField productBoxName = new TextField();
			productBoxName.setPromptText("Product box Name");
			productBoxName.setMaxWidth(maxSizeColumn.getPrefWidth());
			final Button addButton = new Button("Create");
			addButton.setOnAction((final ActionEvent e) ->
			{
				final ProductCategory saveCategory = new ProductCategory(productBoxName.getText());
				data.add(saveCategory);
				productBoxName.clear();
				productController.saveProductCategory(saveCategory);
			});

			final DatePicker datePickerBox = new DatePicker();

			hb.getChildren().addAll(productBoxName, addButton);
			hb.setSpacing(3);

			vbox = new VBox();
			vbox.setSpacing(5);
			vbox.setPadding(new Insets(10, 0, 0, 10));
			vbox.getChildren().addAll(table, hb);

		}
		return vbox;
	}

	/**
	 *
	 * @author Edward Enables editing a cell, nameley the textField
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
}
