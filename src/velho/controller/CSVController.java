package velho.controller;

import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVWriter;

/**
 * A controller for reading and writing CSV files.
 *
 * @author Jose Uusitalo
 */
public abstract class CSVController
{
	public static void writeCSV()
	{
		CSVWriter writer = null;
		try
		{
			writer = new CSVWriter(new FileWriter("productbrands.csv"), '@');

			// feed in your array (or convert your data to an array)
			String[] entries = new String[] { "asd", "u348g" };

			writer.writeNext(entries);

			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
