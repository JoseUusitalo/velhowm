package velho.controller;

import velho.model.BarcodeScanner;
import velho.model.Shelf;
import velho.model.exceptions.NoDatabaseLinkException;

public class ExternalSystemsController
{

	public static void scannerMoveValid()
	{
		// TODO Auto-generated method stub
		BarcodeScanner.scannerMoveValid();
		return;
	}

	public static boolean move(final int productCode, final String shelfSlotCode)
	{

		String shelfidString = (String) Shelf.tokenizeShelfSlotID(shelfSlotCode)[0];
		int shelfid = Integer.parseInt(shelfidString.substring(1));
		Shelf shelf = null;
		try
		{
			shelf = DatabaseController.getShelfByID(shelfid);
		} catch (NoDatabaseLinkException e)
		{

			DatabaseController.tryReLink();
		}
		boolean success = true;
		DebugController.moveResult(productCode, shelfSlotCode, success);
		return success;

	}

}
