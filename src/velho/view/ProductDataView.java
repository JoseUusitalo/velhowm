package velho.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import velho.controller.ProductController;
import velho.model.Product;

public class ProductDataView
{

	/**
	 * The root BorderPane for this view.
	 */
	private GridPane grid;

	private ProductController controller;

	public ProductDataView(final ProductController controller)
	{
		this.controller = controller;
	}

	public GridPane getView(final Product product)
	{
		if (grid == null)
		{
			grid = new GridPane();

			grid.setAlignment(Pos.CENTER);
			grid.setHgap(10);
			grid.setVgap(10);

			Label scenetitle = new Label(product.getName());
			scenetitle.getStyleClass().add("centered-title");
			grid.add(scenetitle, 0, 0, 2, 1);

			Label productID = new Label("ID: ");
			grid.add(productID, 0, 1);

			Label productBrand = new Label("Brand: ");
			grid.add(productBrand, 0, 2);

			Label productCategory = new Label("Category: ");
			grid.add(productCategory, 0, 3);

			// Label productPopularity = new Label("Popularity: ");
			// grid.add(productPopularity, 0, 4);

			Label productIDValue = new Label(String.valueOf(product.getProductID()));
			grid.add(productIDValue, 1, 1);

			Label productBrandValue = new Label(product.getBrand().getName());
			grid.add(productBrandValue, 1, 2);

			Label productCategoryValue = new Label(product.getCategory().getName());
			grid.add(productCategoryValue, 1, 3);

			// Label productPopularityValue = new Label(String.valueOf(product.getPopularity()));
			// grid.add(productPopularityValue, 1, 4);

			Button editButton = new Button("Edit");
			editButton.setMaxWidth(Double.MAX_VALUE);
			editButton.setAlignment(Pos.CENTER);
			grid.add(editButton, 1, 5);

			editButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					controller.editProduct(product);
				}
			});

			Button backButton = new Button("Back");
			backButton.setMaxWidth(Double.MAX_VALUE);
			backButton.setAlignment(Pos.CENTER);
			grid.add(backButton, 0, 5);

			backButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					controller.showList();
				}
			});
		}

		return grid;
	}
}
