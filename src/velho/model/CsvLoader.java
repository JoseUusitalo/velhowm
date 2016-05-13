package velho.model;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.MappingStrategy;

import velho.controller.LocalizationController;
import velho.controller.PopupController;
import velho.controller.database.DatabaseController;
import velho.model.interfaces.DatabaseObject;
import velho.model.interfaces.ObjectValidationStrategy;
import velho.model.strategies.ManifestStateValidationStrategy;
import velho.model.strategies.ManifestValidationStrategy;
import velho.model.strategies.ProductBoxValidationStrategy;
import velho.model.strategies.ProductBrandValidationStrategy;
import velho.model.strategies.ProductCategoryValidationStrategy;
import velho.model.strategies.ProductTypeValidationStrategy;
import velho.model.strategies.ProductValidationStrategy;
import velho.model.strategies.RemovalListStateValidationStrategy;
import velho.model.strategies.RemovalListValidationStrategy;
import velho.model.strategies.RemovalPlatformValidationStrategy;
import velho.model.strategies.ShelfLevelValidationStrategy;
import velho.model.strategies.ShelfSlotValidationStrategy;
import velho.model.strategies.ShelfValidationStrategy;
import velho.model.strategies.UserValidationStrategy;

/**
 * A loader for reading CSV file data into the database.
 *
 * @author Jose Uusitalo
 * @param <T> the type of the object a single line in the CSV file represents
 */
public class CsvLoader<T extends DatabaseObject>
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(CsvLoader.class.getName());

	/**
	 * The class the objects will instantiated as.
	 */
	private final Class<T> objectClass;

	/**
	 * The {@link MappingStrategy} used to map the CSV values to the Java object
	 * properties.
	 */
	private final MappingStrategy<T> strategy;

	/**
	 * A set of data loaded from the CSV file.
	 */
	private Set<T> dataset;

	/**
	 * The {@link ObjectValidationStrategy} used to validate the objects loaded with this loader.
	 */
	private final ObjectValidationStrategy validationStrategy;

	/**
	 * @param objectClass
	 */
	public CsvLoader(final Class<T> objectClass)
	{
		this.objectClass = objectClass;
		strategy = new HeaderColumnNameMappingStrategy<T>();
		((HeaderColumnNameMappingStrategy<T>) strategy).setType(objectClass);
		validationStrategy = getCorrectStrategy(objectClass.getSimpleName());
	}

	/**
	 * Gets the correct {@link ObjectValidationStrategy} for validating objects
	 * of the specified type.
	 *
	 * @param className the simple name of the class of the objects to be
	 *            validated
	 * @return the validation strategy for that class
	 */
	private static ObjectValidationStrategy getCorrectStrategy(final String className)
	{
		switch (className)
		{
			case "ManifestState":
				return new ManifestStateValidationStrategy();
			case "Manifest":
				return new ManifestValidationStrategy();
			case "ProductBox":
				return new ProductBoxValidationStrategy();
			case "ProductBrand":
				return new ProductBrandValidationStrategy();
			case "ProductCategory":
				return new ProductCategoryValidationStrategy();
			case "ProductType":
				return new ProductTypeValidationStrategy();
			case "Product":
				return new ProductValidationStrategy();
			case "RemovalListState":
				return new RemovalListStateValidationStrategy();
			case "RemovalList":
				return new RemovalListValidationStrategy();
			case "RemovalPlatform":
				return new RemovalPlatformValidationStrategy();
			case "ShelfLevel":
				return new ShelfLevelValidationStrategy();
			case "ShelfSlot":
				return new ShelfSlotValidationStrategy();
			case "Shelf":
				return new ShelfValidationStrategy();
			case "User":
				return new UserValidationStrategy();
			default:
				return null;
		}
	}

	/**
	 * <p>
	 * Loads the object data from the specified CSV file.
	 * </p>
	 *
	 * @param csvFilePath path to the CSV file to be read
	 * @param the number of objects loaded from the CSV file
	 */
	public int load(final String csvFilePath)
	{
		final VelhoCsvParser<T> parser = new VelhoCsvParser<T>();
		parser.parseFile(strategy, csvFilePath);

		/*
		 * It may be tempting to use a HashSet but that will cause the order of
		 * items to be lost.
		 * It is crucial that the dataset contains the items read from the CSV
		 * in the same order as they were defined.
		 */
		dataset = new LinkedHashSet<T>(parser.getData());

		SYSLOG.trace("Loaded " + dataset.size() + " " + objectClass.getSimpleName() + " objects from the CSV file.");

		if (parser.hasInvalidData())
			SYSLOG.warn("Sample " + objectClass.getSimpleName() + " CSV data has malformed data, skipped " + parser.getInvalidData().size() + " lines: "
					+ parser.getInvalidData());

		@SuppressWarnings("unchecked")
		final Set<DatabaseObject> invalidDataSet = validationStrategy.getInvalidObjects((Set<DatabaseObject>) dataset);

		if (!invalidDataSet.isEmpty())
		{
			SYSLOG.warn("Sample " + objectClass.getSimpleName() + " objects has " + invalidDataSet.size() + "/" + dataset.size() + " invalid objects.");
			PopupController.getInstance().warning(LocalizationController.getInstance().getCompoundString("CSVContainsInvalidObjectsNotice",
					objectClass.getSimpleName(), csvFilePath, invalidDataSet.size(), dataset.size()));
			dataset.removeAll(invalidDataSet);
		}

		return dataset.size();
	}

	/**
	 * Saves the valid {@link AbstractDatabaseObject}s loaded from the CSV file
	 * into the database using {@link DatabaseController#batchSave(Set)}.
	 *
	 * @return the number of objects saved to the database
	 */
	public int save()
	{
		if (dataset.isEmpty())
		{
			SYSLOG.trace("Nothing to save.");

			return 0;
		}

		return DatabaseController.getInstance().batchSave(dataset);
	}
}
