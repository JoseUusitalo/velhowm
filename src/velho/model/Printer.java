package velho.model;

public class Printer
{
	public static void print(final Object data)
	{
		System.out.println("printer received data");
		System.out.println(data.toString());
	}

}
