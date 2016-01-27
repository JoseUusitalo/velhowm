package velho.controller;

import velho.model.exceptions.NoDatabaseConnectionException;

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
			Database.connect();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		try
		{
			Database.initializeDatabase();
		}
		catch (NoDatabaseConnectionException e)
		{
			System.out.println("ERROR: Unable to initialize database. No database connection established.");
			e.printStackTrace();
		}

		if (Database.isConnected())
			Database.shutdown();

		System.out.println("Done!");
	}
}
