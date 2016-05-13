package velho.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.opencsv.CSVWriter;
import com.opencsv.bean.BeanToCsv;
import com.opencsv.bean.ColumnPositionMappingStrategy;

import javafx.geometry.Insets;
import javafx.scene.Node;
import velho.controller.database.DatabaseController;
import velho.model.CSVLoader;
import velho.model.User;
import velho.model.interfaces.DatabaseObject;
import velho.view.CSVLoadView;
import velho.view.CSVWriteView;
import velho.view.MainWindow;
import velho.view.VerticalViewGroup;

/**
 * The singleton controller for writing and reading CSV files.
 *
 * @author Jose Uusitalo
 */
@SuppressWarnings("static-method")
public class CSVController
{
	/**
	 */
	private static final Logger SYSLOG = Logger.getLogger(CSVController.class.getName());

	/**
	 * A view for loading CSV files to the database.
	 */
	private final CSVLoadView loadCSVView;

	/**
	 * A view for writing CSV files to a file.
	 */
	private final CSVWriteView writeCSVView;

	/**
	 * The main window view.
	 */
	private MainWindow mainWindow;

	/**
	 * A private inner class holding the class instance.
	 *
	 * @author Jose Uusitalo
	 */
	private static class Holder
	{
		/**
		 * The only instance of {@link CSVController}.
		 */
		private static final CSVController INSTANCE = new CSVController();
	}

	/**
	 */
	private CSVController()
	{
		loadCSVView = new CSVLoadView(DatabaseController.getInstance().getValidDatabaseTypes());
		writeCSVView = new CSVWriteView(DatabaseController.getInstance().getValidDatabaseTypes());
	}

	/**
	 * Gets the instance of the {@link CSVController}.
	 *
	 * @return the CSV controller
	 */
	public static synchronized CSVController getInstance()
	{
		return Holder.INSTANCE;
	}

	/**
	 * Initializes this controller.
	 *
	 * @param main the {@link MainWindow}
	 */
	public void initialize(final MainWindow main)
	{
		this.mainWindow = main;
	}

