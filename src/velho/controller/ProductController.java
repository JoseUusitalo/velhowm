package velho.controller;

import org.apache.log4j.Logger;

import javafx.scene.Node;
import velho.controller.database.DatabaseController;
import velho.controller.interfaces.UIActionController;
import velho.model.Product;
import velho.model.ProductBox;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.model.ProductType;
import velho.view.BrandsTabView;
import velho.view.CategoriesTabView;
import velho.view.ListView;
import velho.view.ProductBoxesTabView;
import velho.view.ProductTabView;
import velho.view.ProductTypesTabView;
import velho.view.VerticalViewGroup;

/**
 * The singleton controller for handling {@link Product} objects
 *
 * @author Jose Uusitalo &amp; Edward Puustinen
 */
public class ProductController implements UIActionController
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(ProductController.class.getName());

	/**
	 * The tab for creating product management view
	 */
	private final VerticalViewGroup productManagementView;

	/**
	 * The tab for creating product type view
	 */
	private final ProductTypesTabView productsTypeTabView;

	/**
	 * The tab for creating brands view
	 */
	private final BrandsTabView brandsTabView;

	/**
	 * The tab for creating categories view
	 */
	private final CategoriesTabView categoryTabView;

	/**
	 * Product box viewing/editing/creation tab view.
	 */
	private final ProductBoxesTabView productBoxTabView;

	/**
	 * A tab view for creating and editing products.
	 */
	private ProductTabView productTabView;

	/**
	 * A private inner class holding the class instance.
	 *
	 * @author Jose Uusitalo
	 */
	private static class Holder
	{
		/**
		 * The only instance of {@link ProductController}.
		 */
		private static final ProductController INSTANCE = new ProductController();
	}

	/**
	 */
	private ProductController()
	{
		this.productManagementView = new VerticalViewGroup();
		this.productsTypeTabView = new ProductTypesTabView(DatabaseController.getInstance().getAllProductTypes());
		this.brandsTabView = new BrandsTabView(DatabaseController.getInstance().getAllProductBrands());
		this.categoryTabView = new CategoriesTabView(DatabaseController.getInstance().getAllProductCategories());
		this.productBoxTabView = new ProductBoxesTabView(DatabaseController.getInstance().getAllProductBoxes());
		this.productTabView = new ProductTabView(DatabaseController.getInstance().getAllProducts());
	}

	/**
	 * Gets the instance of the {@link ProductController}.
	 *
	 * @return the product controller
	 */
	public static synchronized ProductController getInstance()
	{
		return Holder.INSTANCE;
	}

	/**
	 * Gets the view in the product list tab.
	 *
	 * @return the product list tab view
	 */
	public Node getProductTabView()
	{
		return productTabView.getView(DatabaseController.getInstance().getAllProductBrands(), DatabaseController.getInstance().getAllProductCategories());
	}

	/**
	 * Saves the new or existing product to database and returns the updated
	 * object.
	 *
	 * @param databaseID database ID of the product (<code>-1</code> for a new
	 * one)
	 * @param name name of the of product
	 * @param brand brand of the product
	 * @param category category of the product
	 */
	@SuppressWarnings("static-method")
	public Product saveProduct(final int databaseID, final String name, final Object brand, final Object category)
	{
		ProductBrand bran = null;
		ProductCategory cat = null;
		if (brand instanceof String)
		{
			SYSLOG.trace("creating new brand from " + brand.toString());

			bran = DatabaseController.getInstance().getProductBrandByID(DatabaseController.getInstance().saveOrUpdate(new ProductBrand((String) brand)));
		}

		if (category instanceof String)
		{
			SYSLOG.trace("creating new category from " + category.toString());
			cat = DatabaseController.getInstance()
					.getProductCategoryByID(DatabaseController.getInstance().saveOrUpdate(new ProductCategory((String) category)));
		}

		if (brand instanceof ProductBrand)
		{
			bran = (ProductBrand) brand;
			SYSLOG.trace("new brand is " + bran.toString());
		}

		if (category instanceof ProductCategory)
		{
			cat = (ProductCategory) category;
			SYSLOG.trace("new product category is " + cat.toString());
		}

		Product newProduct = new Product(databaseID, name, bran, cat);

		final int dbID = DatabaseController.getInstance().saveOrUpdate(newProduct);

		if (dbID < 0)
		{
			PopupController.getInstance().error(LocalizationController.getInstance().getString("failedToSaveProductDataPopUpWarning"));
			return null;
		}

		return DatabaseController.getInstance().getProductByID(dbID);
	}

	/**
	 * Gets a list of product search results.
	 *
	 * @return a view with the search results
	 */
	public Node getProductSearchResultsView()
	{
		SYSLOG.trace("Getting search results for product list.");
		return ListController.getTableView(this, DatabaseController.getInstance().getProductDataColumns(true, false),
				DatabaseController.getInstance().getObservableProductSearchResults());
	}

	/**
	 * Makes an unsupported Object to data
	 */
	@Override
	public void createAction(final Object data)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Updates the new unsupported Object to data
	 */
	@Override
	public void updateAction(final Object data)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Makes an unsupported Object to data
	 */
	@Override
	public void addAction(final Object data)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Removes an unsupported Object from data
	 */
	@Override
	public void removeAction(final Object data)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Deletes an unsupported Object from data
	 */
	@Override
	public void deleteAction(final Object data)
	{
		if (data instanceof ProductBrand)
		{
			if (!DatabaseController.getInstance().deleteProductBrand((ProductBrand) data))
				PopupController.getInstance().error(
						LocalizationController.getInstance().getCompoundString("unableToDeleteBrandPopUp", new Object[] { ((ProductBrand) data).getName() }));
		}
		else if (data instanceof ProductCategory)
		{
			if (!DatabaseController.getInstance().deleteProductCategory((ProductCategory) data))
				PopupController.getInstance().error(
						LocalizationController.getInstance().getCompoundString("unableToDeleteCategory", new Object[] { ((ProductCategory) data).getName() }));
		}
		else if (data instanceof ProductBox)
		{
			if (!DatabaseController.getInstance().deleteProductBox((ProductBox) data))
				PopupController.getInstance().error(LocalizationController.getInstance().getString("unableToDeleteProductBoxPopUp"));
		}
		else if (data instanceof Product)
		{
			if (!DatabaseController.getInstance().deleteProduct((Product) data))
				PopupController.getInstance()
						.error(LocalizationController.getInstance().getCompoundString("unableToDeleteProductError", ((Product) data).getName()));
		}
		else if (data instanceof ProductType)
		{
			if (!DatabaseController.getInstance().deleteProductType((ProductType) data))
				PopupController.getInstance().error(LocalizationController.getInstance().getCompoundString("unableToDeleteProductTypePopUp",
						new Object[] { ((ProductType) data).getName() }));
		}
		else
		{
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Makes an unsupported Object Observable
	 */
	@Override
	public void viewAction(final Object data)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void recreateViews(final ListView node)
	{
		getBrandsTab();
		getCategoryTab();
		getProductBoxesTab();
		getProductManagementView();
		getProductTabView();
		getProductTypesTab();
	}

	/**
	 * Directs the addCategoryView and makes it viewable in tabs
	 */
	public Node getProductManagementView()
	{
		return productManagementView.getView();
	}

	/**
	 * Saves the new or existing ProductBrand to database and returns the
	 * updated object.
	 *
	 * @param name placeholder for the brand
	 * @return a product brand when called
	 */
	@SuppressWarnings("static-method")
	public ProductBrand saveBrand(final String name)
	{
		ProductBrand productBrand = new ProductBrand(name);
		DatabaseController.getInstance().saveOrUpdate(productBrand);
		return productBrand;
	}

	/**
	 * @param name the name of the new category
	 * @param type the type of the new category
	 * @return what ever is in the product category
	 */
	@SuppressWarnings("static-method")
	public ProductCategory saveCategory(final String name, final ProductType type)
	{
		ProductCategory productCategory = new ProductCategory(name, type);
		DatabaseController.getInstance().saveOrUpdate(productCategory);
		return productCategory;

	}

	/**
	 * Saves the new ProductType into databasecontroller idea is that this
	 * should minimize the tasks that product
	 * controller already is running
	 *
	 * @param name The new name of the product
	 * @return what ever is in the product type
	 */
	@SuppressWarnings("static-method")
	public ProductType saveProductType(final String name)
	{
		ProductType productType = new ProductType(name);
		DatabaseController.getInstance().saveOrUpdate(productType);
		return productType;
	}

	/**
	 * Gets brands tab and makes it viewable
	 *
	 * @return returns the BrandsTabView making it Viewable in tabs
	 */
	public Node getBrandsTab()
	{
		return brandsTabView.getView();
	}

	/**
	 * Gets product type tab and makes it viewable
	 *
	 * @return returns the ProductTypesTabView making it Viewable in tabs
	 */
	public Node getProductTypesTab()
	{
		return productsTypeTabView.getView();
	}

	/**
	 * Save a new brand to Product Controller
	 *
	 * @param editBrand allows to edit the new brand
	 */
	@SuppressWarnings("static-method")
	public void saveBrand(final ProductBrand editBrand)
	{
		DatabaseController.getInstance().saveOrUpdate(editBrand);
	}

	/**
	 * Controlls the action the new Product Type to Product Controller when
	 * action is performed
	 *
	 * @param saveProductType saves the new Product Type written in the
	 * TextField
	 */
	@SuppressWarnings("static-method")
	public void saveProductType(final ProductType saveProductType)
	{
		DatabaseController.getInstance().saveOrUpdate(saveProductType);
	}

	/**
	 * Controlls the action of a new Category to Product Controller when action
	 * is performed
	 *
	 * @param saveProductCategory saves the new data written in the TextField
	 */
	@SuppressWarnings("static-method")
	public void saveProductCategory(final ProductCategory saveProductCategory)
	{
		DatabaseController.getInstance().saveOrUpdate(saveProductCategory);
	}

	public Node getCategoryTab()
	{
		return categoryTabView.getView();
	}

	public Node getProductBoxesTab()
	{
		return productBoxTabView.getView(DatabaseController.getInstance().getAllProducts());
	}

	@SuppressWarnings("static-method")
	public void saveProductBox(final ProductBox productBox)
	{
		DatabaseController.getInstance().saveOrUpdate(productBox);
	}
}
