package test.model;

import static org.junit.Assert.assertEquals;

import java.sql.Date;

import org.junit.Test;

import velho.model.Freezer;
import velho.model.Product;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.model.ProductType;

/**
 * Tests for the {@link Freezer} class.
 *
 * @author Joona Silvennoinen
 */
public class FreezerTest
{
	private ProductBrand brand1 = new ProductBrand(-1, "pirkka");
	private ProductType regular = new ProductType(-1, "Regular");
	private ProductType raw = new ProductType(-1, "Raw");
	private ProductType cold = new ProductType(-1, "Cold");
	private ProductCategory category = new ProductCategory(-1, "tyyppi nimi", regular);
	private ProductCategory category2 = new ProductCategory(-1, "tyyppi nimi", raw);
	private ProductCategory category3 = new ProductCategory(-1, "tyyppi nimi", cold);
	private ProductBrand brand2 = new ProductBrand(-1, "rainbow");
	private Date date = new Date(1000);
	private String name1 = "porkkana";
	private String name2 = "peruna";
	private int id1 = 1;
	private int id2 = 2;

	private Product product1 = new Product(id1, name1, brand1, category, -1);
	private Product product2 = new Product(id2, name2, brand2, category2, -1);
	private Product product3 = new Product(id2, name2, brand2, category3, -1);

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testFreezer_Regular()
	{
		Freezer freezer = new Freezer(1, date, 1, product1, 0);
	}

	@SuppressWarnings("unused")
	@Test
	public void testFreezer_RAW()
	{
		Freezer freezer = new Freezer(1, date, 1, product2, 0);
	}

	@SuppressWarnings("unused")
	@Test
	public void testFreezer_COLD()
	{
		Freezer freezer = new Freezer(1, date, 1, product3, 0);
	}

	@Test
	public void testToString()
	{
		Freezer f2 = new Freezer(1, date, 1, product2, 0);
		assertEquals("[1] Freezer: peruna (0)", f2.toString());
	}

	@Test
	public void testGetExpirationDate()
	{
		Freezer freezer = new Freezer(1, date, 1, product2, 0);
		assertEquals(date, freezer.getExpirationDate());
	}
}