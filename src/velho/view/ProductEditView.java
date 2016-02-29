package velho.view;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import velho.controller.ProductController;

/**
 * Creates tab for "Product Edit View"
 * 
 * @author Edward
 *
 */
public class ProductEditView
{
	private ProductController productController;
	private BorderPane bPane;

	public ProductEditView(final ProductController productController)
	{
		this.productController = productController;
	}

	public BorderPane getProductView()
	{
		if (bPane == null)
		{

			bPane = new BorderPane();
			final GridPane productPane = new GridPane();

			bPane.setCenter(productPane);

		}
		return bPane;
	}

	/**
	 * Destroys the view.
	 */

	public void destroy()
	{
		bPane = null;
	}
}
