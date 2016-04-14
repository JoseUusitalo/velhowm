package velho.view;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
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
import velho.model.ProductBrand;

public class BrandsTab
{

	/**
	 * ProductCntroller neeeded when saving to database
	 */
	private ProductController productController;

	/**
	 * Makes the Brands tab call for table and make it viewable
	 */
	private final TableView<Object> table = new TableView<Object>();

	/**
	 * this is a VBox we use like a grid
	 */
	private VBox vbox;

	/**
	 * Makes the Brands and ObservableList
	 */
	private ObservableList<Object> data = DatabaseController.getAllProductBrands();

	/**
	 * Adds info to Product Controller about brands
	 * 
	 * @param productController Product Controller handles the database work
	 * @param uiController links UIController to the productController
	 */
	public BrandsTab(final ProductController productController, final UIController uiController)
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

			Callback<TableColumn<Object, String>, TableCell<Object, String>> cellFactory = (final TableColumn<Object, String> p) -> new EditingCell();

			TableColumn<Object, String> nameColumn = new TableColumn<Object, String>("Name");

			nameColumn.setMinWidth(100);
			nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
			nameColumn.setCellFactory(cellFactory);

			nameColumn.setOnEditCommit((final CellEditEvent<Object, String> t) ->
			{
				final ProductBrand editBrand = ((ProductBrand) t.getTableView().getItems().get(t.getTablePosition().getRow()));
				editBrand.setName(t.getNewValue());
				productController.saveBrand(editBrand);
			});
			table.setItems(data);
			table.getColumns().add(nameColumn);

			final TextField brandName = new TextField();
			brandName.setPromptText("Brand Name");
			brandName.setMaxWidth(nameColumn.getPrefWidth());
			final Button addButton = new Button("Create");
			addButton.setOnAction((final ActionEvent e) ->
			{
				final ProductBrand saveBrand = new ProductBrand(brandName.getText());
				data.add(saveBrand);
				brandName.clear();
				productController.saveBrand(saveBrand);
			});

			hb.getChildren().addAll(brandName, addButton);
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
	 * @author Edward
	 *         Enables editing a cell, nameley the textField
	 */
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
