package velho.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
import velho.model.ProductCategory;
import velho.model.ProductType;
import velho.model.interfaces.GenericView;
import velho.view.components.TableCellDeleteButton;

/**
 * @author Edward Puustinen
 */
public class CategoriesTabView implements GenericView
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
	 * Makes the Categories and ObservableList
	 */
	private final ObservableList<Object> categories;

	/**
	 * Adds info to Product Controller about brands
	 *
	 * @param productController Product Controller handles the database work
	 * @param uiController links UIController to the productController
	 */
	public CategoriesTabView(final ObservableList<Object> categories)
	{
		this.table = new TableView<Object>();
		this.categories = categories;
	}

	/**
	 * VBox grid view make it visible
	 *
	 * @param productList
	 *
	 * @return the VBox
	 */
	public VBox getView()
	{
		if (vbox == null)
		{
			table.getColumns().clear();

			HBox hbox = new HBox();

			table.setEditable(true);
			table.getItems().clear();
			table.setItems(categories);

			final Callback<TableColumn<Object, Object>, TableCell<Object, Object>> cellFactory = (final TableColumn<Object, Object> col) -> new EditingCell();
			final TableColumn<Object, Object> nameColumn = new TableColumn<Object, Object>("Name");

			nameColumn.setMinWidth(100);
			nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
			nameColumn.setCellFactory(cellFactory);

			nameColumn.setOnEditCommit((final CellEditEvent<Object, Object> event) ->
			{
				final ProductCategory editCategory = ((ProductCategory) event.getTableView().getItems().get(event.getTablePosition().getRow()));
				editCategory.setName(event.getNewValue().toString());
				ProductController.getInstance().saveProductCategory(editCategory);
			});
			table.getColumns().add(nameColumn);

			final ObservableList<Object> cbValues = DatabaseController.getInstance().getAllProductTypes();
			final TableColumn<Object, Object> comboBoxColumn = new TableColumn<>(LocalizationController.getInstance().getString("typesComboboxTabName"));
			comboBoxColumn.setMinWidth(150);
			comboBoxColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
			comboBoxColumn.setCellFactory(ComboBoxTableCell.forTableColumn(cbValues));
			comboBoxColumn.setOnEditCommit((final CellEditEvent<Object, Object> event) ->
			{
				final ProductCategory editCategory = ((ProductCategory) event.getTableView().getItems().get(event.getTablePosition().getRow()));
				editCategory.setType((ProductType) event.getNewValue());
				ProductController.getInstance().saveProductCategory(editCategory);
			});
			table.getColumns().add(comboBoxColumn);

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
				public TableCell<Object, String> call(final TableColumn<Object, String> tcolumn)
				{
					final TableCellDeleteButton button = new TableCellDeleteButton(ProductController.getInstance(),
							LocalizationController.getInstance().getString("buttonDelete"));
					button.setAlignment(Pos.CENTER);
					return button;
				}
			});
			table.getColumns().add(deleteColumn);

			final Label categoryLabel = new Label(LocalizationController.getInstance().getString("categoryNameLabel"));
			final TextField categoryName = new TextField();
			categoryName.setPromptText(LocalizationController.getInstance().getString("categoryNamePromtText"));
			categoryName.setMaxWidth(nameColumn.getPrefWidth());
			final Button addButton = new Button(LocalizationController.getInstance().getString("buttonCreate"));

			final Label typeLabel = new Label(LocalizationController.getInstance().getString("categoryTypeLabel"));
			final ComboBox<Object> categoryType = new ComboBox<Object>();
			categoryType.getItems().addAll(cbValues);
			categoryType.getSelectionModel().selectFirst();

			hbox.getChildren().addAll(categoryLabel, categoryName, typeLabel, categoryType, addButton);
			hbox.setSpacing(10);
			hbox.setAlignment(Pos.CENTER_LEFT);

			addButton.setOnAction((final ActionEvent event) ->
			{
				final ProductCategory saveCategory = new ProductCategory(categoryName.getText(), (ProductType) categoryType.getValue());
				categoryName.clear();
				ProductController.getInstance().saveProductCategory(saveCategory);
			});

			vbox = new VBox();
			vbox.setSpacing(5);
			vbox.setPadding(new Insets(10, 0, 0, 10));
			vbox.getChildren().addAll(table, hbox);

			UIController.getInstance().recordView(this);
		}
		return vbox;
	}

	/**
	 *
	 * @author Edward
	 * Enables editing a cell, nameley the textField
	 */
	class EditingCell extends TableCell<Object, Object>
	{

		private TextField textField;

		public EditingCell()
		{
			// Silencing PMD.
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

	@Override
	public void recreate()
	{
		vbox = null;
		getView();
	}

	@Override
	public void destroy()
	{
		vbox = null;
	}
}
