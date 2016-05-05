package velho.controller;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.MappingStrategy;

/**
 * A stripped down customized version of {@link CsvToBean} for use in VELHO Warehouse Management.
 *
 * @author Jose Uusitalo
 * @param <T> the type a line in the csv file represents
 */
public class VelhoCsvReader<T> extends CsvToBean<T>
{
	/**
	 * A map of invalid CSV data where the key is the line number and the value contains a list of invalid values on that line.
	 */
	private Map<Long, List<String>> invalidData;

	/**
	 */
	public VelhoCsvReader()
	{
		invalidData = new HashMap<Long, List<String>>();
	}

	/**
	 * Checks if the csv file parsed by this VELHO csv reader contained invalid data.
	 * Will always return false until a file has been parsed.
	 *
	 * @return <code>true</code> if the parsed csv file contained invalid data
	 */
	public boolean hasInvalidData()
	{
		return !invalidData.isEmpty();
	}

	/**
	 * Gets the map of invalid CSV data where the key is the line number and the value contains a list of invalid values on that line.
	 * A value is considered invalid only if its type does not match the type required by the mutator method of a property.
	 *
	 * @return the map of invalid csv data
	 */
	public Map<Long, List<String>> getInvalidData()
	{
		return invalidData;
	}

	/**
	 * Parses values from the specified CSV file to a list of data.
	 * Reader is automatically closed.
	 *
	 * @param mapper mapping strategy for the bean.
	 * @param filePath path to the csv file to read
	 * @return a list of objects of the specified type parsed from the file
	 */
	@SuppressWarnings("resource")
	public List<T> parseFile(final MappingStrategy<T> mapper, final String filePath) throws IOException
	{
		invalidData.clear();

		final CSVReader reader = new CSVReader(new FileReader(filePath));
		final List<T> datalist = new ArrayList<T>();

		long lineProcessed = 0;
		String[] line = null;

		try
		{
			mapper.captureHeader(reader);
		}
		catch (Exception e)
		{
			reader.close();
			throw new RuntimeException("Error capturing CSV header!", e);
		}

		while (null != (line = reader.readNext()))
		{
			lineProcessed++;

			try
			{
				datalist.add(super.processLine(mapper, line));
			}
			catch (Exception e)
			{
				invalidData.put(lineProcessed, Arrays.asList(line));
			}
		}

		reader.close();

		return datalist;
	}
}
