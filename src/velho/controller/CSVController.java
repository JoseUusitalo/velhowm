package velho.controller;

import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVWriter;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

/**
 * A controller for reading and writing CSV files.
 *
 * @author Jose Uusitalo
 */
public class CSVController
{

	public static <T extends Object> VelhoCsvReader<T> readCSVFile(final String filePath, final Class<T> type)
	{
		final HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<T>();
		strategy.setType(type);

		final VelhoCsvReader<T> csvReader = new VelhoCsvReader<T>();

		try
		{
			csvReader.parseFile(strategy, filePath);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return csvReader;
	}

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
