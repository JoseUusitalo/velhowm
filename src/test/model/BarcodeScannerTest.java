package test.model;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import velho.model.BarcodeScanner;

public class BarcodeScannerTest
{


	@Test public void testGetProductList()
	{
		List<Integer> proCodes = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9,10,11));
		List<Integer> scannerCodes = BarcodeScanner.generateProductList(proCodes);
	    System.out.println(proCodes);
	    System.out.println(scannerCodes);
		assertTrue(proCodes.containsAll(scannerCodes));
	}

}