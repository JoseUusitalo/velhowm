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
import velho.model.ShelfSlot;

/**
 * Controller handling the communication with systems outside the VELHO
 * Warehouse Management.
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
	 * Attempts to print the received data, if the data is empty it shows
	 * "List is empty." to the user.
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
				PopupController.info(LocalizationController.getString("listIsEmptyPopUp"));
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
				PopupController.info(LocalizationController.getString("listIsEmptyPopUp"));
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
	 * @param newShelfSlot the shelf slot to move the box to
	 * @param showPopup show popup messages about failure/success?
	 * @return <code>true</code> if the box was moved successfully
	 */
	public static boolean move(final int productBoxCode, final String newShelfSlotID, final boolean showPopup)
	{
		final ProductBox boxToMove = DatabaseController.getProductBoxByID(productBoxCode);

		if (boxToMove == null)
		{
			SYSLOG.warn("Attempted to move null product box to " + newShelfSlotID + ".");

			if (showPopup)
				PopupController.error(LocalizationController.getString("attemptToMoveNotExistingProductBoxPopUp"));

			return false;
		}

		ShelfSlot newShelfSlot = null;
		Shelf newShelf = null;

		ShelfSlot oldShelfSlot = boxToMove.getShelfSlot();
		Shelf oldShelf = null;

		if (newShelfSlotID != null && !newShelfSlotID.isEmpty())
		{
			newShelfSlot = DatabaseController.getShelfSlotBySlotID(newShelfSlotID);
			newShelf = newShelfSlot.getParentShelfLevel().getParentShelf();
		}

		if (oldShelfSlot != null)
		{
			if (oldShelfSlot.equals(newShelfSlot))
			{
				SYSLOG.debug("Product box " + boxToMove + " is already in the slot " + newShelfSlotID + ".");

				if (showPopup)
					PopupController.info(LocalizationController.getString("unableToMoveProductBoxToSameSlotPopUp") + newShelfSlot + "'.");

				return false;
			}

			oldShelf = oldShelfSlot.getParentShelfLevel().getParentShelf();
		}

		//@formatter:off
		SYSLOG.debug("Moving Product Box: " + boxToMove + "\n"
					+ "\tFrom Shelf: " + oldShelf + "\n"
					+ "\tTo Shelf: " + newShelf + "\n"
					+ "\tTo Slot: " + newShelfSlot);
		//@formatter:on

		if (oldShelfSlot != null && !boxToMove.getShelfSlot().removeBox(boxToMove))
		{
			SYSLOG.error("Failed to remove product box " + boxToMove + " from shelf slot " + boxToMove.getShelfSlot());

			if (showPopup)
				PopupController.error(LocalizationController.getString("failureToRemoveProductBoxErrorPopUp"));

			return false;
		}

		if (newShelfSlot != null && !newShelfSlot.addBox(boxToMove))
		{
			SYSLOG.error("Failed to add product box " + boxToMove + " to shelf slot " + newShelfSlotID);
			// Add to new slot failed, but box was removed from old slot.
			// Add back to old slot.
			if (oldShelfSlot != null)
				oldShelfSlot.addBox(boxToMove);

			if (showPopup)
				PopupController.error(LocalizationController.getString("failureToAddProductBoxToNewShelfSlotErrorPopUp") + newShelfSlotID + "'.");

			return false;
		}

		DatabaseController.saveOrUpdate(boxToMove);

		if (newShelfSlot != null)
			DatabaseController.saveOrUpdate(newShelfSlot);

		if (oldShelfSlot != null)
			DatabaseController.saveOrUpdate(oldShelfSlot);

		SYSLOG.debug("Successfully moved " + boxToMove + " to " + newShelfSlot);

		if (showPopup)
			PopupController.info(LocalizationController.getCompoundString("productBoxTransferSuccessMessage", productBoxCode, newShelfSlot));

		return true;
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
		SYSLOG.info("VELHOWM has received a manifest by driver " + driverID + " with " + boxSet.size() + " product boxes.");
		manifestController.receiveShipment(boxSet, orderDate, driverID);
	}

	/**
	 * Receives a badge ID from the badge scanner.
	 *
	 * @param badgeID badge identification string
	 * @return
	 */
	public static void receiveBadgeID(final String badgeID)
	{
		// TODO: Observer model.

		SYSLOG.info("VELHOWM has received an RFID badge ID: " + badgeID);
		LoginController.login(badgeID);
	}
}
