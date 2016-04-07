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
 * Controller handling the communication with systems outside the Velho Warehouse Management.
 *
 * @author Edward Puustinen &amp; Jose Uusitalo
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
	 * Attempts to print the received data, if the data is empty it shows "List is empty." to the user.
	 *
	 * @param data data to print
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
	 * Sends signal to barcode scanner.
	 *
	 * @param data data to send
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
	 * Moves the specified box to the given shelf.
	 *
	 * @param productBoxCode the database ID of the box to move
	 * @param newShelfSlotID the ID of the shelf slot to move the box to
	 * @param showPopup show popup messages about failure/success?
	 * @return <code>true</code> if the box was moved successfully
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

			SYSLOG.debug("Moving box: " + boxToMove);
			SYSLOG.debug("To shelf: " + newShelfID);

			if (boxToMove == null)
			{
				return false;
			}

			if (newShelf == null)
			{
				return false;
			}

			oldShelfIDString = (String) Shelf.tokenizeShelfSlotID(boxToMove.getShelfSlot().getSlotID())[0];
			oldShelfID = Integer.parseInt(oldShelfIDString.substring(1));
			oldShelf = DatabaseController.getShelfByID(oldShelfID, true);

			SYSLOG.debug("From shelf: " + oldShelf);

			if (oldShelf == null)
			{
				return false;
			}

			if (boxToMove.getShelfSlot().removeBox(boxToMove) == false)
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
		{
			if (success)
				PopupController.info(productBoxCode + " was moved to " + newShelfSlotID + ".");
			else
				PopupController.error(
						productBoxCode + " was not moved to " + newShelfSlotID + ". Either the product box or the shelf does not exist in the database!");
		}

		return success;
	}

	/**
	 * Receives the data read from a manifest barcode.
	 *
	 * @param boxSet a set of boxes on the manifest
	 * @param orderDate the date the manifest was ordered
	 * @param driverID the ID of the driver who delivered the boxes
	 */
	public static void receiveManifestBarcode(final Set<ProductBox> boxSet, final Date orderDate, final int driverID)
	{
		SYSLOG.info("VelhoWM has received a manifest by driver " + driverID + " with " + boxSet.size() + " product boxes.");
		manifestController.receiveShipment(boxSet, orderDate, driverID);
	}
}
