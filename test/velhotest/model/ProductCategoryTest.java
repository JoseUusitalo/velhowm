package velhotest.model;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import velho.controller.database.DatabaseController;
import velho.controller.database.LogDatabaseController;
import velho.model.ProductCategory;
import velho.model.ProductType;

/**
 * Tests for the {@link ProductCategory} class.
 *
 * @author Joona Silvennoinen
 */
public class ProductCategoryTest
{
	private ProductType type = new ProductType("Regular");
	private ProductCategory category = new ProductCategory("category1", type);

	/**
	 * Creates the log database if needed and connects to it.
	 * Loads the sample data into the database if it does not yet exist.
	 *
	 * @throws Exception
	 */
	@BeforeClass
	public static final void init() throws Exception
	{
		LogDatabaseController.connectAndInitialize();
		DatabaseController.link();
		DatabaseController.loadSampleData();
	}

	/**
	 * Unlinks from both databases.
	 */
	@AfterClass
	public static final void unlinkDatabases() throws Exception
	{
		DatabaseController.unlink();
		LogDatabaseController.unlink();
	}

	@SuppressWarnings("unused")
	@Test
	public void testCategory_TypeRegular()
	{
		ProductCategory category1 = new ProductCategory("category1", type);
	}

	@Test
	public void testGetName()
	{
		assertEquals("category1", category.getName());
	}

	@Test
	public void testGetType()
	{
		assertEquals(type, category.getType());
	}
}
