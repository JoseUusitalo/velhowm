package test.model;

import java.sql.Date;

import org.junit.Test;

import velho.model.Freezer;
import velho.model.Product;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.model.enums.ProductType;

/**
 * 
 * @author Joona
 * @see ProductBoxTest
 */
public class FreezerTest
{
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

	private Product product1 = new Product(name1, date, id1, brand1, category);
	private Product product2 = new Product(name2, date, id2, brand2, category2);
	private Product product3 = new Product(name2, date, id2, brand2, category3);
	
	@SuppressWarnings("unused")
	@Test(expected=IllegalArgumentException.class) public void testFreezer_Regular()
	{
		Freezer freezer = new Freezer(1, 1, product1, 0);
	}
	@SuppressWarnings("unused")
	@Test public void testFreezer_RAW()
	{
		Freezer freezer = new Freezer(1, 1, product2, 0);
	}
	@SuppressWarnings("unused")
	@Test public void testFreezer_COLD()
	{
		Freezer freezer = new Freezer(1, 1, product3, 0);
	}
}