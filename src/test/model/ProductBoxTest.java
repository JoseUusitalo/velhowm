package test.model;

import static org.junit.Assert.*;

import java.sql.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import velho.model.ProductBox;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.model.enums.ProductType;
import velho.model.Product;

/**
 * Tests for {@link ProductBox}.
 * 
 * @author Joona
 */
public class ProductBoxTest
{

	private ProductBox box;
	private ProductBrand brand1 = new ProductBrand("pirkka");
	private ProductType type = ProductType.REGULAR;
	private ProductType type2 = ProductType.RAW;
	private ProductType type3 = ProductType.COLD;
	private ProductCategory category = new ProductCategory("tyyppi nimi", type);
	private ProductCategory category2 = new ProductCategory("tyyppi nimi", type2);
	private ProductCategory category3 = new ProductCategory("tyyppi nimi", type3);
	private ProductBrand brand2 = new ProductBrand("rainbow");
	private Date date = new Date(1000);
	private String name1 = "porkkana";
	private String name2 = "peruna";
	private int id1 = 1;
	private int id2 = 2;
	private int maxSize = 12;

	private Product product1 = new Product(name1, date, id1, brand1, category);
	private Product product2 = new Product(name2, date, id2, brand2, category2);
	private Product product3 = new Product(name2, date, id2, brand2, category3);

	@Before public void createProductBox()
	{
		box = new ProductBox(id1, maxSize, product1, 0);
	}

	@After public void destroyProductBox()
	{
		box = null;
	}

	@SuppressWarnings("unused")
	@Test public void testCreateProductBox_RAW()
	{
		ProductBox productBox2 = new ProductBox(1, 1, product2, 0);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class) public void testCreateProductBox_COLD()
	{
		ProductBox productBox3 = new ProductBox(1, 1, product3, 0);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class) public void testCreateProductBox_MaxSize_1()
	{
		ProductBox productBox4 = new ProductBox(1, maxSize, product3, maxSize+1);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class) public void testCreateProductBox_MaxSize_2()
	{
		ProductBox productBox5 = new ProductBox(1, maxSize-14, product3, maxSize);
	}

	
	@Test public void testGetBoxType()
	{
		assertEquals(type, box.getBoxType());
	}

	@Test public void testGetBoxID()
	{
		assertEquals(id1, box.getBoxID());
	}

	@Test public void testGetProductCount()
	{
		assertEquals(0, box.getProductCount());
	}

	@Test public void testGetMaxSize()
	{
		assertEquals(maxSize, box.getMaxSize());
	}

	@Test public void testGetProduct1()
	{
		assertEquals(product1, box.getProduct());
	}

	@Test public void testGetProduct2()
	{
		ProductBox box2 = new ProductBox(id1, maxSize, product2, 0);
		assertEquals(product2, box2.getProduct());
	}

	@Test(expected = IllegalArgumentException.class) public void testGetProduct3()
	{
		ProductBox box3 = new ProductBox(id1, maxSize, product3, 0);
		assertEquals(product3, box3.getProduct());
	}

	@Test public void testProductBoxAdd()
	{
		assertTrue(box.addProduct(2));
		assertEquals(2, box.getProductCount());
	}
	
	@Test public void testProductBoxAdd_False()
	{
		assertFalse(box.addProduct(maxSize+1));
		assertEquals(0, box.getProductCount());
	}
	
	@Test public void testProductBoxAdd_2()
	{
		assertTrue(box.addProduct(maxSize));
		assertFalse(box.addProduct(2));
		assertEquals(maxSize, box.getProductCount());
	}

	@Test public void testRemoveProduct_Empty()
	{
		assertFalse(box.removeProduct(2));
		assertEquals(0, box.getProductCount());
	}

	@Test public void testRemoveProduct()
	{
		assertTrue(box.addProduct(3));
		assertTrue(box.removeProduct(2));
		assertEquals(1, box.getProductCount());
	}

	@Test public void testRemoveProduct2()
	{
		assertTrue(box.addProduct(3));
		assertEquals(3, box.getProductCount());
		assertFalse(box.removeProduct(10));
		assertEquals(3, box.getProductCount());
	}

	@Test public void testAddProduct_TooMany()
	{
		assertFalse(box.addProduct(20));
		assertEquals(0, box.getProductCount());
	}

	@Test public void testProductBoxAdd2()
	{
		assertTrue(box.addProduct(maxSize));
		assertFalse(box.addProduct(2));
		assertEquals(maxSize, box.getProductCount());
	}

	@Test public void testProductBoxAdd3()
	{
		assertTrue(box.addProduct(maxSize - 3));
		assertFalse(box.addProduct(5));
		assertEquals(maxSize - 3, box.getProductCount());
	}
}