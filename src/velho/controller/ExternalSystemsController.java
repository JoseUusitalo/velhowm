package velho.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import velho.model.BarcodeScanner;
import velho.model.Printer;
import velho.model.ProductBox;
import velho.model.ProductBoxSearchResultRow;
import velho.model.Shelf;
import velho.model.exceptions.NoDatabaseLinkException;

public class ExternalSystemsController
{
	/**
	 * Prints received list, if list is empty it prints "List is empty." to the user.
	 *
	 * @param data
	 */
	public static void sendDataToPrinter(final Object data)
	{
		if (data instanceof Collection)
		{
			Collection<Object> dataList = (Collection<Object>) data;
			Iterator<Object> it = dataList.iterator();
			if (it.hasNext())
			{

				if (dataList.iterator().next() instanceof ProductBoxSearchResultRow)
				{
					it = dataList.iterator();
					List<ProductBox> boxProduct = new ArrayList<ProductBox>();
					while (it.hasNext())
					{
						boxProduct.add(((ProductBoxSearchResultRow) it.next()).getBox());
					}
					Printer.print(boxProduct);
				}
			}
			else
			{
				PopupController.info("List is empty.");
			}
		}
		else
		{
			Printer.print(data);
		}
	}

	/**
	 * Carries out initial order from DebugController to BarcodeScanner
	 */
	public static void scannerMoveValid()
	{
		BarcodeScanner.scannerMoveValid();
	}

	/**
	 * Sends signal to barcode scanner
	 *
	 * @param received
	 */
	public static void sendDataToBarcodeScanner(final Object data)
	{

		if (data instanceof Collection)
		{
			Collection<Object> dataList = (Collection<Object>) data;
			Iterator<Object> it = dataList.iterator();
			if (it.hasNext())
			{

				if (dataList.iterator().next() instanceof ProductBoxSearchResultRow)
				{
					it = dataList.iterator();
					List<ProductBox> boxProduct = new ArrayList<ProductBox>();
					while (it.hasNext())
					{
						boxProduct.add(((ProductBoxSearchResultRow) it.next()).getBox());
					}
					BarcodeScanner.deviceBarcode(boxProduct);
				}
			}
			else
			{
				PopupController.info("List is empty.");
			}
		}
		else
		{
			BarcodeScanner.deviceBarcode(data);
		}
	}

	/**
	 * Moves the box from the shelf in question.
	 *
	 * @param productBoxCode
	 *            the code that the Box posses.
	 * @param newShelfSlotID
	 *            the Boxes former shelf id that it modifies.
	 * @return either a true or false, true when the prosses was compleated. False if not.
	 */
	public static boolean move(final int productBoxCode, final String newShelfSlotID, final boolean showPopup)
	{
		String newShelfIDString = (String) Shelf.tokenizeShelfSlotID(newShelfSlotID)[0];
		int newShelfID = Integer.parseInt(newShelfIDString.substring(1));

		String oldShelfIDString = null;
		int oldShelfID = -1;
		boolean boxWasNotInShelf = false;
		boolean boxSaveOnShelf = true;
		Shelf oldShelf = null;
		Shelf newShelf = null;
		ProductBox boxToMove = null;
		boolean success = true;

		try
		{

			newShelf = DatabaseController.getShelfByID(newShelfID, true);
			boxToMove = DatabaseController.getProductBoxByID(productBoxCode);

			if (boxToMove == null)
			{
				return false;
			}

			if (newShelf == null)
			{
				return false;
			}

			if (newShelf.addToSlot(newShelfSlotID, boxToMove) == false)
			{
				return false;
			}

			oldShelfIDString = (String) Shelf.tokenizeShelfSlotID(boxToMove.getShelfSlot())[0];
			oldShelfID = Integer.parseInt(oldShelfIDString.substring(1));
			oldShelf = DatabaseController.getShelfByID(oldShelfID, true);

			if (oldShelf == null)
			{
				return false;
			}
			if (oldShelf.addToSlot(newShelfSlotID, boxToMove) == false)
			{
				return false;
			}
			if (oldShelf.addToSlot(newShelfSlotID, boxToMove) == true)
			{
				return true;
			}

		}
		catch (NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
			if (e != null)
			{
				return false;
			}
		}
		if (showPopup)
			DebugController.moveResult(productBoxCode, newShelfSlotID, success);

		return success;
	}
}
