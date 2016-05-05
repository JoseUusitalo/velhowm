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
public abstract class CSVController
{
	/**
	 * Reads the specified CSV file and parses the data into objects of the specified type.
	 *
	 * @param <T> the type of the object a single line in the file represent
	 * @param filePath path to the csv file
	 * @param type the class the objects will instantiated as
	 * @return a {@link VelhoCsvParser} object containing the valid and invalid data
	 */
	public static <T extends Object> VelhoCsvParser<T> readCSVFile(final String filePath, final Class<T> type)
	{
		final HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<T>();
		strategy.setType(type);

		final VelhoCsvParser<T> csvReader = new VelhoCsvParser<T>();

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
