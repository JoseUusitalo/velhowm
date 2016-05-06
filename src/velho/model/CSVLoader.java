package velho.model;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.MappingStrategy;

import velho.controller.VelhoCsvParser;
import velho.controller.database.DatabaseController;

/**
 * <p>
 * A loader for reading CSV file data into the database.
 * </p>
 * <p>
 * NOTE: The method {@link #getInvalidDataObjects(Set)} must be overridden for the loader to work.
 * </p>
 *
 * @author Jose Uusitalo
 * @param <T> the type of the object a single line in the CSV file represents
 */
public class CSVLoader<T extends AbstractDatabaseObject>
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(CSVLoader.class.getName());

	/**
	 * The class the objects will instantiated as.
	 */
	private final Class<T> objectClass;

	/**
	 * The {@link MappingStrategy} used to map the CSV values to the Java object properties.
	 */
	private final MappingStrategy<T> strategy;

	/**
	 * A set of data loaded from the CSV file.
	 */
	private Set<T> dataset;

	/**
	 * @param objectClass
	 */
	public CSVLoader(final Class<T> objectClass)
	{
		this.objectClass = objectClass;
		strategy = new HeaderColumnNameMappingStrategy<T>();
		((HeaderColumnNameMappingStrategy<T>) strategy).setType(objectClass);
	}

	/**
	 * Reads the specified CSV file and parses the data into objects of the specified type.
	 *
	 * @param <T> the type of the object a single line in the file represents
	 * @param filePath path to the csv file
	 * @param objectClass the class the objects will instantiated as
	 * @return a {@link VelhoCsvParser} object containing the valid and invalid data
	 */
	private VelhoCsvParser<T> readCSVFile(final String csvFilePath)
	{
		final VelhoCsvParser<T> parser = new VelhoCsvParser<T>();

		try
		{
			parser.parseFile(strategy, csvFilePath);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return parser;
	}

	/**
	 * <p>
	 * Gets the contextually invalid objects from the specified set of technically valid objects.
	 * </p>
	 * <p>
	 * For example it is possible to create a {@link User} object that has both a PIN and a badge number, but the application assumes that all users have either
	 * a PIN or a badge number but not both.
	 * </p>
	 *
	 * @param validDataSet a set of valid objects
	 * @return a set of contextually invalid objects in the valid data set
	 */
	protected Set<T> getInvalidDataObjects(final Set<T> validDataSet)
	{
		return validDataSet;
	}

	/**
	 * <p>
	 * Loads the object data from the specified CSV file.
	 * </p>
	 * <p>
	 * NOTE: This method will not load anything if the {@link #getInvalidDataObjects(Set)} has not been overridden!
	 * </p>
	 *
	 * @param csvFilePath path to the CSV file to be read
	 */
	public void load(final String csvFilePath)
	{
		final VelhoCsvParser<T> parser = readCSVFile(csvFilePath);
		dataset = new HashSet<T>(parser.getData());

		SYSLOG.trace("Loaded " + dataset.size() + " " + objectClass.getSimpleName() + " objects from the CSV file.");

		if (parser.hasInvalidData())
			SYSLOG.warn("Sample " + objectClass.getSimpleName() + " CSV data has malformed data, skipped " + parser.getInvalidData().size() + " lines: "
					+ parser.getInvalidData());

		final Set<T> invalidDataSet = getInvalidDataObjects(dataset);

		if (!invalidDataSet.isEmpty())
		{
			SYSLOG.warn("Sample " + objectClass.getSimpleName() + " objects has " + invalidDataSet.size() + "/" + dataset.size() + " invalid objects.");
			dataset.removeAll(invalidDataSet);
		}
	}

	/**
	 * Saves the valid {@link AbstractDatabaseObject}s loaded from the CSV file into the database using {@link DatabaseController#batchSave(Set)}.
	 */
	public void save()
	{
		DatabaseController.batchSave(dataset);
	}
}
