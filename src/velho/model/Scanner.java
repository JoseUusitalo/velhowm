package velho.model;

import java.util.ArrayList;

public class Scanner
{
	/**
	 * Creates a random amount of random products via ArrayList
	 */
	public static void generateProductList() {
		
		ArrayList<String> numbers = new ArrayList<String>();
		//8kpl * luku 9! suurin product koodi
		System.out.println("Done!");
		for(int i=0; i < 10; i++){
			numbers.add(String.valueOf((int)((Math.random()*99999999)+1)));
		}
		System.out.println(numbers);
	}
}