package test.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import velho.model.ProductCategory;
import velho.model.ProductType;

/**
 * Tests for the {@link ProductCategory} class.
 *
 * @author Joona Silvennoinen
 */
public class ProductCategoryTest
{
	private ProductType type = new ProductType("Regular");
	private ProductCategory category = new ProductCategory("category1", type);

	@SuppressWarnings("unused")
	@Test
	public void testCategory_TypeRegular()
	{
		ProductCategory category1 = new ProductCategory("category1", type);
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
