package test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import velho.controller.DatabaseController;
import velho.model.ProductBox;
import velho.model.ProductBoxSearchResultRow;
import velho.model.enums.DatabaseFileState;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseLinkException;

/**
 * Tests for the class {@link ProductBoxSearchResultRow}.
 * The tests are fairly meaningless as the class is a proxy for the {@link ProductBox} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class ProductBoxSearchResultRowTest
{
	private static ProductBox box;
	private static ProductBoxSearchResultRow row;

	@BeforeClass
	public static void createBox() throws Exception
	{
		try
		{
			assertTrue(DatabaseController.link() != DatabaseFileState.DOES_NOT_EXIST);
			assertTrue(DatabaseController.initializeDatabase());
		}
		catch (ClassNotFoundException | ExistingDatabaseLinkException e)
		{
			e.printStackTrace();
		}
		catch (NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}

		box = DatabaseController.getProductBoxByID(1);
		row = new ProductBoxSearchResultRow(box);
	}

	@Test
	public final void testGetBox()
	{
		assertEquals(box, row.getBox());
	}

	@Test
	public final void testGetBoxProductCount()
	{
		assertEquals(box.getProductCount(), row.getBoxProductCount());
	}

	@Test
	public final void testGetBoxShelfSlot()
	{
		assertEquals(box.getShelfSlot(), row.getBoxShelfSlot());
	}

	@Test
	public final void testGetProductName()
	{
		assertEquals(box.getProduct().getName(), row.getProductName());
	}

	@Test
	public final void testGetProductID()
	{
		assertEquals(box.getProduct().getDatabaseID(), row.getProductID());
	}

	@Test
	public final void testGetProductBrand()
	{
		assertEquals(box.getProduct().getBrand(), row.getProductBrand());
	}

	@Test
	public final void testGetProductCategory()
	{
		assertEquals(box.getProduct().getCategory(), row.getProductCategory());
	}

	@Test
	public final void testGetBoxID()
	{
		assertEquals(box.getDatabaseID(), row.getBoxID());
	}
}
