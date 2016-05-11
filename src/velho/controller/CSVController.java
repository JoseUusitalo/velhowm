package velho.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.opencsv.CSVWriter;

import javafx.scene.Node;
import velho.controller.database.DatabaseController;
import velho.controller.interfaces.UIActionController;
import velho.model.AbstractDatabaseObject;
import velho.model.CSVLoader;
import velho.model.User;
import velho.model.interfaces.DatabaseObject;
import velho.view.CSVView;
import velho.view.ListView;
import velho.view.MainWindow;

/**
 * A controller for writing CSV files.
 *
 * @author Jose Uusitalo
 */
public class CSVController implements UIActionController
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(CSVController.class.getName());

	/**
	 * A view for loading CSV files to the database.
	 */
	private CSVView loadCSVView;

	/**
	 * The main window view.
	 */
	private MainWindow mainWindow;

	/**
	 */
	public CSVController(final MainWindow mainWindow)
	{
		this.mainWindow = mainWindow;
		loadCSVView = new CSVView(this, DatabaseController.getValidDatabaseTypes());
	}

	/**
	 * Writes the given arrays of strings into the specified CSV file.
	 * Will overwrite existing files.
	 *
	 * @param filePath path to the CSV to be written
	 * @param lines a list of a string arrays containing the data
	 */
	public static void writeArraysToCSV(final String filePath, final List<String[]> lines)
	{
		String path = filePath;

		if (!filePath.endsWith(".csv"))
			path += ".csv";

		SYSLOG.debug("Writing CSV file: " + new File(path).getAbsolutePath());

		try (final FileWriter fw = new FileWriter(path); CSVWriter writer = new CSVWriter(fw, ',', '\0'))
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
	 * This method is slightly slower than using {@link CSVController#writeArraysToCSV(String, List)} directly as the specified collections must be converted to
	 * arrays first.
	 *
	 * @param filePath path to the CSV to be written
	 * @param lines a list of lists containing the data as strings
	 */
	public static void writeCollectionsToCSV(final String filePath, final List<Collection<String>> lines)
	{
		final List<String[]> arrays = new ArrayList<String[]>();

		for (final Collection<String> line : lines)
			arrays.add(line.toArray(new String[line.size()]));

		writeArraysToCSV(filePath, arrays);
	}

	/**
	 * Gets the CSV file header for the specified {@link DatabaseObject} class.
	 *
	 * @param <T> a class implementing {@link DatabaseObject}
	 * @param type the type to get the header for
	 * @return the CSV data header line
	 */
	public static <T extends DatabaseObject> String[] getCSVDataHeader(final Class<T> type)
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
				throw new IllegalArgumentException("Unsupported data type.");
		}
	}

	/**
	 * Writes the specified object into a string array of data that can be written to a CSV file with {@link #writeArraysToCSV(String, List)}.
	 *
	 * @param user database object whose data to convert into a string array
	 * @return an array of strings representing the specified object in a CSV file
	 */
	public static String[] objectToCSVDataArray(final User user)
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

	@SuppressWarnings({ "static-method", "rawtypes" })
	public void loadCSVFileToDatabase(final String filePath, final Class csvType)
	{
		@SuppressWarnings("unchecked")
		final CSVLoader<AbstractDatabaseObject> loader = new CSVLoader<AbstractDatabaseObject>(csvType);
		final int loaded = loader.load(filePath);
		final int saved = loader.save();

		if (saved == 0)
			PopupController.warning("Failed to load any " + csvType.getSimpleName() + " objects from " + filePath
					+ ". File probably contained data already present in the database.");
		else
			PopupController.info(saved + "/" + loaded + " " + csvType.getSimpleName() + " objects successfully loaded from: " + filePath);
	}

	/**
	 * Gets a view for loading CSV files to database.
	 *
	 * @return view for loading CSV files
	 */
	public Node getLoadCSVView()
	{
		return loadCSVView.getView(mainWindow.getStage());
	}

	@Override
	public void createAction(final Object data)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void updateAction(final Object data)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void addAction(final Object data)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAction(final Object data)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAction(final Object data)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void viewAction(final Object data)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void recreateViews(final ListView listView)
	{
		// TODO Auto-generated method stub

	}

	public static boolean isValidCSVFile(final File file)
	{
		return file != null && file.exists() && file.isFile() && file.getName().toLowerCase().endsWith(".csv");
	}
}
