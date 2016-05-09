package velho.controller.database;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.h2.jdbcx.JdbcConnectionPool;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import velho.controller.LocalizationController;
import velho.controller.LoginController;
import velho.controller.PopupController;
import velho.model.AbstractDatabaseObject;
import velho.model.Manifest;
import velho.model.ManifestState;
import velho.model.Product;
import velho.model.ProductBox;
import velho.model.ProductBoxSearchResultRow;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.model.ProductType;
import velho.model.RemovalList;
import velho.model.RemovalListState;
import velho.model.RemovalPlatform;
import velho.model.Shelf;
import velho.model.ShelfLevel;
import velho.model.ShelfSlot;
import velho.model.User;
import velho.model.data.SampleData;
import velho.model.enums.DatabaseFileState;
import velho.model.enums.DatabaseQueryType;
import velho.model.enums.DatabaseTable;
import velho.model.enums.UserRole;
import velho.model.interfaces.DatabaseObject;
import velho.view.MainWindow;

/**
 * The H2 database controller.
 *
 * @author Jose Uusitalo
 */
public abstract class DatabaseController
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger DBLOG = Logger.getLogger("dbLogger");

	/**
	 * The database URI on the local machine.
	 */
	private final static String DB_URI = "jdbc:h2:./data/velho;MV_STORE=FALSE;MVCC=TRUE;";

	/**
	 * User name for the system itself.
	 */
	private final static String USERNAME = "VELHOWM";

	/**
	 * The path to the database file in the file system.
	 */
	private final static String DB_FILEPATH = "./data/velho.h2.db";

	/**
	 * A pool where database connections are acquired from.
	 */
	private static JdbcConnectionPool connectionPool;

	/**
	 * The date format used by the database.
	 */
	private static final SimpleDateFormat H2_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * The Hibernate session factory.
	 */
	private static final SessionFactory SESSION_FACTORY = HibernateSessionFactory.getInstance();

	/**
	 * The value of hibernate.jdbc.batch_size property in hibernate.cfg.xml file.
	 */
	private static final int HIBERNATE_BATCH_SIZE = 30;

	/*
	 * ---- UI LISTS ----
	 */

	/**
	 * An observable list of {@link User} objects for display in the user
	 * interface.
	 */
	private static ObservableList<Object> observableUsers = FXCollections.observableArrayList();

	/**
	 * An observable list of {@link Product} objects for display in the user
	 * interface.
	 */
	private static ObservableList<Object> observableProducts = FXCollections.observableArrayList();

	/**
	 * An observable list of {@link ProductType} objects for display in the user
	 * interface.
	 */
	private static ObservableList<Object> observableProductTypes = FXCollections.observableArrayList();

	/**
	 * An observable list of {@link Product} search results for display in the
	 * user interface.
	 */
	private static ObservableList<Object> observableProductBoxSearchResults = FXCollections.observableArrayList();

	/**
	 * An observable list of {@link ProductBox} objects for display in the
	 * user interface.
	 */
	private static ObservableList<Object> observableProductBoxes = FXCollections.observableArrayList();

	/**
	 * An observable list of {@link RemovalList} objects for display in the user
	 * interface.
	 */
	private static ObservableList<Object> observableRemovalLists = FXCollections.observableArrayList();

	/**
	 * An observable list of {@link ProductCategory} objects for display in the
	 * user interface.
	 */
	private static ObservableList<Object> observableProductCategories = FXCollections.observableArrayList();

	/**
	 * An observable list of {@link ProductBrand} objects for display in the
	 * user interface.
	 */
	private static ObservableList<Object> observableProductBrands = FXCollections.observableArrayList();

	/**
	 * An observable list of {@link RemovalListState} objects for display in the
	 * user interface.
	 */
	private static ObservableList<Object> observableRemovalListStates = FXCollections.observableArrayList();

	/**
	 * An observable list of {@link RemovalListState} objects for display in the
	 * user interface.
	 */
	private static ObservableList<Object> observableManifests = FXCollections.observableArrayList();

	/**
	 * An observable list of {@link ManifestState} objects for display in the
	 * user interface.
	 */
	private static ObservableList<Object> observableManifestStates = FXCollections.observableArrayList();

	/**
	 * An observable list of {@link Shelf} objects for display in the
	 * user interface.
	 */
	private static ObservableList<Object> observableShelves = FXCollections.observableArrayList();

	/**
	 * An observable list of {@link RemovalPlatform} objects for display in the
	 * user interface.
	 */
	private static ObservableList<Object> observableRemovalPlatforms = FXCollections.observableArrayList();

	/*
	 * -------------------------------- PRIVATE DATABASE METHODS --------------------------------
	 */

	/**
	 * Checks if the database file exists.
	 *
	 * @return <code>true</code> if the database file exists
	 */
	private static boolean databaseExists()
	{
		final File db = new File(DB_FILEPATH);
		return (db.exists() && !db.isDirectory());
	}

	/**
	 * Checks for a database link and gets a new connection to the database for
	 * running statements.
	 *
	 * @return a database connection
	 */
	protected static Connection getConnection()
	{
		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
		}
		catch (final SQLException e)
		{
			if (e.getMessage().contains("Database may be already in use"))
			{
				DBLOG.info("Database is already in use.");
				PopupController.error(LocalizationController.getString("popUpDatabaseInUse"));
			}

			System.out.println(e);
		}
		return connection;
	}

	/*
	 * -------------------------------- PUBLIC DATABASE METHODS --------------------------------
	 */

	/**
	 * Formats the given date into a H2 date string.
	 *
	 * @param date date to format
	 * @return a string that can be inserted into the database
	 */
	public static String getH2DateFormat(final Date date)
	{
		return H2_DATE_FORMAT.format(date);
	}

	/**
	 * Formats the given H2 date string into a {@link Date} object.
	 *
	 * @param dateString date string to parse
	 * @return a Date object representing the string
	 */
	public static Date parseDateString(final String dateString) throws ParseException
	{
		return H2_DATE_FORMAT.parse(dateString);
	}

	/**
	 * Escapes all single and double quotes in the given string.
	 *
	 * @param sql string to escape
	 * @return escaped string
	 */
	@Deprecated
	public static String escape(final String sql)
	{
		String escaped = sql.replace("'", "''");
		return escaped.replace("\"", "\\\"");
	}

	/**
	 * Creates an SQL query out of the given data.
	 *
	 * @param type query command
	 * @param tableName name of the table
	 * @param joinOnCondition join tables according to this condition
	 * @param columns columns to select (can be <code>null</code>)
	 * @param columnValues values to set to the specified columns
	 * @param where conditions (can be <code>null</code>)
	 * @return an SQL query string
	 */
	@Deprecated
	protected static String sqlBuilder(final DatabaseQueryType type, final String tableName, final Map<DatabaseTable, String> joinOnCondition,
			final String[] columns, final Map<String, Object> columnValues, final List<String> where)
	{
		final StringBuilder sb = new StringBuilder();

		// Command
		sb.append(type.toString());
		sb.append(" ");

		// Columns
		if (columns != null)
		{
			final int count = columns.length;
			for (int i = 0; i < count; i++)
			{
				sb.append(columns[i]);
				if (i < count - 1)
					sb.append(", ");
			}
			sb.append(" ");
		}

		switch (type)
		{
			case INSERT:
				sb.append("INTO ");
				break;
			case UPDATE:
				break;
			default:
				sb.append("FROM ");
				break;
		}

		// Table name.
		sb.append(tableName.toLowerCase());

		// Join.
		if (joinOnCondition != null)
		{
			final Iterator<DatabaseTable> it = joinOnCondition.keySet().iterator();
			DatabaseTable key = null;

			while (it.hasNext())
			{
				sb.append(" LEFT JOIN ");

				key = it.next();

				sb.append(key.toString().toLowerCase());
				sb.append(" ON ");
				sb.append(joinOnCondition.get(key));
			}
		}

		// Insert values.
		if (columnValues != null)
		{
			sb.append(" SET ");

			final Iterator<String> it = columnValues.keySet().iterator();
			String key = null;

			while (it.hasNext())
			{
				key = it.next();

				sb.append(key);
				sb.append("=");

				final Object value = columnValues.get(key);

				// If value is not Integer or Double do not add apostrophes.
				if (value instanceof Integer || value instanceof Double)
				{
					sb.append(value);
				}
				else
				{
					sb.append("'");
					sb.append(value.toString());
					sb.append("'");

				}

				if (it.hasNext())
					sb.append(", ");
			}
		}

		// Conditionals.
		if (where != null && where.size() > 0)
		{
			sb.append(" WHERE ");
			final int size = where.size();

			for (int i = 0; i < size; i++)
			{
				sb.append(where.get(i));

				if (i < size - 1)
					sb.append(" AND ");
			}
		}

		sb.append(";");

		DBLOG.trace("[SQLBUILDER] " + escape(sb.toString()));

		return sb.toString();
	}

	/**
	 * Checks if a database link exists.
	 *
	 * @return <code>true</code> if a database link exists
	 */
	public static boolean isLinked()
	{
		return connectionPool != null;
	}

	/**
	 * Attempts to re-link the database.
	 */
	public static void tryReLink()
	{
		// Just in case.
		unlink();

		try
		{
			link();
		}
		catch (final ClassNotFoundException e)
		{
			e.printStackTrace();
		}

		PopupController.warning(LocalizationController.getString("popUpDatabaseConnectionLost"));
	}

	/**
	 * Creates the link to the database. Use {@link #unlink()} to close the
	 * connection.
	 *
	 * @return <code>true</code> if the link was created successfully
	 * @throws ClassNotFoundException when the H2 driver was unable to load
	 * @throws ExistingDatabaseLinkException when a database link already exists
	 */
	public static DatabaseFileState link() throws ClassNotFoundException
	{
		if (isLinked())
		{
			DBLOG.debug("Database already linked.");
			return DatabaseFileState.EXISTING;
		}

		// Load the driver.
		Class.forName("org.h2.Driver");

		String uri = DB_URI;
		DatabaseFileState state = null;

		/*
		 * If the database does not exists, it will be created with the
		 * connection pool.
		 * Otherwise add this check after the URI for extra security.
		 */
		if (databaseExists())
		{
			state = DatabaseFileState.EXISTING;
			uri += ";IFEXISTS=TRUE";
			DBLOG.debug("Database exists.");
		}
		else
		{
			state = DatabaseFileState.DOES_NOT_EXIST;
			DBLOG.info("Database does not exist, creating a new database.");
		}

		// Create a connection pool.
		connectionPool = JdbcConnectionPool.create(uri, USERNAME, "@_Vry $ECURE pword2");

		// Try getting a connection
		// If the database does not exist, it is created.
		try
		{
			final Connection c = getConnection();

			if (c != null)
				c.close();
		}
		catch (final SQLException e)
		{
			e.printStackTrace();
		}

		if (databaseExists())
		{
			if (state == DatabaseFileState.DOES_NOT_EXIST)
				state = DatabaseFileState.CREATED;

			if (isLinked())
				DBLOG.info("Database linked.");
			else
				DBLOG.info("Database linking failed.");
		}
		else
			DBLOG.info("Database creation failed.");

		return state;
	}

	/**
	 * Shuts down the connection to the database. Use {@link #link()} to connect
	 * to the database again.
	 */
	public static void unlink()
	{
		if (connectionPool != null)
		{
			connectionPool.dispose();
			connectionPool = null;
			DBLOG.info("Database unlinked.");
		}
		else
			DBLOG.debug("No database link to unlink.");
	}

	/*
	 * -------------------------------- PRIVATE GETTER METHODS
	 * --------------------------------
	 */

	/**
	 * Gets an object from the database with the given database ID.
	 *
	 * @param objectClass the name class of the {@link AbstractDatabaseObject}
	 * to get
	 * @param databaseID the database ID of the object
	 * @return the corresponding object or <code>null</code> for invalid ID
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	private static Object getByID(final Class<? extends AbstractDatabaseObject> objectClass, final int databaseID) throws HibernateException
	{
		if (databaseID < 1)
			return null;

		SESSION_FACTORY.getCurrentSession().beginTransaction();

		final Object result = SESSION_FACTORY.getCurrentSession().get(objectClass, databaseID);

		try
		{
			SESSION_FACTORY.getCurrentSession().getTransaction().commit();

		}
		catch (final HibernateException e)
		{
			SESSION_FACTORY.getCurrentSession().getTransaction().rollback();

			throw new HibernateException("Failed to commit.");
		}

		return result;
	}

	/**
	 * Gets the {@link UserRole} object according to the given ID.
	 *
	 * @param id the ordinal of the role
	 * @return the corresponding user role object or <code>null</code> if role
	 * was not found
	 */
	public static UserRole getRoleByID(final int id)
	{
		// TODO: Find a way to put the roles into the database.

		for (final UserRole role : UserRole.values())
		{
			if (role.ordinal() == id)
				return role;
		}

		return null;
	}

	/**
	 * Gets the {@link UserRole} object according to the given name.
	 *
	 * @param roleName the name of the role (case insensitive)
	 * @return the corresponding user role object or <code>null</code> if role was not found
	 */
	public static UserRole getUserRoleByName(final String roleName)
	{
		// TODO: Find a way to put the roles into the database.

		for (final UserRole role : UserRole.values())
		{
			if (role.getName().equalsIgnoreCase(roleName))
				return role;
		}

		return null;
	}

	/**
	 * Attempts to find a subset of {@link ProductBox} objects from the given
	 * list of product boxes that contain at
	 * least the wanted number of products.
	 * The algorithm attempts to find a set of boxes that are the closest match
	 * to the given wantedProductCount and will
	 * only go over this amount by the smallest possible number of products if
	 * no suitable product boxes exist in the
	 * list that would satisfy the wanted product count exactly.
	 * If the given list of boxes does not contain enough products in total to
	 * reach the wanted amount, the entire list
	 * is returned.
	 *
	 * @param boxes list of product box objects to search from
	 * @param wantedProductCount number of products wanted from the given
	 * product boxes
	 * @return a list of product boxes that either contains at least the wanted
	 * number of products, or if there were not
	 * enough products, the same list that was given
	 */
	private static List<ProductBox> getBoxesContainingAtLeastProducts(final List<ProductBox> boxes, final Integer wantedProductCount)
	{
		final Map<Integer, List<ProductBox>> boxProductCount = new TreeMap<Integer, List<ProductBox>>();
		List<ProductBox> nextSmallestBoxes = null;
		final List<ProductBox> resultingBoxes = new ArrayList<ProductBox>();

		int emptyCount = 0;
		int wantedWouldBeIndex = -1;
		int addCountIndex = -1;
		int productCountSum = 0;
		boolean lookingForLarger = false;

		// Build a list of product box content sizes.
		for (final ProductBox box : boxes)
		{
			// Ignore empty boxes.
			if (box.getProductCount() > 0)
			{
				// No product box recorded with this size?
				if (boxProductCount.get(box.getProductCount()) == null)
				{
					// Create the box array with the product count as key.
					boxProductCount.put(box.getProductCount(), new ArrayList<ProductBox>());
				}

				// Add the box to the array.
				boxProductCount.get(box.getProductCount()).add(box);
			}
			else
				emptyCount++;
		}

		if (emptyCount == 0)
			DBLOG.trace("Found " + boxes.size() + " product boxes.");
		else
			DBLOG.trace("Found " + boxes.size() + " product boxes of which " + emptyCount + " were empty.");

		final StringBuffer sb = new StringBuffer();
		sb.append("Found product box sizes:");

		for (final Integer i : boxProductCount.keySet())
			sb.append(i + " (x" + boxProductCount.get(i).size() + "),");

		DBLOG.trace(sb.toString());

		// Convert the ordered key set to an arrray for binary search.
		Integer[] boxSizeArray = new Integer[boxProductCount.size()];
		boxSizeArray = boxProductCount.keySet().toArray(boxSizeArray);

		// Find the index at which the wantedProductCount Integer would be in,
		// if it were in the array.
		wantedWouldBeIndex = -Arrays.binarySearch(boxSizeArray, wantedProductCount) - 1;
		addCountIndex = wantedWouldBeIndex;

		if (MainWindow.DEBUG_MODE)
			DBLOG.trace("Wanted box of size " + wantedProductCount + " would have been at index " + wantedWouldBeIndex + ".");

		// Keep adding boxes to the foundProducts until we reach the wanted
		// product count.
		while (productCountSum < wantedProductCount)
		{
			DBLOG.trace("Search status | Count: " + productCountSum + " / " + wantedProductCount + " | Would Be Index: " + wantedWouldBeIndex + " | Add Index: "
					+ addCountIndex + " / " + boxProductCount.size());

			// Looking for too few products, show the smallest box of that
			// product instead.
			if (wantedWouldBeIndex == 0)
				addCountIndex++;

			try
			{
				// The next smallest product boxes.
				// Can throw an IndexOutOfBounds exception.
				nextSmallestBoxes = boxProductCount.get(boxSizeArray[--addCountIndex]);

				if (MainWindow.DEBUG_MODE)
					DBLOG.trace("The next smallest product boxes at index " + addCountIndex + " are: " + nextSmallestBoxes);

				for (final ProductBox box : nextSmallestBoxes)
				{
					// Does adding this box to the set still keep the product
					// counter under the wanted amount?
					// OR does a box that small not exist?
					if (wantedWouldBeIndex == 0 || addCountIndex == 0 || ((productCountSum + box.getProductCount() <= wantedProductCount)))
					{
						// Add up the product count.
						productCountSum += box.getProductCount();

						if (MainWindow.DEBUG_MODE)
							DBLOG.trace(box + " added to list, current product count sum is " + productCountSum + ".");

						// Add the found box to the set.
						resultingBoxes.add(box);
					}
					else if (lookingForLarger)
					{
						// Allow going over the wanted size limit if looking for
						// a larger box.
						// Add up the product count.
						productCountSum += box.getProductCount();

						if (MainWindow.DEBUG_MODE)
							DBLOG.trace(box + " added to list, current product count sum is " + productCountSum + ".");

						// Add the found box to the set.
						resultingBoxes.add(box);

						// Found the box that we wanted.
						break;
					}
					else
					{
						// Else break the loop and find the next smallest boxes.
						if (MainWindow.DEBUG_MODE)
							DBLOG.trace("Adding " + box + " to list would put the product count over the limit.");

						break;
					}
				}
			}
			catch (final IndexOutOfBoundsException e)
			{
				if (wantedProductCount > productCountSum)
				{
					if (wantedWouldBeIndex + 1 < boxProductCount.size() - 1)
					{
						/*
						 * All boxes are not yet in the result.
						 * Still have not reached the wanted product count.
						 * Restart the loop at one higher index.
						 * Repeat until either the wanted count is reached or
						 * all products have been added to the
						 * result.
						 */
						DBLOG.trace("Unable to build a product box list of that size from smaller boxes. Going one size larger.");
						resultingBoxes.clear();
						productCountSum = 0;
						wantedWouldBeIndex++;
						addCountIndex = wantedWouldBeIndex;
						lookingForLarger = true;
					}
					else
					{
						DBLOG.debug("Unable to find that many products. Listing all available boxes.");
						break;
					}
				}
				else
				{
					DBLOG.error("Spooky untested code.");
					DBLOG.error("Search status | Count: " + productCountSum + " / " + wantedProductCount + " | Would Be Index: " + wantedWouldBeIndex
							+ " | Add Index: " + addCountIndex + " / " + boxProductCount.size());
				}
			}
		}

		return resultingBoxes;
	}

	/*
	 * -------------------------------- PUBLIC GETTER METHODS
	 * --------------------------------
	 */

	/**
	 * Gets a map of columns and column names for displaying {@link User}
	 * objects in table views.
	 *
	 * @param withDeleteColumn get the delete button column?
	 *
	 * @return a map where the key is the column value and value is the column
	 * name
	 */
	public static Map<String, String> getPublicUserDataColumns(final boolean withDeleteColumn)
	{
		final LinkedHashMap<String, String> cols = new LinkedHashMap<String, String>();
		cols.put("firstName", LocalizationController.getString("publicUserTableHeaderFirstName"));
		cols.put("lastName", LocalizationController.getString("publicUserTableHeaderLastName"));
		cols.put("roleName", LocalizationController.getString("publicUserTableHeaderRole"));

		if (withDeleteColumn)
			cols.put("deleteButton", LocalizationController.getString("publicProductTableDeleteButton"));

		return cols;
	}

	/**
	 * Gets a map of columns and column names for displaying {@link Product}
	 * objects in table views.
	 *
	 * @param withAddColumn get the add button column?
	 * @param withDeleteColumn get the delete button column?
	 *
	 * @return a map where the key is the column value and value is the column
	 * name
	 */
	public static Map<String, String> getProductDataColumns(final boolean withAddColumn, final boolean withDeleteColumn)
	{
		final LinkedHashMap<String, String> cols = new LinkedHashMap<String, String>();

		if (withAddColumn)
			cols.put("addButton", LocalizationController.getString("buttonAdd"));

		if (withDeleteColumn)
			cols.put("deleteButton", LocalizationController.getString("buttonDelete"));

		cols.put("databaseID", "ID");
		cols.put("name", "Name");
		cols.put("brand", "Brand");
		cols.put("category", "Category");
		cols.put("viewButton", "");

		return cols;
	}

	/**
	 * Gets a map of columns and column names for displaying {@link RemovalList}
	 * objects in table views.
	 *
	 * @return a map where the key is the column value and value is the column
	 * name
	 */
	public static Map<String, String> getRemovalListDataColumns()
	{
		final LinkedHashMap<String, String> cols = new LinkedHashMap<String, String>();
		cols.put("databaseID", LocalizationController.getString("publicRemovalTableHeaderID"));
		cols.put("state", LocalizationController.getString("publicRemovalTableHeaderState"));
		cols.put("size", LocalizationController.getString("publicRemovalTableHeaderSize"));
		cols.put("viewButton", LocalizationController.getString("publicRemovalTableViewButton"));

		// Only managers and administrators can delete lists.
		if (LoginController.getCurrentUser().getRole().compareTo(UserRole.MANAGER) >= 0)
			cols.put("deleteButton", LocalizationController.getString("publicRemovalTableDeleteButton"));

		return cols;
	}

	/**
	 * Gets a map of columns and column names for displaying
	 * {@link ProductBoxSearchResultRow} objects in table views.
	 *
	 * @param withAddColumn get the add button column?
	 * @param withRemoveColumn get the remove button column?
	 *
	 * @return a map where the key is the column value and value is the column
	 * name
	 */
	public static Map<String, String> getProductSearchDataColumns(final boolean withAddColumn, final boolean withRemoveColumn)
	{
		final LinkedHashMap<String, String> cols = new LinkedHashMap<String, String>();

		if (withAddColumn)
			cols.put("addButton", LocalizationController.getString("buttonAdd"));

		if (withRemoveColumn)
			cols.put("removeButton", LocalizationController.getString("buttonRemove"));

		cols.put("databaseID", LocalizationController.getString("publicProductSearchTableHeaderID"));
		cols.put("productName", LocalizationController.getString("publicProductSearchTableHeaderName"));
		cols.put("productBrand", LocalizationController.getString("publicProductSearchTableHeaderBrand"));
		cols.put("productCategory", LocalizationController.getString("publicProductSearchTableHeaderCategory"));
		cols.put("expirationDate", LocalizationController.getString("publicProductSearchTableHeaderExpires"));
		cols.put("boxID", LocalizationController.getString("publicProductSearchTableHeaderBoxID"));
		cols.put("boxShelfSlot", LocalizationController.getString("publicProductSearchTableHeaderShelfSlot"));
		cols.put("boxProductCount", LocalizationController.getString("publicProductSearchTableHeaderProductCount"));

		return cols;
	}

	/**
	 * Gets a map of columns and column names for displaying {@link Manifest}
	 * objects in table views.
	 *
	 * @return a map where the key is the column value and value is the column
	 * name
	 */
	public static Map<String, String> getManifestDataColumns()
	{
		final LinkedHashMap<String, String> cols = new LinkedHashMap<String, String>();
		cols.put("databaseID", LocalizationController.getString("publicManifestTableHeaderID"));
		cols.put("state", LocalizationController.getString("publicManifestTableHeaderState"));
		cols.put("size", LocalizationController.getString("publicManifestTableHeaderSize"));
		cols.put("driverID", LocalizationController.getString("publicManifestTableDriverID"));
		cols.put("orderedDate", LocalizationController.getString("publicManifestTableOrderedDate"));
		cols.put("receivedDate", LocalizationController.getString("publicManifestTableReceivedDate"));
		cols.put("viewButton", LocalizationController.getString("buttonView"));

		return cols;
	}

	/**
	 * Gets the {@link ProductType} object from the database with the given ID.
	 *
	 * @param id the product type database ID
	 * @return the corresponding product type object or <code>null</code> for
	 * invalid ID
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static ProductType getProductTypeByID(final int id) throws HibernateException
	{
		return (ProductType) getByID(ProductType.class, id);
	}

	/**
	 * Gets the {@link ProductCategory} object from the database with the given
	 * ID.
	 *
	 * @param id the product category database ID
	 * @return the corresponding product category object or <code>null</code>
	 * for invalid ID
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static ProductCategory getProductCategoryByID(final int id) throws HibernateException
	{
		return (ProductCategory) getByID(ProductCategory.class, id);
	}

	/**
	 * Gets the {@link ProductBrand} object from the database with the given ID.
	 *
	 * @param id the product brand database ID
	 * @return the corresponding product brand object or <code>null</code> for
	 * invalid ID
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static ProductBrand getProductBrandByID(final int id) throws HibernateException
	{
		return (ProductBrand) getByID(ProductBrand.class, id);
	}

	/**
	 * Gets a set of user role names.
	 *
	 * @return a set of user role names
	 */
	public static Set<String> getUserRoleNames()
	{
		// TODO: Use database.

		final UserRole[] roles = UserRole.values();
		final Set<String> names = new LinkedHashSet<String>();

		for (final UserRole role : roles)
		{
			names.add(role.getName());
		}

		return names;
	}

	/**
	 * Authenticates a user with the given badge ID string.
	 *
	 * @param badgeID a badge ID string
	 * @return a {@link User} object representing the authenticated user or
	 * <code>null</code> for invalid credentials
	 * @see User#isValidBadgeID(String)
	 */
	public static User authenticateBadgeID(final String badgeID)
	{
		SESSION_FACTORY.getCurrentSession().beginTransaction();

		// @formatter:off
		@SuppressWarnings("unchecked")
		List<User> result = SESSION_FACTORY.getCurrentSession().createQuery("from User where badgeID = :id")
			   	   	   .setParameter("id", badgeID)
			   	   	   .list();
		// @formatter:on

		try
		{
			SESSION_FACTORY.getCurrentSession().getTransaction().commit();
		}
		catch (final HibernateException e)
		{
			SESSION_FACTORY.getCurrentSession().getTransaction().rollback();
			throw e;
		}

		if (result.size() == 0)
			return null;
		else if (result.size() > 1)
			DBLOG.error("Multiple users with the same badge ID: " + badgeID);

		return result.get(0);
	}

	/**
	 * Authenticates a user with the given PIN string.
	 *
	 * @param pin is a PIN string
	 * @return a {@link User} object representing the authenticated user or
	 * <code>null</code> for invalid credentials
	 * @see User#isValidPIN(String)
	 */
	public static User authenticatePIN(final String firstName, final String lastName, final String pin)
	{
		SESSION_FACTORY.getCurrentSession().beginTransaction();

		// @formatter:off
		@SuppressWarnings("unchecked")
		List<User> result = SESSION_FACTORY.getCurrentSession().createQuery("from User "
																		 + "where firstName = :fn "
																		 + "and lastName = :ln "
																		 + "and pin = :pin")
								   	   	  .setParameter("fn", firstName)
								   	   	  .setParameter("ln", lastName)
								   	   	  .setParameter("pin", pin)
								   	   	  .list();
		// @formatter:on

		try
		{
			SESSION_FACTORY.getCurrentSession().getTransaction().commit();
		}
		catch (final HibernateException e)
		{
			SESSION_FACTORY.getCurrentSession().getTransaction().rollback();
			throw e;
		}

		if (result.size() == 0)
			return null;
		else if (result.size() > 1)
			DBLOG.error("Multiple users with the same data: " + firstName + " " + lastName + " " + pin);

		return result.get(0);
	}

	/**
	 * Gets the {@link Product} object from the database with the given ID.
	 *
	 * @param id the product database ID
	 * @return the corresponding product object or <code>null</code> for invalid
	 * ID
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static Product getProductByID(final int id) throws HibernateException
	{
		return (Product) getByID(Product.class, id);
	}

	/**
	 * Gets the {@link ProductBox} object from the database with the given ID.
	 *
	 * @param id the product box database ID
	 * @return the corresponding product box object or <code>null</code> for
	 * invalid ID
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static ProductBox getProductBoxByID(final int id) throws HibernateException
	{
		return (ProductBox) getByID(ProductBox.class, id);
	}

	/**
	 * Gets the {@link User} object from the database with the given ID.
	 *
	 * @param id the user database ID (use a negative number to denote a debug account)
	 * @return the corresponding user object, <code>null</code> if a user with that ID does not exist, or the currently
	 * logged in user (i.e. debug user) if the ID was negative
	 * @throws HibernateException when the query failed to commit and has been rolled back
	 */
	public static User getUserByID(final int id) throws HibernateException
	{
		// Debug account ID?
		if (id < 0)
			return LoginController.getCurrentUser();

		SESSION_FACTORY.getCurrentSession().beginTransaction();

		final User user = SESSION_FACTORY.getCurrentSession().get(User.class, id);

		SESSION_FACTORY.getCurrentSession().getTransaction().commit();

		return user;
	}

	/**
	 * Gets a {@link Product} from the database by its name.
	 *
	 * @param name unique name of the product
	 * @return the wanted product or <code>null</code> if the product is not
	 * present in the database
	 */
	public static Product getProductByName(final String name)
	{
		SESSION_FACTORY.getCurrentSession().beginTransaction();

		// @formatter:off
		@SuppressWarnings("unchecked")
		List<Product> result = SESSION_FACTORY.getCurrentSession().createQuery("from Product where name = :name")
			   	   	   .setParameter("name", name)
			   	   	   .list();
		// @formatter:on

		try
		{
			SESSION_FACTORY.getCurrentSession().getTransaction().commit();
		}
		catch (final HibernateException e)
		{
			SESSION_FACTORY.getCurrentSession().getTransaction().rollback();
			throw e;
		}

		if (result.size() == 0)
			return null;

		return result.get(0);
	}

	/**
	 * Gets the {@link Shelf} object from the database with the given ID.
	 *
	 * @param id the shelf database ID
	 * @return the corresponding shelf object or <code>null</code> for invalid
	 * ID
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static Shelf getShelfByID(final int id) throws HibernateException
	{
		return (Shelf) getByID(Shelf.class, id);
	}

	/**
	 * Gets the {@link ShelfLevel} object from the database with the given ID.
	 *
	 * @param id the shelf level database ID
	 * @return the corresponding shelf object or <code>null</code> for invalid
	 * ID
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static ShelfLevel getShelfLevelByID(final int id) throws HibernateException
	{
		return (ShelfLevel) getByID(ShelfLevel.class, id);
	}

	/**
	 * Gets the {@link ShelfSlot} object from the database with the given ID.
	 *
	 * @param id the shelf slot database ID
	 * @return the corresponding shelf object or <code>null</code> for invalid
	 * ID
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static ShelfSlot getShelfSlotByID(final int id) throws HibernateException
	{
		return (ShelfSlot) getByID(ShelfSlot.class, id);
	}

	/**
	 * Gets the {@link RemovalList} object from the database with the given ID.
	 *
	 * @param id the removal list database ID
	 * @return the corresponding removal list object or <code>null</code> for
	 * invalid ID
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static RemovalList getRemovalListByID(final int id) throws HibernateException
	{
		return (RemovalList) getByID(RemovalList.class, id);
	}

	/**
	 * Gets the {@link Manifest} object from the database with the given ID.
	 *
	 * @param id the manifest database ID
	 * @return the corresponding manifest object or <code>null</code> for
	 * invalid ID
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static Manifest getManifestByID(final int id) throws HibernateException
	{
		return (Manifest) getByID(Manifest.class, id);
	}

	/**
	 * Gets a list of {@link Product} database ID in the database.
	 *
	 * @return a list of integer product database IDs
	 */
	public static List<Integer> getProductCodeList()
	{
		final List<Object> result = getAllProducts();
		final List<Integer> ints = new ArrayList<Integer>();

		for (final Object p : result)
			ints.add(((AbstractDatabaseObject) p).getDatabaseID());

		return ints;
	}

	/**
	 * Gets an {@link ObservableList} of cached product search results.
	 *
	 * @return a cached list of products searched by the user
	 */
	public static ObservableList<Object> getObservableProductSearchResults()
	{
		return observableProductBoxSearchResults;
	}

	/**
	 * Searches the database for product boxes of the specified size.
	 *
	 * @param productData a map of data to search for where the key is the
	 * product box
	 * database ID and the value is the number of products
	 * @throws NoDatabaseLinkException
	 */
	@SuppressWarnings("unchecked")
	public static List<ProductBoxSearchResultRow> searchProductBoxByDataList(final Map<Integer, Integer> productData)
	{
		final List<ProductBoxSearchResultRow> foundProducts = FXCollections.observableArrayList();
		Integer wantedProductCount = null;
		List<ProductBox> boxes = null;

		// For every unique string representing a product.
		for (final Integer productID : productData.keySet())
		{
			boxes = new ArrayList<ProductBox>();
			wantedProductCount = productData.get(productID);

			DBLOG.debug("Looking for [" + productID + "] of size " + wantedProductCount);

			SESSION_FACTORY.getCurrentSession().beginTransaction();

			// First look for an exact amount.
			// @formatter:off
			boxes = SESSION_FACTORY.getCurrentSession().createQuery("from ProductBox as pb"
									  + " where pb.product.databaseID = :id"
									  + " and pb.productCount = :count"
									  + " order by pb.expirationDate asc")
				   	   	   .setParameter("id", productID)
				   	   	   .setParameter("count", wantedProductCount)
				   	   	   .list();
			// @formatter:on

			SESSION_FACTORY.getCurrentSession().getTransaction().commit();

			// Couldn't find a box with exactly the number of products wanted.
			if (boxes.isEmpty())
			{
				if (MainWindow.DEBUG_MODE)
					DBLOG.debug("Unable to find a product box with the wanted size of " + wantedProductCount + ". Looking from multiple boxes.");

				SESSION_FACTORY.getCurrentSession().beginTransaction();

				/*
				 * Remove the product count condition and find all product boxes
				 * with the wanted product ID.
				 */
				// @formatter:off
				boxes = SESSION_FACTORY.getCurrentSession().createQuery("from ProductBox as pb"
										  + " where pb.product.databaseID = :id"
										  + " order by pb.expirationDate asc")
						   	   .setParameter("id", productID)
						   	   .list();
				// @formatter:on

				SESSION_FACTORY.getCurrentSession().getTransaction().commit();

				// for (ProductBox b : boxes)
				// System.out.println(b.getExpirationDate());
				boxes = getBoxesContainingAtLeastProducts(boxes, wantedProductCount);
			}
			else if (boxes.size() > 1)
			{
				/*
				 * If multiple boxes are found with the exact wanted size,
				 * select one that will expire the soonest which
				 * is the first result with a non-null expiration date.
				 */
				ProductBox oldest = null;

				for (final ProductBox b : boxes)
				{
					if (b.getExpirationDate() != null)
					{
						oldest = b;
						break;
					}
				}

				boxes.clear();
				boxes.add(oldest);
			}

			for (final ProductBox box : boxes)
				foundProducts.add(new ProductBoxSearchResultRow(box));
		}

		// Remove nulls.
		foundProducts.removeAll(Collections.singleton(null));

		DBLOG.debug("Updating product box list search results.");
		observableProductBoxSearchResults.clear();
		observableProductBoxSearchResults.addAll(foundProducts);

		// Return the data for unit testing.
		return foundProducts;
	}

	/**
	 * Searches the database for product boxes with the given conditions and
	 * additionally from the given tables.
	 *
	 * @param where conditions in SQL format
	 * @param joins SQL join statements
	 * @return a list of found product boxes as
	 * {@link ProductBoxSearchResultRow} objects
	 * @throws NoDatabaseLinkException
	 */
	public static List<ProductBoxSearchResultRow> searchProductBox(final String identifier, final int productCount, final ProductBrand brand,
			final ProductCategory category, final LocalDate expiresStart, final LocalDate expiresEnd, final boolean canBeInRemovalList)
	{
		final List<ProductBoxSearchResultRow> foundProducts = FXCollections.observableArrayList();

		final StringBuilder sb = new StringBuilder();

		sb.append("from ProductBox as pb ");

		final int emptyLength = sb.length();

		if (identifier != null && !identifier.trim().isEmpty())
		{
			try
			{
				Integer.parseInt(identifier.trim());

				sb.append("where pb.databaseID = :boxID ");
			}
			catch (NumberFormatException e)
			{
				sb.append("where pb.product.name like :productName ");
			}
		}

		if (productCount >= 0)
		{
			if (sb.length() == emptyLength)
				sb.append("where ");
			else
				sb.append("and ");

			sb.append("pb.productCount = :productCount ");
		}

		if (brand != null)
		{
			if (sb.length() == emptyLength)
				sb.append("where ");
			else
				sb.append("and ");

			sb.append("pb.product.brand.databaseID = :brandID ");
		}

		if (category != null)
		{
			if (sb.length() == emptyLength)
				sb.append("where ");
			else
				sb.append("and ");

			sb.append("pb.product.category.databaseID = :categoryID ");
		}

		if (expiresStart != null)
		{
			if (sb.length() == emptyLength)
				sb.append("where ");
			else
				sb.append("and ");

			sb.append("pb.expirationDate >= :expDateStart ");
		}

		if (expiresEnd != null)
		{
			if (sb.length() == emptyLength)
				sb.append("where ");
			else
				sb.append("and ");

			sb.append("pb.expirationDate <= :expDateEnd ");
		}

		if (!canBeInRemovalList)
		{
			if (sb.length() == emptyLength)
				sb.append("where ");
			else
				sb.append("and ");

			sb.append("pb.removalList is null ");
		}

		SESSION_FACTORY.getCurrentSession().beginTransaction();

		final Query query = SESSION_FACTORY.getCurrentSession().createQuery(sb.toString().trim());

		final StringBuilder logsb = new StringBuilder();

		if (DBLOG.isDebugEnabled())
			logsb.append("Searching for a product box with query: " + sb.toString().trim() + "\n");

		if (identifier != null && !identifier.trim().isEmpty())
		{
			try
			{
				query.setParameter("boxID", Integer.parseInt(identifier.trim()));

				if (DBLOG.isDebugEnabled())
					logsb.append("\tboxID = " + Integer.parseInt(identifier.trim()) + "\n");
			}
			catch (NumberFormatException e)
			{
				query.setString("productName", "%%" + identifier.trim() + "%%");

				if (DBLOG.isDebugEnabled())
					logsb.append("\tproductName like %%" + identifier.trim() + "%%\n");
			}
		}

		if (productCount >= 0)
		{
			query.setParameter("productCount", productCount);

			if (DBLOG.isDebugEnabled())
				logsb.append("\tproductCount = " + productCount + "\n");
		}

		if (brand != null)
		{
			query.setParameter("brandID", brand.getDatabaseID());

			if (DBLOG.isDebugEnabled())
				logsb.append("\tbrandID = " + brand.getDatabaseID() + "\n");
		}

		if (category != null)
		{
			query.setParameter("categoryID", category.getDatabaseID());

			if (DBLOG.isDebugEnabled())
				logsb.append("\tcategoryID = " + category.getDatabaseID() + "\n");
		}

		if (expiresStart != null)
		{
			final Date date = Date.from(expiresStart.atTime(0, 0).toInstant(ZoneOffset.of("Z")));
			query.setParameter("expDateStart", date);

			if (DBLOG.isDebugEnabled())
				logsb.append("\texpDateStart = " + expiresStart + "\n");
		}

		if (expiresEnd != null)
		{
			final Date date = Date.from(expiresEnd.atTime(0, 0).toInstant(ZoneOffset.of("Z")));
			query.setParameter("expDateEnd", date);

			if (DBLOG.isDebugEnabled())
				logsb.append("\texpDateEnd = " + date + "\n");
		}

		if (!canBeInRemovalList)
		{
			if (DBLOG.isDebugEnabled())
				logsb.append("\tremovalList is null\n");
		}

		if (DBLOG.isDebugEnabled())
			DBLOG.debug(logsb.toString());

		@SuppressWarnings("unchecked")
		List<ProductBox> result = query.list();

		try
		{
			SESSION_FACTORY.getCurrentSession().getTransaction().commit();
		}
		catch (final HibernateException e)
		{
			SESSION_FACTORY.getCurrentSession().getTransaction().rollback();
			throw e;
		}

		for (final ProductBox box : result)
			foundProducts.add(new ProductBoxSearchResultRow(box));

		DBLOG.trace("Updating product box search with " + result.size() + " results.");
		observableProductBoxSearchResults.clear();
		observableProductBoxSearchResults.addAll(foundProducts);

		// Return the data for unit testing.
		return foundProducts;
	}

	/**
	 * Gets the {@link RemovalListState} object from the database with the given
	 * ID.
	 *
	 * @param id the removal list state database ID
	 * @return the corresponding removal list state object or <code>null</code>
	 * for invalid ID
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static RemovalListState getRemovalListStateByID(final int id) throws HibernateException
	{
		return (RemovalListState) getByID(RemovalListState.class, id);
	}

	/**
	 * Gets the {@link ManifestState} object from the database with the given
	 * ID.
	 *
	 * @param id the manifest state database ID
	 * @return the corresponding manifest state object or <code>null</code> for
	 * invalid ID
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static ManifestState getManifestStateByID(final int id) throws HibernateException
	{
		return (ManifestState) getByID(ManifestState.class, id);
	}

	/**
	 * Gets the {@link RemovalPlatform} object from the database with the given
	 * ID.
	 *
	 * @param id the removal platform database ID
	 * @return the corresponding removal platform object or <code>null</code>
	 * for invalid ID
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static RemovalPlatform getRemovalPlatformByID(final int id)
	{
		return (RemovalPlatform) getByID(RemovalPlatform.class, id);
	}

	/*
	 * -------------------------------- PUBLIC SETTER METHODS
	 * --------------------------------
	 */

	/**
	 * Loads sample data into the database if it does not yet exist there.
	 * Assumes that a database exists.
	 *
	 * @return <code>true</code> if database changed as a result of this call
	 * @throws NoDatabaseLinkException
	 */
	public static boolean loadSampleData() throws HibernateException
	{
		// TODO: Find a way to return a proper boolean.
		return SampleData.createAll();
	}

	/**
	 * Deletes an object from the database.
	 *
	 * @param object object to delete
	 * @throws HibernateException when the object was not deleted
	 * @throws ConstraintViolationException when the object could not be deleted because it is referenced by a <code>not-null</code> property
	 */
	private static void delete(final Object object) throws HibernateException, ConstraintViolationException
	{
		SESSION_FACTORY.getCurrentSession().beginTransaction();

		SESSION_FACTORY.getCurrentSession().delete(object);

		try
		{
			SESSION_FACTORY.getCurrentSession().getTransaction().commit();

			DBLOG.debug("Deleted :" + object);
		}
		catch (final HibernateException e)
		{
			SESSION_FACTORY.getCurrentSession().getTransaction().rollback();

			DBLOG.debug("Failed to delete: " + object);

			throw e;
		}

		// TODO: Figure out some way to return a boolean.
	}

	/**
	 * Deletes a user from the database.
	 *
	 * @param user user to be deleted
	 * @return <code>true</code> if the specified user was deleted, <code>false</code> if the object is being referenced by another object that cannot have a
	 * null value in that property
	 */
	public static boolean deleteUser(final User user)
	{
		try
		{
			delete(user);
			observableUsers.remove(user);

			return true;
		}
		catch (final HibernateException e)
		{
			return false;
		}
	}

	/**
	 * Deletes a removal list from the database.
	 *
	 * @param list removal list to be deleted
	 * @return <code>true</code> if the specified removal list was deleted, <code>false</code> if the object is being referenced by another object that cannot
	 * have a null value in that property
	 */
	public static boolean deleteRemovalList(final RemovalList list) throws HibernateException, ConstraintViolationException
	{
		/*
		 * HIBERNATE NOTICE
		 * The child elements must be manually removed from the removal list collection before deleting the collection.
		 * If this is not done, Hibernate will attempt to delete all the child elements as well.
		 */

		if (list != null)
			list.clear();

		try
		{
			delete(list);
			observableRemovalLists.remove(list);

			return true;
		}
		catch (final HibernateException e)
		{
			return false;
		}
	}

	/**
	 * Deletes a product from the database.
	 *
	 * @param product product to be deleted
	 * @return <code>true</code> if the specified product was deleted, <code>false</code> if the object is being referenced by another object that cannot have a
	 * null value in that property
	 */
	public static boolean deleteProduct(final Product product)
	{
		try
		{
			delete(product);
			observableProducts.remove(product);

			return true;
		}
		catch (final HibernateException e)
		{
			return false;
		}
	}

	/**
	 * Deletes a product brand from the database.
	 *
	 * @param brand brand to be deleted
	 * @return <code>true</code> if the specified brand was deleted, <code>false</code> if the object is being referenced by another object that cannot have a
	 * null value in that property
	 */
	public static boolean deleteProductBrand(final ProductBrand brand)
	{
		try
		{
			delete(brand);
			observableProductBrands.remove(brand);

			return true;
		}
		catch (final HibernateException e)
		{
			return false;
		}
	}

	/**
	 * Deletes a product category from the database.
	 *
	 * @param category category to be deleted
	 * @return <code>true</code> if the specified category was deleted, <code>false</code> if the object is being referenced by another object that cannot have
	 * a null value in that property
	 */
	public static boolean deleteProductCategory(final ProductCategory category)
	{
		try
		{
			delete(category);
			observableProductCategories.remove(category);

			return true;
		}
		catch (final HibernateException e)
		{
			return false;
		}
	}

	/**
	 * Deletes a product box from the database.
	 *
	 * @param box box to be deleted
	 * @return <code>true</code> if the specified box was deleted, <code>false</code> if the object is being referenced by another object that cannot have a
	 * null value in that property
	 */
	public static boolean deleteProductBox(final ProductBox box)
	{
		try
		{
			delete(box);
			observableProductBoxes.remove(box);

			return true;
		}
		catch (final HibernateException e)
		{
			return false;
		}
	}

	/**
	 * Deletes a product type from the database.
	 *
	 * @param type type to be deleted
	 * @return <code>true</code> if the specified type was deleted, <code>false</code> if the object is being referenced by another object that cannot have a
	 * null value in that property
	 */
	public static boolean deleteProductType(final ProductType type)
	{
		try
		{
			delete(type);
			observableProductTypes.remove(type);

			return true;
		}
		catch (final HibernateException e)
		{
			return false;
		}
	}

	/*
	 * -------------------------------- SAVING DATA
	 * --------------------------------
	 */

	/**
	 * Creates a new or updates an existing object depending on whether the
	 * given object exists in the database.
	 *
	 * @param object new or existing {@link DatabaseObject} in the database
	 * @return the database ID of the inserted or updated object
	 * @throws HibernateException when data was not saved
	 */
	public static int saveOrUpdate(final DatabaseObject object)
	{
		int generatedID = object.getDatabaseID();

		try
		{
			SESSION_FACTORY.getCurrentSession().beginTransaction();

			if (object.getDatabaseID() < 1)
			{
				generatedID = (int) SESSION_FACTORY.getCurrentSession().save(object);
				DBLOG.debug("Saved: " + generatedID + " " + object);
			}
			else
			{
				SESSION_FACTORY.getCurrentSession().update(object);
				DBLOG.debug("Updated: " + object);
			}

			SESSION_FACTORY.getCurrentSession().flush();
			SESSION_FACTORY.getCurrentSession().clear();

			SESSION_FACTORY.getCurrentSession().getTransaction().commit();
		}
		catch (final Exception e)
		{
			SESSION_FACTORY.getCurrentSession().getTransaction().rollback();

			System.err.println(e.getMessage());
			throw e;
		}

		// TODO: Update observable lists.

		return generatedID;
	}

	/**
	 * Inserts a new object into the database.
	 *
	 * @param object new database object
	 * @return the database ID of the inserted object
	 * @throws HibernateException when data was not saved
	 */
	public static int save(final DatabaseObject object)
	{
		SESSION_FACTORY.getCurrentSession().beginTransaction();

		final int generatedID = (int) SESSION_FACTORY.getCurrentSession().save(object);

		SESSION_FACTORY.getCurrentSession().flush();

		try
		{
			SESSION_FACTORY.getCurrentSession().getTransaction().commit();
		}
		catch (final HibernateException e)
		{
			SESSION_FACTORY.getCurrentSession().getTransaction().rollback();

			throw new HibernateException("Failed to commit.");
		}

		DBLOG.debug("Saved: " + generatedID + " " + object);

		// TODO: Update observable lists.

		return generatedID;
	}

	/**
	 * Batch saves the given set of {@link AbstractDatabaseObject}s.
	 * Batch saving is noticeable faster than saving each object in a collection individually.
	 *
	 * @param objects a set of objects to be saved to the database
	 */
	public static <T extends AbstractDatabaseObject> void batchSave(final Set<T> objects)
	{
		SESSION_FACTORY.getCurrentSession().beginTransaction();

		int count = 0;
		int saved = 0;

		for (final Object obj : objects)
		{
			/*
			 * HIBERNATE NOTE
			 *
			 * This fixes: org.hibernate.NonUniqueObjectException: A different object with the same identifier value was already associated with the session
			 *
			 * Example problem is:
			 * CategoryA (Java object ID 500, database ID 1) has a product type of Type1 (Java object ID 600, database ID 3).
			 * SESSION_FACTORY.getCurrentSession().save(CategoryA) is fine.
			 * CategoryB (Java object ID 530, database ID 2) has a product type of Type1 (Java object ID 710, database ID 3).
			 * SESSION_FACTORY.getCurrentSession().save(CategoryB) throws the exception because CategoryB refers to another INSTANCE of Type1.
			 *
			 * Hibernate uses OBJECT EQUALITY for comparing objects, not EQUALS.
			 * The Type1 product type refers to the same object in the database but is a different instance in Java for both objects.
			 * Apparently merging returns the first instance of referenced objects, which in the case of the example would change the type of CategoryB to point
			 * to the object Type1 (Java object ID 600, database ID 3).
			 *
			 * The more you know.
			 * Merging persists the specified object and gives it an identifier according to generator specified in the mapping.
			 * So far I have been using HashSets to store my example data.
			 * HashSets are not ordered.
			 * The example data in the CSV files is ordered and it is crucial that the objects are assigned database IDs in that very particular order.
			 * I cannot manually define the database ID of an object so Hibernate simply does so in a sequence starting at 1.
			 * So when the data is read from the CSV file into a HashSet the order of the elements is lost.
			 * This loop reads the objects from the unordered HashSet according to the hash of the object.
			 * In other words, the object that was defined on the first row in a CSV file is probably not the first element in the HashSet, thus its database ID
			 * is not going to be 1.
			 * The only reason the example data works at all is because I am assuming that the data gets inserted into the database in that order.
			 * The kind of problems where you spend an hour questioning reality only for it to be solved by writing a single word on a single line in a single
			 * file are the best kind of problems.
			 * Solution: use LinkedHashSet which preserves the insertion order.
			 */
			final Object object = SESSION_FACTORY.getCurrentSession().merge(obj);

			/*
			 * HIBERNATE NOTE
			 *
			 * This would have been nice to know a few weeks ago. In hindsight it is quite obvious.
			 * Saving an object to the database with SESSION_FACTORY.getCurrentSession().save(obj) causes the databaseID of the object to be recalculated
			 * because all mappings have the databaseID column set to <generator class="native" /> instead of <generator class="assigned" />.
			 * In short. This cannot be used to save sample data where the database ID is set manually.
			 *
			 * Additionally after hours of research I have not found a way to use assigned positive integers as IDs.
			 * You can either have fully assigned IDs, or generated IDs, but not both if we are only using positive integers.
			 * However, it is possible to have positive integers be generated values and negative integers be assigned IDs but I want to use positive integers
			 * for both which is not possible to implement in a reasonable fashion.
			 *
			 * I have a feeling you could just use a separate database table to keep track of used IDs for each database table and keep generating new IDs
			 * (using a custom sequence generator) as long as the generated ID hits an ID that is already in use (because it was manually assigned, generated
			 * IDs do not collide) and finally return an unused ID. But that is just silly and slow.
			 */
			if (0 < (int) SESSION_FACTORY.getCurrentSession().save(object))
				saved++;

			count++;

			if (count % HIBERNATE_BATCH_SIZE == 0)
			{
				// Flush a batch of inserts and release memory, improving batch insert speed.
				SESSION_FACTORY.getCurrentSession().flush();
				SESSION_FACTORY.getCurrentSession().clear();
			}
		}

		SESSION_FACTORY.getCurrentSession().getTransaction().commit();

		// The log message will be wrong if the set contains objects of different types but whatever.
		if (!objects.isEmpty())
			DBLOG.debug("Batch saved " + saved + "/" + objects.size() + " " + objects.iterator().next().getClass().getSimpleName() + " objects.");
	}

	/**
	 * Batch updates the given set of {@link AbstractDatabaseObject}s.
	 * Batch updating is noticeable faster than updating each object in a collection individually.
	 *
	 * @param objects a set of objects to be updated in the database
	 */
	public static <T extends AbstractDatabaseObject> void batchUpdate(final Set<T> objects)
	{
		SESSION_FACTORY.getCurrentSession().beginTransaction();

		int count = 0;

		for (final Object obj : objects)
		{
			SESSION_FACTORY.getCurrentSession().update(obj);
			count++;

			if (count % HIBERNATE_BATCH_SIZE == 0)
			{
				// Flush a batch of inserts and release memory, improving batch update speed.
				SESSION_FACTORY.getCurrentSession().flush();
				SESSION_FACTORY.getCurrentSession().clear();
			}
		}

		SESSION_FACTORY.getCurrentSession().getTransaction().commit();

		// The log message will be wrong if the set contains objects of different types but whatever.
		if (!objects.isEmpty())
			DBLOG.debug("Batch updated " + objects.size() + " " + objects.iterator().next().getClass().getSimpleName() + " objects.");
	}

	/*
	 * -------------------------------- GETTERS --------------------------------
	 */

	/**
	 * Loads all objects of the specified type from the database into memory.
	 *
	 * @param className the name of the Java class of the objects to get
	 * @return a list of objects
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	private static List<Object> getAll(final String className) throws HibernateException
	{

		SESSION_FACTORY.getCurrentSession().beginTransaction();

		@SuppressWarnings("unchecked")
		final List<Object> result = SESSION_FACTORY.getCurrentSession().createQuery("from " + className).list();

		try
		{
			SESSION_FACTORY.getCurrentSession().getTransaction().commit();
		}
		catch (final HibernateException e)
		{
			SESSION_FACTORY.getCurrentSession().getTransaction().rollback();
			throw e;
		}

		DBLOG.debug("Loaded all " + className + " objects.");

		return result;
	}

	/**
	 * Loads all {@link ProductBox} objects from the database into memory.
	 * Additionally loads the following objects into memory:
	 * <ul>
	 * <li>{@link ProductBrand}</li>
	 * <li>{@link ProductType}</li>
	 * <li>{@link ProductCategory}</li>
	 * <li>{@link Product}</li>
	 * <li>{@link ProductContainer}</li>
	 * </ul>
	 *
	 * @return a list of products in the database currently on shelves
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static ObservableList<Object> getAllProductBoxes() throws HibernateException
	{
		observableProductBoxes.clear();
		observableProductBoxes.addAll(getAll("ProductBox"));
		return observableProductBoxes;
	}

	/**
	 * Gets all badge IDs from the database.
	 *
	 * @return a list of badge id number strings
	 */
	public static List<String> getAllBadgeIDS()
	{
		final Session session = SESSION_FACTORY.openSession();

		// Transaction is created and closed automatically with the session.
		session.beginTransaction();

		@SuppressWarnings("unchecked")
		final List<String> result = session.createQuery("select badgeID from User where badgeID is not null").list();

		session.close();

		return result;
	}

	/**
	 * Loads all {@link RemovalPlatform} objects from the database into memory.
	 *
	 * @return a list of removal platforms in the database
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static ObservableList<Object> getAllRemovalPlatforms() throws HibernateException
	{
		observableRemovalPlatforms.clear();
		observableRemovalPlatforms.addAll(getAll("RemovalPlatform"));
		return observableRemovalPlatforms;
	}

	/**
	 * Loads all {@link Shelf} objects from the database into memory.
	 *
	 * @return a list of shelves in the database
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static ObservableList<Object> getAllShelves() throws HibernateException
	{
		observableShelves.clear();
		observableShelves.addAll(getAll("Shelf"));

		return observableShelves;
	}

	/**
	 * Loads all {@link ShelfLevel} objects from the database into memory.
	 *
	 * @return a list of shelf levels in the database
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static List<Object> getAllShelfLevels()
	{
		return getAll("ShelfLevel");
	}

	/**
	 * Loads all {@link ShelfSlot} objects from the database into memory.
	 *
	 * @return a list of shelf slots in the database
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static List<Object> getAllShelfSlots()
	{
		return getAll("ShelfSlot");
	}

	/**
	 * Loads all {@link RemovalList} objects from the database into memory.
	 *
	 * @return a list of removal lists in the database
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static ObservableList<Object> getAllRemovalLists() throws HibernateException
	{
		observableRemovalLists.clear();
		observableRemovalLists.addAll(getAll("RemovalList"));

		return observableRemovalLists;
	}

	/**
	 * Loads all {@link User} objects from the database into memory.
	 *
	 * @return a list of users in the database
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static ObservableList<Object> getAllUsers() throws HibernateException
	{
		observableUsers.clear();
		observableUsers.addAll(getAll("User"));

		return observableUsers;
	}

	public static Set<UserRole> getAllUserRoles()
	{
		// TODO: Find a way to put roles into the database.
		return new LinkedHashSet<UserRole>(Arrays.asList(UserRole.values()));
	}

	/**
	 * Loads all {@link Product} objects from the database into memory.
	 *
	 * @return a list of products in the database currently on shelves
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static ObservableList<Object> getAllProducts() throws HibernateException
	{
		observableProducts.clear();
		observableProducts.addAll(getAll("Product"));

		return observableProducts;
	}

	/**
	 * Loads all {@link ProductCategory} objects from the database into memory.
	 *
	 * @return an {@link ObservableList} of all product categories
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static ObservableList<Object> getAllProductCategories() throws HibernateException
	{
		observableProductCategories.clear();
		observableProductCategories.addAll(getAll("ProductCategory"));

		return observableProductCategories;
	}

	/**
	 * Loads all {@link ProductBrand} objects from the database into memory.
	 *
	 * @return an {@link ObservableList} of all product brands
	 * @throws HibernateException when the query failed to commit and has been
	 * rolled back
	 */
	public static ObservableList<Object> getAllProductBrands() throws HibernateException
	{
		// TODO: Can this return a list of ProductBrand objects? Will the JavaFX
		// lists still work?

		observableProductBrands.clear();
		observableProductBrands.addAll(getAll("ProductBrand"));
		return observableProductBrands;
	}

	/**
	 * Loads all {@link RemovalListState} objects from the database into memory.
	 *
	 * @return an {@link ObservableList} of all removal list states
	 */
	public static ObservableList<Object> getAllRemovalListStates()
	{
		observableRemovalListStates.clear();
		observableRemovalListStates.addAll(getAll("RemovalListState"));
		return observableRemovalListStates;
	}

	/**
	 * Loads all {@link Manifest} objects from the database into memory.
	 *
	 * @return an {@link ObservableList} of all manifests
	 */
	public static ObservableList<Object> getAllManifests()
	{
		observableManifests.clear();
		observableManifests.addAll(getAll("Manifest"));
		return observableManifests;
	}

	/**
	 * Gets an observable list of manifest states for changing a manifest's
	 * state.
	 * The list consists of states the currently logged in user is allowed to
	 * use in addition to the current state of
	 * the manifest regardless of whether the user is allowed to use it or not.
	 *
	 * @return an {@link ObservableList} of manifest states
	 */
	public static ObservableList<Object> getManifestStateChangeList(final ManifestState currentState)
	{
		final List<Object> states = getAll("ManifestState");

		observableManifestStates.clear();

		/*
		 * This is the list of states shown in the manifest view that allows the
		 * user to change the state of the list.
		 * Only Managers are allowed accept and reject manifests.
		 * Logisticians can also see the accepted/rejected state if it is the
		 * current state of the manifest.
		 */
		for (final Object state : states)
		{
			if (((ManifestState) state).getName().equals("Accepted") || ((ManifestState) state).getName().equals("Rejected"))
			{
				if (LoginController.userRoleIsGreaterOrEqualTo(UserRole.MANAGER) || ((ManifestState) state).compareTo(currentState) == 0)
					observableManifestStates.add(state);
			}
			else
				observableManifestStates.add(state);
		}

		return observableManifestStates;
	}

	/**
	 * Clears the observable list of product box search results.
	 */
	public static void clearSearchResults()
	{
		DBLOG.info("Clearing search results.");
		observableProductBoxSearchResults.clear();
	}

	/**
	 * Closes the session factory.
	 */
	public static void closeSessionFactory()
	{
		DBLOG.info("Closing session factory.");
		SESSION_FACTORY.close();
	}

	/**
	 * Gets the {@link ShelfSlot} object by a the {@link ShelfSlot#getSlotID()} string.
	 *
	 * @param shelfSlotID shelf slot ID string
	 * @return the shelf slot object
	 */
	public static ShelfSlot getShelfSlotBySlotID(final String shelfSlotID)
	{
		return getShelfByID(Shelf.shelfSlotIDToShelfDatabaseID(shelfSlotID)).getShelfSlot(shelfSlotID);
	}

	/**
	 * Makes the product types and observable in the Hibernate
	 *
	 * @return Observable Product Types
	 * @throws HibernateException this should be the new database to get the information from
	 */
	public static ObservableList<Object> getAllProductTypes() throws HibernateException
	{
		observableProductTypes.clear();
		observableProductTypes.addAll(getAll("ProductType"));

		return observableProductTypes;
	}

	/**
	 * Checks if the table of the specified class has at least one entry in it.
	 *
	 * @param className name of the class whose table to check
	 * @return <code>true</code> if at least one record was found
	 */
	private static boolean tableHasEntries(final String className)
	{
		SESSION_FACTORY.getCurrentSession().beginTransaction();

		//@formatter:off
		@SuppressWarnings("unchecked")
		final List<Object> result = SESSION_FACTORY.getCurrentSession()
							.createQuery("from " + className)
							.setFetchSize(1)
							.setFirstResult(0)
							.setMaxResults(1)
							.list();
		//@formatter:on

		try
		{
			SESSION_FACTORY.getCurrentSession().getTransaction().commit();
		}
		catch (final HibernateException e)
		{
			SESSION_FACTORY.getCurrentSession().getTransaction().rollback();
			throw e;
		}

		return !result.isEmpty();
	}

	/**
	 * Checks if the database has user objects stored.
	 *
	 * @return <code>true</code> if database has users
	 */
	public static boolean hasUsers()
	{
		return tableHasEntries("User");
	}

	/**
	 * Checks if the database has product brand objects stored.
	 *
	 * @return <code>true</code> if database has product brands
	 */
	public static boolean hasProductBrands()
	{
		return tableHasEntries("ProductBrand");
	}

	/**
	 * Checks if the database has product type objects stored.
	 *
	 * @return <code>true</code> if database has product types
	 */
	public static boolean hasProductTypes()
	{
		return tableHasEntries("ProductType");
	}

	/**
	 * Checks if the database has product category objects stored.
	 *
	 * @return <code>true</code> if database has product categories
	 */
	public static boolean hasProductCategories()
	{
		return tableHasEntries("ProductCategory");
	}

	/**
	 * Checks if the database has product objects stored.
	 *
	 * @return <code>true</code> if database has products
	 */
	public static boolean hasProducts()
	{
		return tableHasEntries("Product");
	}

	/**
	 * Checks if the database has shelf objects stored.
	 *
	 * @return <code>true</code> if database has shelves
	 */
	public static boolean hasShelves()
	{
		return tableHasEntries("Shelf");
	}

	/**
	 * Checks if the database has shelf level objects stored.
	 *
	 * @return <code>true</code> if database has shelf levels
	 */
	public static boolean hasShelfLevels()
	{
		return tableHasEntries("ShelfLevel");
	}

	/**
	 * Checks if the database has shelf slot objects stored.
	 *
	 * @return <code>true</code> if database has shelf slots
	 */
	public static boolean hasShelfSlots()
	{
		return tableHasEntries("ShelfSlot");
	}

	/**
	 * Checks if the database has manifest state objects stored.
	 *
	 * @return <code>true</code> if database has manifest states
	 */
	public static boolean hasManifestStates()
	{
		return tableHasEntries("ManifestState");
	}

	/**
	 * Checks if the database has manifest objects stored.
	 *
	 * @return <code>true</code> if database has manifests
	 */
	public static boolean hasManifests()
	{
		return tableHasEntries("Manifest");
	}

	/**
	 * Checks if the database has removal list state objects stored.
	 *
	 * @return <code>true</code> if database has removal list states
	 */
	public static boolean hasRemovalListStates()
	{
		return tableHasEntries("RemovalListState");
	}

	/**
	 * Checks if the database has removal list objects stored.
	 *
	 * @return <code>true</code> if database has removal lists
	 */
	public static boolean hasRemovalLists()
	{
		return tableHasEntries("RemovalList");
	}

	/**
	 * Checks if the database has product box objects stored.
	 *
	 * @return <code>true</code> if database has product boxes
	 */
	public static boolean hasProductBoxes()
	{
		return tableHasEntries("ProductBox");
	}

	/**
	 * Checks if the database has removal platform objects stored.
	 *
	 * @return <code>true</code> if database has removal platforms
	 */
	public static boolean hasRemovalPlatforms()
	{
		return tableHasEntries("RemovalPlatform");
	}

	public static UserRole getUserByName(final String roleName)
	{
		for (final UserRole role : UserRole.values())
		{
			if (role.name().equals(roleName))
				return role;
		}

		return null;
	}

	public static ObservableList<Class> getValidDatabaseTypes()
	{
		final ObservableList<Class> classes = FXCollections.observableArrayList();

		//@formatter:off
		classes.addAll(	Manifest.class,
						ManifestState.class,
						Product.class,
						ProductBox.class,
						ProductBrand.class,
						ProductCategory.class,
						ProductType.class,
						RemovalList.class,
						RemovalListState.class,
						RemovalPlatform.class,
						Shelf.class,
						ShelfLevel.class,
						ShelfSlot.class,
						User.class);
		//@formatter:on

		return classes;
	}
}
