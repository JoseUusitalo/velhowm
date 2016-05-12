package velhotest.model;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import velho.controller.database.DatabaseController;
import velho.controller.database.LogDatabaseController;
import velho.model.Product;
import velho.model.ProductBrand;
import velho.model.ProductCategory;

/**
 * Tests for the {@link Product} class.
 *
 * @author Joona Silvennoinen
 */
public class ProductTest
{
	private ProductBrand brand = DatabaseController.getInstance().getProductBrandByID(1);
	private ProductCategory category = DatabaseController.getInstance().getProductCategoryByID(3);
	private Product product;
	private String name = "porkkana";

	/**
	 * Creates the log database if needed and connects to it.
	 * Loads the sample data into the database if it does not yet exist.
	 *
	 * @throws Exception
	 */
	@BeforeClass
	public static final void init() throws Exception
	{
		LogDatabaseController.getInstance().connectAndInitialize();
		DatabaseController.getInstance().link();
		DatabaseController.getInstance().loadSampleData();
	}

	/**
	 * Unlinks from both databases.
	 */
	@AfterClass
	public static final void unlinkDatabases() throws Exception
	{
		DatabaseController.getInstance().unlink();
		LogDatabaseController.getInstance().unlink();
	}

	@Before
	public void createProduct()
	{
		product = new Product(0, name, brand, category);
	}

	@After
	public void destroyProduct()
	{
		product = null;
	}

	@Test
	public void testGetName()
	{
		assertEquals(name, product.getName());
	}

	@Test
	public void testGetProductID()
	{
		assertEquals(0, product.getDatabaseID());
	}

	@Test
	public void testGetBrand()
	{
		assertEquals(brand, product.getBrand());
	}

	@Test
	public void testGetBrandName()
	{
		assertEquals("Test Brand #1", product.getBrand().getName());
	}

	@Test
	public void testGetBrandID()
	{
		assertEquals(1, product.getBrand().getDatabaseID());
	}

	@Test
	public void testGetCategory()
	{
		assertEquals(category, product.getCategory());
	}

	@Test
	public void testGetCategoryID()
	{
		assertEquals(3, product.getCategory().getDatabaseID());
	}

	@Test
	public void testGetTypeID()
	{
		assertEquals(3, product.getCategory().getType().getDatabaseID());
	}

	@Test
	public final void testToString()
	{
		assertEquals("[0] porkkana (Test Brand #1 / Frozen Things (Frozen))", product.toString());
	}
}
