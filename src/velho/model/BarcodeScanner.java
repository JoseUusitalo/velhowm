package velho.model;

import java.util.Collections;
import java.util.List;

import velho.controller.DatabaseController;
import velho.model.exceptions.NoDatabaseLinkException;

/**
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
	public static int generateProductList(List<Integer> numbers)
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
			// TODO Auto-generated catch block
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

}