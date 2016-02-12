package test.model;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import velho.controller.DatabaseController;
import velho.model.BarcodeScanner;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseLinkException;

/**
 * @author Edward
 */
@SuppressWarnings("static-method")
public class BarcodeScannerTest
{

	@BeforeClass
	public final static void connectAndInitializeDatabase() throws ClassNotFoundException, NoDatabaseLinkException, ExistingDatabaseLinkException
	{
		assertTrue(DatabaseController.link());
		assertTrue(DatabaseController.initializeDatabase());
	}

	@AfterClass
	public final static void unlink() throws NoDatabaseLinkException
	{
		DatabaseController.unlink();
	}

	@Test
	public void testMoveValidProduct()
	{
		assertTrue(BarcodeScanner.scannerMoveValid());

	}

	@Test
	public void generateProductList()
	{
		int numbers = BarcodeScanner.generateProductList();
		assertNotNull(numbers);
		return;
	}
}