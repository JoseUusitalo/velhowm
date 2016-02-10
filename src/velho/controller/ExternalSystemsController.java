package velho.controller;

import velho.model.BarcodeScanner;
import velho.model.ProductBox;
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

	public static boolean move(final int productBoxCode, final String newShelfSlotID)
	{

		String newShelfIDString = (String) Shelf.tokenizeShelfSlotID(newShelfSlotID)[0];
		int newShelfID = Integer.parseInt(newShelfIDString.substring(1));

		String oldShelfIDString = null;
		int oldShelfID = -1;

		Shelf oldShelf = null;
		Shelf newShelf = null;
		ProductBox boxToMove = null;

		try
		{
			newShelf = DatabaseController.getShelfByID(newShelfID);
			boxToMove = DatabaseController.getProductBoxByID(productBoxCode);

			oldShelfIDString = (String) Shelf.tokenizeShelfSlotID(boxToMove.getShelfSlot())[0];
			oldShelfID = Integer.parseInt(oldShelfIDString.substring(1));
			oldShelf = DatabaseController.getShelfByID(oldShelfID);
		}
		catch (NoDatabaseLinkException e)
		{

			DatabaseController.tryReLink();
		}

		oldShelf.removeFromSlot(boxToMove);
		newShelf.addToSlot(newShelfSlotID, boxToMove);
		boolean success = true;
		DebugController.moveResult(productBoxCode, newShelfSlotID, success);
		return success;
	}
}
