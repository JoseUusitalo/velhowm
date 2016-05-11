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
import velho.view.AddProductView;
import velho.view.BrandsTabView;
import velho.view.CategoriesTabView;
import velho.view.GenericTabView;
import velho.view.ListView;
import velho.view.ProductBoxesTabView;
import velho.view.ProductDataView;
import velho.view.ProductTypesTabView;
import velho.view.VerticalViewGroup;

/**
 * Controller for handling {@link Product} objects
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
	 * Apache log4j logger: User.
	 */
	private static final Logger USRLOG = Logger.getLogger("userLogger");

	/**
	 * The {@link AddProductView}.
	 */
	private AddProductView addProductView;

	/**
	 * The tab view for the product list view.
	 */
	private GenericTabView listTab;

	/**
	 * The {@link UIController}.
	 */
	private UIController uiController;

	/**
	 * The tab view for creating new objects and saving them to the list view.
	 */
	private GenericTabView addTab;

	/**
	 * The tab for creating product management view
	 */
	private VerticalViewGroup productManagementView;

	/**
	 * The tab for creating product type view
	 */
	private ProductTypesTabView productsTypeTabView;

	/**
	 * The tab for creating brands view
	 */
	private BrandsTabView brandsTabView;

	/**
	 * The tab for creating categories view
	 */
	private CategoriesTabView categoryTabView;

	private ProductBoxesTabView productBoxTabView;

	/**
	 * @param uiController
	 */
	public ProductController(final UIController uiController)
	{
		this.uiController = uiController;
		this.productManagementView = new VerticalViewGroup();
		this.productsTypeTabView = new ProductTypesTabView(this);
		this.addProductView = new AddProductView(this, uiController);
		this.brandsTabView = new BrandsTabView(this);
		this.categoryTabView = new CategoriesTabView(this);
		this.productBoxTabView = new ProductBoxesTabView(this);
		listTab = new GenericTabView();
		addTab = new GenericTabView();
		showList();
	}

	/**
	 * Gets the view in the product list tab.
	 *
	 * @return the product list tab view
	 */
	public Node getTabView()
	{
		return listTab.getView();
	}

	/**
	 * Gets the product editing view.
	 *
	 * @return the product editing view
	 */
	public Node getProductEditView()
	{
		return addProductView.getView(true);
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

			bran = DatabaseController.getProductBrandByID(DatabaseController.saveOrUpdate(new ProductBrand((String) brand)));
		}

		if (category instanceof String)
		{
			SYSLOG.trace("creating new category from " + category.toString());
			cat = DatabaseController.getProductCategoryByID(DatabaseController.saveOrUpdate(new ProductCategory((String) category)));
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

		final int dbID = DatabaseController.saveOrUpdate(newProduct);

		if (dbID < 0)
		{
			PopupController.error(LocalizationController.getString("failedToSaveProductDataPopUpWarning"));
			return null;
		}

		return DatabaseController.getProductByID(dbID);
	}

	/**
	 * Changes the view in the product list tab to the edit view.
	 *
	 * @param product {@link Product} to edit
	 */
	public void editProduct(final Product product)
	{
		AddProductView editView = new AddProductView(this, uiController);
		listTab.setView(editView.getView(true));
		editView.setViewData(product);
	}

	/**
	 * Gets the view for creating new products.
	 *
	 * @return the product creation view
	 */
	public Node getAddProductView()
	{
		return new AddProductView(this, uiController).getView(false);
	}

	/**
	 * Changes the view in the product list tab to the list.
	 */
	public void showList()
	{
		// TODO ei päivity oikein
		listTab.setView(ListController.getTableView(this, DatabaseController.getProductDataColumns(false, false), DatabaseController.getAllProducts()));
	}

	/**
	 * Changes the view in the product add/edit list tab to the list.
	 */
	public void showCreatingListView()
	{

		addTab.setView(ListController.getTableView(this, DatabaseController.getProductDataColumns(false, false), DatabaseController.getAllProducts()));

	}

	/**
	 * Changes the view in the product list tab to display the data from the
	 * given product.
	 *
	 * @param product product to display
	 */
	public void showProductView(final Product product)
	{
		listTab.setView(new ProductDataView(this).getView(product));
	}

	/**
	 * Gets a list of product search results.
	 *
	 * @return a view with the search results
	 */
	public Node getProductSearchResultsView()
	{
		SYSLOG.trace("Getting search results for product list.");
		return ListController.getTableView(this, DatabaseController.getProductDataColumns(true, false), DatabaseController.getObservableProductSearchResults());
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
			if (!DatabaseController.deleteProductBrand((ProductBrand) data))
				PopupController.error(LocalizationController.getCompoundString("unableToDeleteBrandPopUp", new Object[] { ((ProductBrand) data).getName() }));
		}
		else if (data instanceof ProductCategory)
		{
			if (!DatabaseController.deleteProductCategory((ProductCategory) data))
				PopupController.error(LocalizationController.getCompoundString("unableToDeleteCategory", new Object[] { ((ProductCategory) data).getName() }));
		}
		else if (data instanceof ProductBox)
		{
			if (!DatabaseController.deleteProductBox((ProductBox) data))
				PopupController.error(LocalizationController.getString("unableToDeleteProductBoxPopUp"));
		}
		else if (data instanceof ProductType)
		{
			if (!DatabaseController.deleteProductType((ProductType) data))
				PopupController
						.error(LocalizationController.getCompoundString("unableToDeleteProductTypePopUp", new Object[] { ((ProductType) data).getName() }));
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
		USRLOG.info("Viewing: " + ((Product) data).toString());
		listTab.setView(new ProductDataView(this).getView(((Product) data)));
	}

	@Override
	public void recreateViews(final ListView node)
	{
		// TODO check for correct view
		showList();
	}

	/**
	 * Directs the addCategoryView and makes it viewable in tabs
	 */
	public Node getProductManagementView()
	{
		productManagementView.setContents(getProductEditView());
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
		DatabaseController.save(productBrand);
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
		DatabaseController.save(productCategory);
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
		DatabaseController.save(productType);
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
		DatabaseController.save(editBrand);

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
		DatabaseController.save(saveProductType);
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
		DatabaseController.save(saveProductCategory);
	}

	public Node getCategoryTab()
	{
		return categoryTabView.getView();
	}

	public Node getProductBoxesTab()
	{
		return productBoxTabView.getView(DatabaseController.getAllProducts());
	}

	@SuppressWarnings("static-method")
	public void saveProductBox(final ProductBox productBox)
	{
		DatabaseController.saveOrUpdate(productBox);
	}
}
