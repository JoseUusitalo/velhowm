package velho.view;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import velho.controller.LocalizationController;
import velho.controller.SearchController;
import velho.controller.UIController;
import velho.model.interfaces.GenericView;

/**
 * @author Joona Silvennoinen &amp; Jose Uusitalo
 */
public class SearchView implements GenericView
{
	// TODO search button doesnt work right
	/**
	 * The root GridPane for this view.
	 */
	private GridPane grid;

	private final SearchController searchController;

	private final ObservableList<Object> productCategories;

	private final ObservableList<Object> productBrands;

	private final boolean canBeInRemovalList;

	/**
	 * @param searchController
	 * @param limits
	 * @param productBrands
	 * @param productCategories
	 */
	public SearchView(final SearchController searchController, final boolean canBeInRemovalList, final ObservableList<Object> productBrands, final ObservableList<Object> productCategories)
	{
		this.searchController = searchController;
		this.canBeInRemovalList = canBeInRemovalList;
		this.productBrands = productBrands;
		this.productCategories = productCategories;
	}

	/**
	 * Gets the login view.
	 *
	 * @return the login view GridPane
	 */
	public GridPane getView()
	{
		if (grid == null)
		{
			grid = new GridPane();
			grid.setVgap(10);
			grid.setHgap(10);
			grid.setAlignment(Pos.CENTER);

			final TextField nameField = new TextField();
			nameField.setPromptText(LocalizationController.getInstance().getString("productNameorIDPromptText"));
			grid.add(nameField, 0, 1, 2, 1);

			final Label countSpinnerLabel = new Label(LocalizationController.getInstance().getString("productCountSpinnerText"));
			countSpinnerLabel.setAlignment(Pos.CENTER);
			grid.add(countSpinnerLabel, 2, 1, 1, 1);

			final Spinner<Integer> productCountField = new Spinner<Integer>();
			// productCountField.setValueFactory(new
			// SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 10000));
			productCountField.setEditable(true);
			productCountField.setPrefWidth(75.0);

			productCountField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 10000, Integer.parseInt("-1")));
			productCountField.setEditable(true);

			final EventHandler<KeyEvent> keyboardHandler = new EventHandler<KeyEvent>()
			{
				@Override
				public void handle(final KeyEvent event)
				{
					try
					{
						if (Integer.parseInt(productCountField.getEditor().textProperty().get()) < -1)
						{
							throw new NumberFormatException();
						}

					}
					catch (NumberFormatException e)
					{
						productCountField.getEditor().textProperty().set("-1");
					}
				}
			};

			productCountField.getEditor().addEventHandler(KeyEvent.KEY_RELEASED, keyboardHandler);

			grid.add(productCountField, 3, 1, 1, 1);

			final ComboBox<Object> brandbox = new ComboBox<Object>();
			brandbox.getItems().add(null);
			brandbox.setPromptText(LocalizationController.getInstance().getString("productBrandComboboxPrompt"));
			brandbox.getItems().addAll(productBrands);
			brandbox.setMaxWidth(Double.MAX_VALUE);
			brandbox.getSelectionModel().selectFirst();
			grid.add(brandbox, 4, 1, 1, 1);

			final ComboBox<Object> categorybox = new ComboBox<Object>();
			categorybox.getItems().add(null);
			categorybox.setPromptText(LocalizationController.getInstance().getString("productCategoryComboboxPrompt"));
			categorybox.getItems().addAll(productCategories);
			categorybox.getSelectionModel().selectFirst();
			grid.add(categorybox, 4, 2, 1, 1);

			final DatePicker dpStart = new DatePicker();
			dpStart.setPromptText(LocalizationController.getInstance().getString("expirationDateStartDTPrompt"));
			grid.add(dpStart, 0, 2, 1, 1);

			final DatePicker dpEnd = new DatePicker();
			dpEnd.setPromptText(LocalizationController.getInstance().getString("expirationDateEndDTPrompt"));
			grid.add(dpEnd, 1, 2, 1, 1);

			Button searchButton = new Button(LocalizationController.getInstance().getString("searchButton"));
			searchButton.setMaxWidth(Double.MAX_VALUE);
			searchButton.setPrefHeight(60.0);
			searchButton.setPrefWidth(120.0);
			grid.add(searchButton, 5, 1, 1, 2);

			searchButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{

					try
					{
						Integer.parseInt(productCountField.getValue().toString());
					}
					catch (final NumberFormatException e)
					{
						// Although badge IDs are stored as string, they are
						// still numbers.
					}

					searchController.productBoxSearch(canBeInRemovalList, nameField.getText(), productCountField.getValue(), brandbox.getValue(), categorybox.getValue(), dpStart.getValue(), dpEnd.getValue());
				}
			});
			UIController.getInstance().recordView(this);
		}

		return grid;
	}

	/**
	 * Destroys the view.
	 */

	@Override
	public void recreate()
	{
		grid = null;
		getView();
	}

	@Override
	public void destroy()
	{
		grid = null;
	}
}
