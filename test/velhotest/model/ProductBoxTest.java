package velhotest.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import velho.controller.database.DatabaseController;
import velho.controller.database.LogDatabaseController;
import velho.model.Product;
import velho.model.ProductBox;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.model.ProductType;

/**
 * Tests for {@link ProductBox}.
 *
 * @author Joona Silvennoinen
 */
public class ProductBoxTest
{
	private ProductBox box;
	private ProductBrand brand1 = new ProductBrand("pirkka");
	private ProductType regular = new ProductType("Regular");
	private ProductType raw = new ProductType("Raw");
	private ProductType cold = new ProductType("Cold");
	private ProductCategory category = new ProductCategory("tyyppi nimi", regular);
	private ProductCategory category2 = new ProductCategory("tyyppi nimi", raw);
	private ProductCategory category3 = new ProductCategory("tyyppi nimi", cold);
	private ProductBrand brand2 = new ProductBrand("rainbow");
	private Date date = new Date(1000);
	private String name1 = "porkkana";
	private String name2 = "peruna";
	private int maxSize = 12;

	private Product product1 = new Product(0, name1, brand1, category);
	private Product product2 = new Product(0, name2, brand2, category2);
	private Product product3 = new Product(0, name2, brand2, category3);

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

	@Before
	public void createProductBox()
	{
		box = new ProductBox(0, product1, maxSize, 0, date);
	}

	@After
	public void destroyProductBox()
	{
		box = null;
	}

	@SuppressWarnings("unused")
	@Test
	public void testCreateProductBox_RAW()
	{
		ProductBox productBox2 = new ProductBox(1, product2, 1, 0, date);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testCreateProductBox_MaxSize_1()
	{
		ProductBox productBox4 = new ProductBox(1, product3, maxSize, maxSize + 1, date);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testCreateProductBox_MaxSize_2()
	{
		ProductBox productBox5 = new ProductBox(1, product3, maxSize - 14, maxSize, date);
	}

	@Test
	public void testGetBoxType()
	{
		assertEquals(regular, box.getBoxType());
	}

	@Test
	public void testGetBoxID()
	{
		assertEquals(0, box.getDatabaseID());
	}

	@Test
	public void testGetProductCount()
	{
		assertEquals(0, box.getProductCount());
	}

	@Test
	public void testGetMaxSize()
	{
		assertEquals(maxSize, box.getMaxSize());
	}

	@Test
	public void testGetProduct1()
	{
		assertEquals(product1, box.getProduct());
	}

	@Test
	public void testGetProduct2()
	{
		ProductBox box2 = new ProductBox(0, product2, maxSize, 0, date);
		assertEquals(product2, box2.getProduct());
	}

	@Test
	public void testProductBoxAdd()
	{
		assertTrue(box.addProduct(2));
		assertEquals(2, box.getProductCount());
	}

	@Test
	public void testProductBoxAdd_False()
	{
		assertFalse(box.addProduct(maxSize + 1));
		assertEquals(0, box.getProductCount());
	}

	@Test
	public void testProductBoxAdd_2()
	{
		assertTrue(box.addProduct(maxSize));
		assertFalse(box.addProduct(2));
		assertEquals(maxSize, box.getProductCount());
	}

	@Test
	public void testRemoveProduct_Empty()
	{
		assertFalse(box.removeProduct(2));
		assertEquals(0, box.getProductCount());
	}

	@Test
	public void testRemoveProduct()
	{
		assertTrue(box.addProduct(3));
		assertTrue(box.removeProduct(2));
		assertEquals(1, box.getProductCount());
	}

	@Test
	public void testRemoveProduct2()
	{
		assertTrue(box.addProduct(3));
		assertEquals(3, box.getProductCount());
		assertFalse(box.removeProduct(10));
		assertEquals(3, box.getProductCount());
	}

	@Test
	public void testAddProduct_TooMany()
	{
		assertFalse(box.addProduct(20));
		assertEquals(0, box.getProductCount());
	}

	@Test
	public void testProductBoxAdd2()
	{
		assertTrue(box.addProduct(maxSize));
		assertFalse(box.addProduct(2));
		assertEquals(maxSize, box.getProductCount());
	}

	@Test
	public void testProductBoxAdd3()
	{
		assertTrue(box.addProduct(maxSize - 3));
		assertFalse(box.addProduct(5));
		assertEquals(maxSize - 3, box.getProductCount());
	}

	@Test
	public void testToString()
	{
		ProductBox box2 = new ProductBox(0, product2, 1, 0, date);
		assertEquals("[0] Box: peruna (0)", box2.toString());
	}

	@Test
	public void testGetExpirationDate()
	{
		assertEquals(date, box.getExpirationDate());
	}
}
