package velhotest.model;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import velho.controller.database.DatabaseController;
import velho.controller.database.LogDatabaseController;
import velho.model.ProductBox;
import velho.model.ProductBoxSearchResultRow;

/**
 * Tests for the class {@link ProductBoxSearchResultRow}.
 * The tests are fairly meaningless as the class is a proxy for the {@link ProductBox} class.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class ProductBoxSearchResultRowTest
{
	private static ProductBox box = DatabaseController.getProductBoxByID(1);
	private static ProductBoxSearchResultRow row = new ProductBoxSearchResultRow(box);

	/**
	 * Creates the log database if needed and connects to it.
	 * Loads the sample data into the database if it does not yet exist.
	 *
	 * @throws Exception
	 */
	@BeforeClass
	public static final void init() throws Exception
	{
		LogDatabaseController.connectAndInitialize();
		DatabaseController.link();
		DatabaseController.loadSampleData();
	}

	/**
	 * Unlinks from both databases.
	 */
	@AfterClass
	public static final void unlinkDatabases() throws Exception
	{
		DatabaseController.unlink();
		LogDatabaseController.unlink();
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