	/**
	 * Writes the given arrays of strings into the specified CSV file.
	 * Will overwrite existing files.
	 *
	 * @param filePath path to the CSV to be written
	 * @param lines a list of a string arrays containing the data
	 */
	public void writeArraysToCSV(final String filePath, final List<String[]> lines)
	{
		String path = filePath;

		if (!filePath.endsWith(".csv"))
			path += ".csv";

		SYSLOG.debug("Writing CSV file: " + new File(path).getAbsolutePath());

		try (final FileWriter fileWriter = new FileWriter(path); CSVWriter writer = new CSVWriter(fileWriter, ',', '\0'))
		{
			writer.writeAll(lines);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Writes the list of strings into the specified CSV file.
	 * This method is slightly slower than using
	 * {@link CSVController#writeArraysToCSV(String, List)} directly as the
	 * specified collections must be converted to
	 * arrays first.
	 *
	 * @param filePath path to the CSV to be written
	 * @param lines a list of lists containing the data as strings
	 */
	public void writeCollectionsToCSV(final String filePath, final List<Collection<String>> lines)
	{
		final List<String[]> arrays = new ArrayList<String[]>();

		for (final Collection<String> line : lines)
			arrays.add(line.toArray(new String[line.size()]));

		writeArraysToCSV(filePath, arrays);
	}

	/**
	 * Gets the CSV file header for the specified {@link DatabaseObject} class.
	 *
	 * @param type the type to get the header for
	 * @return the CSV data header line
	 */
	public String[] getCSVDataHeader(final Class<? extends DatabaseObject> type)
	{
		switch (type.getSimpleName())
		{
			case "ProductBrand":
			case "ManifestState":
			case "ProductType":
			case "RemovalListState":
				return new String[] { "databaseID", "name" };
			case "Manifest":
				return new String[] { "databaseID", "driverID", "manifestStateID", "orderedDateString", "receivedDateString" };
			case "ProductBox":
				return new String[] { "databaseID", "manifestID", "removalListID", "shelfSlotID", "productID", "maxSize", "productCount",
						"expirationDateString" };
			case "ProductCategory":
				return new String[] { "databaseID", "name", "typeID" };
			case "Product":
				return new String[] { "databaseID", "name", "brandID", "categoryID" };
			case "RemovalList":
				return new String[] { "databaseID", "removalListStateID" };
			case "RemovalPlatform":
				return new String[] { "databaseID", "freeSpacePercent", "freeSpaceLeftWarningPercent" };
			case "ShelfLevel":
				return new String[] { "databaseID", "parentShelfID", "shelfPosition", "maxShelfSlots" };
			case "ShelfSlot":
				return new String[] { "databaseID", "parentShelfLevelID", "levelPosition", "maxProductBoxes" };
			case "Shelf":
				return new String[] { "databaseID", "levelCount" };
			case "User":
				return new String[] { "databaseID", "firstName", "lastName", "pin", "badgeID", "role" };
			default:
				throw new IllegalArgumentException("Unsupported data type: " + type.getSimpleName());
		}
	}

	/**
	 * Loads data from the specified CSV to database.
	 *
	 * @param filePath file path to the CSV file
	 * @param csvType the type of data to load from the CSV file
	 */
	public void loadCSVFileToDatabase(final String filePath, final Class<? extends DatabaseObject> csvType)
	{
		@SuppressWarnings({ "rawtypes", "unchecked" })
		final CSVLoader loader = new CSVLoader(csvType);
		final int loaded = loader.load(filePath);
		final int saved = loader.save();

		if (saved == 0)
			PopupController.getInstance()
					.warning(LocalizationController.getInstance().getCompoundString("failedToLoadAnyObjectsNotice", csvType.getSimpleName(), filePath));
		else
			PopupController.getInstance().info(
					LocalizationController.getInstance().getCompoundString("objectSuccesfullyLoadedNotice", saved, loaded, csvType.getSimpleName(), filePath));
	}

	/**
	 * Gets a view for loading CSV files to database.
	 *
	 * @return view for loading CSV files
	 */
	public Node getCSVView()
	{
		final VerticalViewGroup vvg = new VerticalViewGroup();
		vvg.setContents(loadCSVView.getView(mainWindow.getStage()), writeCSVView.getView(mainWindow.getStage()));

		vvg.getView().setPadding(new Insets(10));
		return vvg.getView();
	}

	/**
	 * Checks if the specified {@link File} is a file that exists and has the CSV extension.
	 *
	 * @param file file to be checked
	 * @return <code>true</code> of the specified file as a CSV file
	 */
	public boolean isValidCSVFile(final File file)
	{
		return file != null && file.exists() && file.isFile() && file.getName().toLowerCase().endsWith(".csv");
	}

	/**
	 * Writes the contents of a database table to a CSV file.
	 *
	 * @param filePath path to a CSV file
	 * @param classToWrite the {@link velho.model} class whose database table to write to a file
	 */
	@SuppressWarnings("unchecked")
	public void writeDatabaseTableToCSVFile(final String filePath, final Class<? extends DatabaseObject> classToWrite)
	{
		@SuppressWarnings("rawtypes")
		ColumnPositionMappingStrategy strategy = new ColumnPositionMappingStrategy();
		strategy.setType(classToWrite);
		strategy.setColumnMapping(getCSVDataHeader(classToWrite));

		String path = filePath;

		if (!filePath.endsWith(".csv"))
			path += ".csv";

		SYSLOG.debug("Writing CSV file: " + new File(path).getAbsolutePath());

		try (final FileWriter fileWriter = new FileWriter(path); CSVWriter writer = new CSVWriter(fileWriter, ',', '\0'))
		{
			@SuppressWarnings("rawtypes")
			BeanToCsv bean = new BeanToCsv();
			bean.write(strategy, writer, DatabaseController.getInstance().getAll(classToWrite.getSimpleName()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Writes the specified object into a string array of data that can be
	 * written to a CSV file with {@link #writeArraysToCSV(String, List)}.
	 *
	 * @param user database object whose data to convert into a string array
	 * @return an array of strings representing the specified object in a CSV
	 *         file
	 */
	public String[] objectToCSVDataArray(final User user)
	{
		// databaseID,firstName,lastName,pin,badgeID,role
		//@formatter:off
		return new String[] { String.valueOf(user.getDatabaseID()),
							  user.getFirstName(),
							  user.getLastName(),
							  user.getPin(),
							  user.getBadgeID(),
							  user.getRole().toString() };
		//@formatter:on
	}
}
