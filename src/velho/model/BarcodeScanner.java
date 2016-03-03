package velho.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import velho.controller.DatabaseController;
import velho.controller.ExternalSystemsController;
import velho.model.exceptions.NoDatabaseLinkException;

/**
 * Created to scan newly arrived product boxes. This class creates product boxes into the system.
 *
 * @author Edward
 */
public class BarcodeScanner
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(BarcodeScanner.class.getName());

	/**
	 * Receives the message from ProductListSeacrh
	 *
	 * @param received
	 */
	public static void deviceBarcode(final Object received)
	{
		SYSLOG.debug("Barcode Scanner received data: " + received.toString());
	}

	/**
	 * Generates the product barcodes trought the scanner and sorts them
	 * randomly.
	 *
	 * @param numbers returns an array of random numbers
	 * @return returns the total of numbers with maxSize of item/products
	 */
	public static int generateProductList(final List<Integer> numbers)
	{

		final int maxSize = (int) (Math.random() * (numbers.size() + 1));

		for (int i = 0; i < maxSize; i++)
		{
			numbers.add((int) (Math.random() * 99999999 + 1));
		}
		Collections.shuffle(numbers);
		return numbers.get(maxSize);
	}

	/**
	 * Returns random product code from ProductCodeList.
	 */
	public static int generateProductList()
	{

		List<Integer> numbers = new ArrayList<Integer>();
		try
		{
			numbers = DatabaseController.getProductCodeList();
		}
		catch (final NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}

		final int maximSize = (int) (Math.random() * (numbers.size() + 1));

		for (int i = 0; i < maximSize; i++)
		{
			numbers.add((int) (Math.random() * 99999999 + 1));
		}
		Collections.shuffle(numbers);
		return numbers.get(maximSize);
	}

	/**
	 * Validates the order to move the product/box/items.
	 *
	 * @return Either a true or a false, if there is room in the shelf or no room.
	 */
	public static boolean scannerMoveValid()
	{
		SYSLOG.info("Moving a box.");

		List<Integer> list = null;
		String shelf = null;

		Object[] tokens;

		try
		{
			Shelf shelfObj = null;
			do
			{
				list = DatabaseController.getProductCodeList();
				Collections.shuffle(list);
				shelf = DatabaseController.getRandomShelfSlot();
				tokens = Shelf.tokenizeShelfSlotID(shelf);
				int derp = Integer.parseInt(((String) tokens[0]).substring(1));
				SYSLOG.debug("Adding random product: " + list.get(0));
				SYSLOG.debug("To random shelf slot: " + shelf);
				shelfObj = DatabaseController.getShelfByID(derp, true);
			}
			while (shelfObj.slotHasFreeSpace(shelf) == false);

			if (ExternalSystemsController.move(list.get(0), shelf, true))
			{
				SYSLOG.debug("Move successful.");
				return true;
			}

			SYSLOG.debug("Move failed.");
			return false;
		}
		catch (final NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}
		return false;
	}

	/**
	 * Generates a list of random product boxes and sends it to the system to process as a manifest.
	 */
	public static void sendRandomShipment()
	{
		final Set<ProductBox> boxSet = new HashSet<ProductBox>();
		final Random r = new Random();
		List<Integer> productIDs;
		int driverID = r.nextInt(10000);
		long now = Instant.now().toEpochMilli();

		// 1 day is about 86400000 milliseconds.
		// 1 month is about 2628000000 milliseconds.
		// Range: [1 month ago, yesterday]
		Date orderDate = new Date((now - 2628000000L) + (long) (r.nextDouble() * ((now - 86400000L) - (now - 2628000000L))));

		try
		{
			productIDs = DatabaseController.getProductCodeList();
			Collections.shuffle(productIDs);

			int uniqueProducts = productIDs.size();
			int randomBoxes = r.nextInt(29) + 1; // Range: [1, 30]
			int maxSize;
			int productCount;
			Date randomDate;

			for (int i = 0; i < randomBoxes; i++)
			{
				maxSize = r.nextInt(499) + 1; // Range: [1, 500]
				productCount = r.nextInt(maxSize) + 1; // Range: [1, maxSize]

				// 10 years is about 315400000000 milliseconds.
				// Exact rounding is irrelevant so cast is fine.
				randomDate = new Date(now + (long) (r.nextDouble() * 315400000000L));

				// @formatter:off
				boxSet.add(new ProductBox(	-1,
											randomDate,
											maxSize,
											DatabaseController.getProductByID(productIDs.get(i % uniqueProducts)), // Make sure that the index doesn't go over the number of unique products in the database.
											productCount));
				// @formatter:on
			}
		}
		catch (final NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}

		SYSLOG.debug("Generated " + boxSet.size() + " random product boxes.");

		ExternalSystemsController.receiveManifestBarcode(boxSet, orderDate, driverID);
	}
}