package test.model;

import static org.junit.Assert.*;

import java.sql.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import velho.model.Product;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.model.ProductType;

/**
 * Tests for the {@link Product} class.
 * 
 * @author Joona
 */
public class ProductTest
{
	private ProductBrand brand = new ProductBrand(-1, "jotain");
	private ProductType regular = new ProductType(-1, "Regular");
	private ProductCategory category = new ProductCategory(-1, "jahas", regular);
	private Product product;
	private Date date = new Date(1000);
	private String name = "porkkana";
	private int id = 20;

	@Before
	public void createProduct()
	{
		product = new Product(id, name, date, brand, category, -1);
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
	public void testGetPopularity()
	{
		assertEquals(-1, product.getPopularity());
	}

	@Test
	public void testGetExpirationDate()
	{
		assertEquals(date, product.getExpirationDate());
	}

	@Test
	public void testGetProductID()
	{
		assertEquals(id, product.getProductID());
	}

	@Test
	public void testGetBrand()
	{
		assertEquals(brand, product.getBrand());
	}

	@Test
	public void testGetBrandName()
	{
		assertEquals("jotain", product.getBrand().getName());
	}

	@Test
	public void testGetType()
	{
		assertEquals(category, product.getCategory());
	}

	@Test
	public void testSetPopularity()
	{
		int popularity = 10;
		product.setPopularity(popularity);
		assertEquals(popularity, product.getPopularity());
	}

	@Test
	public final void testToString()
	{
		assertEquals("[20] porkkana (jotain / jahas (Regular)), Expires: 1970-01-01, Popularity: -1", product.toString());
	}
}
