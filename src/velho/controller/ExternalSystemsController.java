package velho.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import velho.controller.database.DatabaseController;
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
@SuppressWarnings("static-method")
public class ExternalSystemsController
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(ExternalSystemsController.class.getName());

	/**
	 * The {@link ManifestController}.
	 */
	private ManifestController manifestController;

	/**
	 * A private inner class holding the class instance.
	 *
	 * @author Jose Uusitalo
	 */
	private static class Holder
	{
		/**
		 * The only instance of {@link ExternalSystemsController}.
		 */
		private static final ExternalSystemsController INSTANCE = new ExternalSystemsController();
	}

	/**
	 */
	private ExternalSystemsController()
	{
		// No need to instantiate this class.
	}

	/**
	 * Gets the instance of the {@link ExternalSystemsController}.
	 *
	 * @return the external systems controller
	 */
	public static synchronized ExternalSystemsController getInstance()
	{
		return Holder.INSTANCE;
	}

	/**
	 * @param manifestController
	 */
	public void setControllers(final ManifestController manifestController)
	{
		// TODO: remove
		this.manifestController = manifestController;
	}

	/**
	 * Attempts to print the received data, if the data is empty it shows
	 * "List is empty." to the user.
	 *
	 * @param data data to print
	 */
	@SuppressWarnings("unchecked")
	public void sendDataToPrinter(final Object data)
	{
		if (data instanceof Collection)
		{
			final Collection<Object> dataList = (Collection<Object>) data;
			Iterator<Object> iterator = dataList.iterator();
			if (iterator.hasNext())
			{

				if (dataList.iterator().next() instanceof ProductBoxSearchResultRow)
				{
					iterator = dataList.iterator();
					final List<ProductBox> boxProduct = new ArrayList<ProductBox>();
					while (iterator.hasNext())
					{
						boxProduct.add(((ProductBoxSearchResultRow) iterator.next()).getBox());
					}
					Printer.print(boxProduct);
				}
			}
			else
			{
				PopupController.getInstance().info(LocalizationController.getInstance().getString("listIsEmptyPopUp"));
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
	public void scannerMoveValid()
	{
		BarcodeScanner.scannerMoveValid();
	}

	/**
	 * Sends signal to barcode scanner.
	 *
	 * @param data data to send
	 */
	@SuppressWarnings("unchecked")
	public void sendDataToBarcodeScanner(final Object data)
	{
		if (data instanceof Collection)
		{
			final Collection<Object> dataList = (Collection<Object>) data;
			Iterator<Object> iterator = dataList.iterator();
			if (iterator.hasNext())
			{

				if (dataList.iterator().next() instanceof ProductBoxSearchResultRow)
				{
					iterator = dataList.iterator();
					final List<ProductBox> boxProduct = new ArrayList<ProductBox>();
					while (iterator.hasNext())
					{
						boxProduct.add(((ProductBoxSearchResultRow) iterator.next()).getBox());
					}
					BarcodeScanner.deviceBarcode(boxProduct);
				}
			}
			else
			{
				PopupController.getInstance().info(LocalizationController.getInstance().getString("listIsEmptyPopUp"));
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
	public boolean move(final int productBoxCode, final String newShelfSlotID, final boolean showPopup)
	{
		final ProductBox boxToMove = DatabaseController.getInstance().getProductBoxByID(productBoxCode);

		if (boxToMove == null)
		{
			SYSLOG.warn("Attempted to move null product box to " + newShelfSlotID + ".");

			if (showPopup)
				PopupController.getInstance().error(LocalizationController.getInstance().getString("attemptToMoveNotExistingProductBoxPopUp"));

			return false;
		}

		ShelfSlot newShelfSlot = null;
		Shelf newShelf = null;

		ShelfSlot oldShelfSlot = boxToMove.getShelfSlot();
		Shelf oldShelf = null;

		if (newShelfSlotID != null && !newShelfSlotID.isEmpty())
		{
			newShelfSlot = DatabaseController.getInstance().getShelfSlotBySlotID(newShelfSlotID);
			newShelf = newShelfSlot.getParentShelfLevel().getParentShelf();
		}

		if (oldShelfSlot != null)
		{
			if (oldShelfSlot.equals(newShelfSlot))
			{
				SYSLOG.debug("Product box " + boxToMove + " is already in the slot " + newShelfSlotID + ".");

				if (showPopup)
					PopupController.getInstance().info(LocalizationController.getInstance().getString("unableToMoveProductBoxToSameSlotPopUp") + newShelfSlot + "'.");

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
				PopupController.getInstance().error(LocalizationController.getInstance().getString("failureToRemoveProductBoxErrorPopUp"));

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
				PopupController.getInstance().error(LocalizationController.getInstance().getString("failureToAddProductBoxToNewShelfSlotErrorPopUp") + newShelfSlotID + "'.");

			return false;
		}

		DatabaseController.getInstance().saveOrUpdate(boxToMove);

		if (newShelfSlot != null)
			DatabaseController.getInstance().saveOrUpdate(newShelfSlot);

		if (oldShelfSlot != null)
			DatabaseController.getInstance().saveOrUpdate(oldShelfSlot);

		SYSLOG.debug("Successfully moved " + boxToMove + " to " + newShelfSlot);

		if (showPopup)
			PopupController.getInstance().info(LocalizationController.getInstance().getCompoundString("productBoxTransferSuccessMessage", productBoxCode, newShelfSlot));

		return true;
	}

	/**
	 * Receives the data read from a manifest barcode.
	 *
	 * @param boxSet a set of boxes on the manifest
	 * @param orderDate the date the manifest was ordered
	 * @param driverID the ID of the driver who delivered the boxes
	 */
	public void receiveManifestBarcode(final Set<ProductBox> boxSet, final Date orderDate, final int driverID)
	{
		SYSLOG.info("VELHOWM has received a manifest by driver " + driverID + " with " + boxSet.size() + " product boxes.");
		manifestController.receiveShipment(boxSet, orderDate, driverID);
	}

	/**
	 * Receives a badge ID from the badge scanner.
	 *
	 * @param badgeID badge identification string
	 */
	public void receiveBadgeID(final String badgeID)
	{
		// TODO: Observer model.

		SYSLOG.info("VELHOWM has received an RFID badge ID: " + badgeID);
		LoginController.getInstance().login(badgeID);
	}
}
