package velho.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BarcodeScanner
{
	/**
	 * Creates a random amount of random products via ArrayList
	 */
	public static List<Integer> generateProductList(List<Integer> numbers) {
//(int)(Math.random()*99999999+1)
		ArrayList<Integer> intList = new ArrayList<Integer>();
		int maxSize = (int)(Math.random()*numbers.size()+1);

		for(int i=0; i < maxSize; i++){
			intList.add(numbers.get(i));
		}
		Collections.shuffle(intList);
		return intList;
	}

}