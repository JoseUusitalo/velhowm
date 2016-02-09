package velho.controller;

import velho.model.BarcodeScanner;
import velho.model.Shelf;

public class ExternalSystemsController
{

	public static void scannerMoveValid()
	{
		// TODO Auto-generated method stub
		BarcodeScanner.scannerMoveValid();
		System.out.println("homma toimii ehk");
		return;
	}

	public static void move(Integer integer, String shelfSlotCode)
	{
		// TODO Auto-generated method stub
		Shelf.tokenizeShelfSlotID(shelfSlotCode);
	}

}
