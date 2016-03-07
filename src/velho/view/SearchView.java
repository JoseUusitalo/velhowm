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
import velho.controller.SearchController;

/**
 *
 * @author Joona &amp; Jose Uusitalo
 */
public class SearchView
{
	/**
	 * The root GridPane for this view.
	 */
	private GridPane grid;
	private SearchController searchController;
	private ObservableList<Object> productCategories;
	private ObservableList<Object> productBrands;
	private String limits;

	public SearchView(final SearchController searchController, final ObservableList<Object> productBrands, final ObservableList<Object> productCategories)
	{
		this.searchController = searchController;
		this.productBrands = productBrands;
		this.productCategories = productCategories;
	}

	/**
	 * @param searchController2
	 * @param limits
	 * @param allProductBrands
	 * @param allProductCategories
	 */
	public SearchView(final SearchController searchController, final String limits, final ObservableList<Object> productBrands,
			final ObservableList<Object> productCategories)
	{
		this.searchController = searchController;
		this.limits = limits;
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
			nameField.setPromptText("Product Name or Product ID");
			grid.add(nameField, 0, 1, 2, 1);

			final Label countSpinnerLabel = new Label("Product Count: ");
			countSpinnerLabel.setAlignment(Pos.CENTER);
			grid.add(countSpinnerLabel, 2, 1, 1, 1);

			final Label popularitySpinnerLabel = new Label("Product Popularity: ");
			popularitySpinnerLabel.setAlignment(Pos.CENTER);
			// grid.add(popularitySpinnerLabel, 2, 2, 1, 1);

			final Spinner<Integer> productCountField = new Spinner<Integer>();
			// productCountField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 10000));
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

			final Spinner<Integer> popularityField = new Spinner<Integer>();
			// popularityField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 10000));
			popularityField.setEditable(true);
			popularityField.setPrefWidth(75.0);

			popularityField.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 10000, Integer.parseInt("-1")));
			popularityField.setEditable(true);

			final EventHandler<KeyEvent> keyboardHandler2 = new EventHandler<KeyEvent>()
			{
				@Override
				public void handle(final KeyEvent event)
				{
					try
					{
						if (Integer.parseInt(popularityField.getEditor().textProperty().get()) < -1)
						{
							throw new NumberFormatException();
						}
					}
					catch (NumberFormatException e)
					{
						popularityField.getEditor().textProperty().set("-1");
					}
				}
			};

			popularityField.getEditor().addEventHandler(KeyEvent.KEY_RELEASED, keyboardHandler2);

			// grid.add(popularityField, 3, 2, 1, 1);

			final ComboBox<Object> brandbox = new ComboBox<Object>();
			brandbox.getItems().add(null);
			brandbox.setPromptText("Product Brand");
			brandbox.getItems().addAll(productBrands);
			brandbox.setMaxWidth(Double.MAX_VALUE);
			brandbox.getSelectionModel().selectFirst();
			grid.add(brandbox, 4, 1, 1, 1);

			final ComboBox<Object> categorybox = new ComboBox<Object>();
			categorybox.getItems().add(null);
			categorybox.setPromptText("Product Category");
			categorybox.getItems().addAll(productCategories);
			categorybox.getSelectionModel().selectFirst();
			grid.add(categorybox, 4, 2, 1, 1);

			final DatePicker dpStart = new DatePicker();
			dpStart.setPromptText("Expration Date Start");
			grid.add(dpStart, 0, 2, 1, 1);

			final DatePicker dpEnd = new DatePicker();
			dpEnd.setPromptText("Expration Date End");
			grid.add(dpEnd, 1, 2, 1, 1);

			Button searchButton = new Button("Search");
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
						// Although badge IDs are stored as string, they are still numbers.
					}

					try
					{
						Integer.parseInt(popularityField.getValue().toString());
					}
					catch (final NumberFormatException e)
					{
						// Although badge IDs are stored as string, they are still numbers.
					}
					searchController.productSearch(limits, nameField.getText(), productCountField.getValue(), popularityField.getValue(), brandbox.getValue(),
							categorybox.getValue(), dpStart.getValue(), dpEnd.getValue());
				}
			});
		}

		return grid;
	}

	/**
	 * Destroys the view.
	 */

	public void destroy()
	{
		grid = null;
	}
}
