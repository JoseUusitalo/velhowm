package velhotest.model;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.junit.BeforeClass;
import org.junit.Test;

import velho.controller.DatabaseController;
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

	/**
	 * Loads the sample data into the database if it does not yet exist.
	 *
	 * @throws ParseException
	 */
	@BeforeClass
	public static final void loadSampleData() throws ParseException
	{
		DatabaseController.loadSampleData();
	}

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
