package velho.model;

public class Printer
{
	public static void print(final Object data)
	{
		System.out.println("Printer received data");
		System.out.println(data.toString());
	}

}
