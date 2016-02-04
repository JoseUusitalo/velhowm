package test.model;

import static org.junit.Assert.*;

import org.junit.Test;

import velho.model.ProductCategory;
import velho.model.enums.ProductType;

@SuppressWarnings("static-method")
public class ProductCategoryTest
{

	private ProductCategory category = new ProductCategory("category1", ProductType.REGULAR);

	@SuppressWarnings("unused")
	@Test public void testCategory_TypeRegular()
	{
		ProductCategory category1 = new ProductCategory("category1", ProductType.REGULAR);
	}

	@SuppressWarnings("unused")
	@Test(expected=IllegalArgumentException.class) public void testCategory_TypeNull()
	{
		ProductCategory category2 = new ProductCategory("category2", null);
	}
	
	@Test public void testGetName()
	{
		assertEquals("category1", category.getName());
	}
	
	@Test public void testGetType()
	{
		assertEquals(ProductType.REGULAR, category.getType());
	}
}
