package test.model;

import static org.junit.Assert.*;

import java.sql.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import velho.model.Product;
import velho.model.ProductBrand;
import velho.model.ProductType;

/**
 * Tests for {@link Product}.
 * @author Joona
 *
 */
public class ProductTest
{

	private ProductBrand brand = new ProductBrand("jotain") ;
	private ProductType type = new ProductType("jahas") ;
	private Product product;
	private Date date = new Date(1000);
	private String name = "porkkana";
	private int id = 20;
	
	@Before public void createProduct()
	{
		product = new Product(name, date, id, brand, type);
	}
	@After public void destroyProduct()
	{
		product = null;
	}
	@Test public void testGetName()
	{
		assertEquals(name, product.getName());
	}
	@Test public void testGetPopularity()
	{
		assertEquals(0, product.getPopularity());
	}
	@Test public void testGetExpirationDate()
	{
		assertEquals(date, product.getExpirationDate());
	}
	@Test public void testGetProductID()
	{
		assertEquals(id, product.getProductID());
	}
	@Test public void testGetBrand()
	{
		assertEquals(brand, product.getBrand());
	}
	@Test public void testGetType()
	{
		assertEquals(type, product.getType());
	}
	@Test public void testSetPopularity()
	{
		int popularity = 10;
		product.setPopularity(popularity);
		assertEquals(popularity, product.getPopularity());
	}
	
	

}
