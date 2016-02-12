package velho.model;

import java.util.Collections;
import java.util.List;

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
	 * Generates the product barcodes trought the scanner and sorts them
	 * randomly.
	 *
	 * @param numbers
	 *            returns an array of random numbers
	 * @return returns the total of numbers with maxSize of item/products
	 */
	public static int generateProductList(final List<Integer> numbers)
	{

		int maxSize = (int) (Math.random() * (numbers.size() + 1));

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

		List<Integer> numbers = null;
		try
		{
			numbers = DatabaseController.getProductCodeList();
		}
		catch (NoDatabaseLinkException e)
		{
			e.printStackTrace();
		}
		int maximSize = (int) (Math.random() * (numbers.size() + 1));

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

		List<Integer> list = null;
		String shelf = null;

		try
		{
			list = DatabaseController.getProductCodeList();
			Collections.shuffle(list);
			shelf = DatabaseController.getRandomShelfSlot();
		}
		catch (NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}

		System.out.println("random product " + list.get(0));
		System.out.println("random shelf slot " + shelf);

		if (ExternalSystemsController.move(list.get(0), shelf))
		{
			System.out.println("Success");
			return true;
		}
		else
		{
			System.out.println("Pint Failed");
			return false;
		}

	}

}