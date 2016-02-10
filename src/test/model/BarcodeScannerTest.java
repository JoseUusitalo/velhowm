package test.model;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	public void testGetProductList()
	{
		List<Integer> proCodes = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11));
		int scannerCodes = BarcodeScanner.generateProductList(proCodes);
		// System.out.println(proCodes);
		// System.out.println(BarcodeScanner.generateProductList()+"scanner"+scannerCodes);
		// assertTrue(scannerCodes.size() <= proCodes.size()*2);
		fail("not yet implemented");
	}

	@Test
	public void testMoveValidProduct()
	{
		assertTrue(BarcodeScanner.scannerMoveValid());
	}

}