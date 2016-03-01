package velho.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import velho.model.BarcodeScanner;
import velho.model.Printer;
import velho.model.ProductBox;
import velho.model.ProductBoxSearchResultRow;
import velho.model.Shelf;
import velho.model.exceptions.NoDatabaseLinkException;

/**
 * @author Edward &amp; Jose Uusitalo
 */
public class ExternalSystemsController
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(ExternalSystemsController.class.getName());

	/**
	 * The {@link ManifestController}.
	 */
	private static ManifestController manifestController;

	/**
	 * @param manifestController
	 */
	public static void setControllers(final ManifestController manifestController)
	{
		ExternalSystemsController.manifestController = manifestController;
	}

	/**
	 * Prints received list, if list is empty it prints "List is empty." to the user.
	 *
	 * @param data
	 */
	@SuppressWarnings("unchecked")
	public static void sendDataToPrinter(final Object data)
	{
		if (data instanceof Collection)
		{
			final Collection<Object> dataList = (Collection<Object>) data;
			Iterator<Object> it = dataList.iterator();
			if (it.hasNext())
			{

				if (dataList.iterator().next() instanceof ProductBoxSearchResultRow)
				{
					it = dataList.iterator();
					final List<ProductBox> boxProduct = new ArrayList<ProductBox>();
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
	@SuppressWarnings("unchecked")
	public static void sendDataToBarcodeScanner(final Object data)
	{

		if (data instanceof Collection)
		{
			final Collection<Object> dataList = (Collection<Object>) data;
			Iterator<Object> it = dataList.iterator();
			if (it.hasNext())
			{

				if (dataList.iterator().next() instanceof ProductBoxSearchResultRow)
				{
					it = dataList.iterator();
					final List<ProductBox> boxProduct = new ArrayList<ProductBox>();
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
	 * @param productBoxCode the code that the Box posses.
	 * @param newShelfSlotID the Boxes former shelf id that it modifies.
	 * @return either a true or false, true when the prosses was compleated. False if not.
	 */
	public static boolean move(final int productBoxCode, final String newShelfSlotID, final boolean showPopup)
	{
		final String newShelfIDString = (String) Shelf.tokenizeShelfSlotID(newShelfSlotID)[0];
		final int newShelfID = Integer.parseInt(newShelfIDString.substring(1));

		String oldShelfIDString = null;
		int oldShelfID = -1;
		Shelf oldShelf = null;
		Shelf newShelf = null;
		ProductBox boxToMove = null;
		final boolean success = true;

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

			oldShelfIDString = (String) Shelf.tokenizeShelfSlotID(boxToMove.getShelfSlot())[0];
			oldShelfID = Integer.parseInt(oldShelfIDString.substring(1));
			oldShelf = DatabaseController.getShelfByID(oldShelfID, true);

			if (oldShelf == null)
			{
				return false;
			}

			if (oldShelf.removeFromSlot(boxToMove) == false)
			{
				return false;
			}
			if (newShelf.addToSlot(newShelfSlotID, boxToMove) == false)
			{
				return false;
			}

		}
		catch (final NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();

		}
		if (showPopup)
			DebugController.moveResult(productBoxCode, newShelfSlotID, success);

		return success;
	}

	public static void receiveManifestBarcode(final Set<ProductBox> boxSet, final Date orderDate, final int driverID)
	{
		SYSLOG.info("VelhoWM has received a manifest by driver " + driverID + " with " + boxSet.size() + " product boxes.");
		manifestController.receiveShipment(boxSet, orderDate, driverID);
	}
}
