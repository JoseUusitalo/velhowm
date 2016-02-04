package test.model;

import static org.junit.Assert.*;

import java.sql.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import velho.model.ProductBox;
import velho.model.ProductBrand;
import velho.model.ProductType;
import velho.model.Product;

/**
 * Tests for {@link ProductBox}.
 * @author Joona
 *
 */
public class ProductBoxTest
{
	
	private ProductBox box;
	private ProductBrand brand1 = new ProductBrand("pirkka");
	private ProductType type = new ProductType("tyyppi nimi");
	private ProductBrand brand2 = new ProductBrand("rainbow");
	private Date date = new Date(1000);
	private String name1 = "porkkana";
	private String name2 = "peruna";
	private int id1 = 1;
	private int id2 = 2;
	private int maxSize = 12;
	
	private Product product1 = new Product(name1, date, id1, brand1, type);
	private Product product2 = new Product(name2, date, id2, brand2, type);
	
	@Before public void createProductBox()
	{
		box = new ProductBox(id1, maxSize, product1, 0);
	}
	@After public void destroyProductBox()
	{
		box = null;
	}
	

	@Test public void testProductBox()
	{
		assertEquals(0, box.getProductCount());
		assertEquals(maxSize, box.getMaxSize());
		assertEquals(id1, box.getBoxID());
	}
	
	@Test public void testProductBoxAdd()
	{
		assertTrue(box.addProduct(2));
		assertEquals(2, box.getProductCount());
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
		assertTrue(box.addProduct(maxSize-3));
		assertFalse(box.addProduct(5));
		assertEquals(maxSize-3, box.getProductCount());
	}
}
