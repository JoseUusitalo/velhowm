package velho.controller;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.MappingStrategy;

/**
 * A stripped down customized version of {@link CsvToBean} for use in VELHO Warehouse Management.
 *
 * @author Jose Uusitalo
 * @param <T> the type a line in the csv file represents
 */
public class VelhoCsvParser<T> extends CsvToBean<T>
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(VelhoCsvParser.class.getName());

	/**
	 * The list of data read from the CSV file.
	 */
	private final List<T> datalist;

	/**
	 * A map of invalid CSV data where the key is the line number and the value contains a list of invalid values on that line.
	 */
	private final Map<Long, List<String>> invalidData;

	/**
	 */
	public VelhoCsvParser()
	{
		datalist = new ArrayList<T>();
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
	 * Gets the data parsed with the {@link #parseFile(MappingStrategy, String)} method.
	 *
	 * @return a list of objects of the specified type parsed from the file
	 */
	public List<T> getData()
	{
		return datalist;
	}

	/**
	 * Clears previous data from memory and parses values from the specified CSV file to a list of data.
	 *
	 * @param mapper mapping strategy for the bean.
	 * @param filePath path to the csv file to read
	 * @see #getData()
	 */
	@SuppressWarnings("resource")
	public void parseFile(final MappingStrategy<T> mapper, final String filePath) throws IOException
	{
		datalist.clear();
		invalidData.clear();

		SYSLOG.trace("Parsing: " + new File(filePath).getAbsolutePath());

		final CSVReader reader = new CSVReader(new FileReader(filePath));

		long lineProcessed = 0;
		String[] line = null;

		try
		{
			mapper.captureHeader(reader);
		}
		catch (final IOException e)
		{
			reader.close();
			throw new IOException("Error capturing CSV header.");
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
	}
}
