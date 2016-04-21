package velho.model;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import velho.controller.DatabaseController;
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
	private ProductBrand brand = DatabaseController.getProductBrandByID(1);
	private ProductCategory category = DatabaseController.getProductCategoryByID(3);
	private Product product;
	private String name = "porkkana";
	private int id = 20;

	/**
	 * Loads the sample data into the database if it does not yet exist.
	 *
	 * @throws ParseException
	 */
	@BeforeClass
	public static final void loadSampleData() throws ParseException
	{
		DatabaseController.loadSampleData();
	}

	@Before
	public void createProduct()
	{
		product = new Product(id, name, brand, category);
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
		assertEquals(id, product.getDatabaseID());
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
		assertEquals("[20] porkkana (Test Brand #1 / Frozen Things (Frozen))", product.toString());
	}
}
