package velho.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import velho.model.ProductType;
import velho.view.components.TableCellDeleteButton;

public class ProductTypesTabView
{
	private ProductController productController;

	private final TableView<Object> table = new TableView<Object>();

	private VBox vbox;

	private ObservableList<Object> data = DatabaseController.getAllProductTypes();

	public ProductTypesTabView(final ProductController productController)
	{
		this.productController = productController;
	}

	public VBox getView()
	{
		if (vbox == null)
		{
			HBox hb = new HBox();

			table.setEditable(true);
			table.setItems(data);

			final Callback<TableColumn<Object, String>, TableCell<Object, String>> cellFactory = (final TableColumn<Object, String> p) -> new EditingCell();
			final TableColumn<Object, String> nameColumn = new TableColumn<Object, String>("Name");

			nameColumn.setMinWidth(100);
			nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
			nameColumn.setCellFactory(cellFactory);

			nameColumn.setOnEditCommit((final CellEditEvent<Object, String> t) ->
			{
				final ProductType editProductType = ((ProductType) t.getTableView().getItems().get(t.getTablePosition().getRow()));
				editProductType.setName(t.getNewValue());
				productController.saveProductType(editProductType);
			});
			table.getColumns().add(nameColumn);

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

			// Adding the button to the cell
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

			final Label typeLabel = new Label("Type Name: ");
			final TextField productTypeName = new TextField();
			productTypeName.setMaxWidth(nameColumn.getPrefWidth());
			final Button addButton = new Button("Create");
			addButton.setOnAction((final ActionEvent e) ->
			{
				final ProductType saveProductType = new ProductType(productTypeName.getText());
				data.add(saveProductType);
				productTypeName.clear();
				productController.saveProductType(saveProductType);
			});

			hb.getChildren().addAll(typeLabel, productTypeName, addButton);
			hb.setSpacing(10);
			hb.setAlignment(Pos.CENTER_LEFT);

			vbox = new VBox();
			vbox.setSpacing(5);
			vbox.setPadding(new Insets(10, 0, 0, 10));
			vbox.getChildren().addAll(table, hb);

		}
		return vbox;
	}

	class EditingCell extends TableCell<Object, String>
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

			setText(getItem());
			setGraphic(null);
		}

		@Override
		public void updateItem(final String item, final boolean empty)
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
}
