package velho.controller;

import velho.model.exceptions.NoDatabaseLinkException;

/**
 * The main controller for the VELHO Warehouse Management software.
 *
 * @author Jose Uusitalo
 */
public class Velho
{
	/**
	 * The main method for running this software.
	 *
	 * @param args
	 */
	public static void main(String[] args)
	{
		System.out.println("Running VELHO Warehouse Management.");

		try
		{
			DatabaseController.link();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		try
		{
			DatabaseController.initializeDatabase();
		}
		catch (NoDatabaseLinkException e)
		{
			System.out.println("ERROR: Unable to initialize database. No database connection established.");
			e.printStackTrace();
		}

		if (DatabaseController.isLinked())
			DatabaseController.unlink();

		System.out.println("Done!");
	}
}
