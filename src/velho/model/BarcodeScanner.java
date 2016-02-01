package velho.model;

import java.util.Collections;
import java.util.List;

import velho.controller.DatabaseController;

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

		List<Integer> numbers = DatabaseController.getProductCodeList(1 - 20);
		int maximSize = (int) (Math.random() * (numbers.size() + 1));

		for (int i = 0; i < maximSize; i++)
		{
			numbers.add((int) (Math.random() * 99999999 + 1));
		}
		Collections.shuffle(numbers);
		return numbers.get(maximSize);
	}

	public static void getProductCode()
	{
		// TODO Auto-generated method stub
		DatabaseController.getProductCode();
		return;
	}

}