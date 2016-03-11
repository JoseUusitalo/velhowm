package test.model;

import static org.junit.Assert.*;

import org.junit.Test;

import velho.model.ProductCategory;
import velho.model.ProductType;

/**
 * Tests for the {@link ProductCategory} class.
 * 
 * @author Joona Silvennoinen
 */
@SuppressWarnings("static-method")
public class ProductCategoryTest
{
	private ProductType type = new ProductType(-1, "Regular");
	private ProductCategory category = new ProductCategory(-1, "category1", type);

	@SuppressWarnings("unused")
	@Test
	public void testCategory_TypeRegular()
	{
		ProductCategory category1 = new ProductCategory(-1, "category1", type);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void testCategory_TypeNull()
	{
		ProductCategory category2 = new ProductCategory(-1, "category2", null);
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
