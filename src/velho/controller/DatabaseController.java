package velho.controller;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import velho.model.AbstractDatabaseObject;
import velho.model.HibernateSessionFactory;
import velho.model.Manifest;
import velho.model.ManifestState;
import velho.model.Product;
import velho.model.ProductBox;
import velho.model.ProductBoxSearchResultRow;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.model.ProductContainer;
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
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseException;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.model.exceptions.UniqueKeyViolationException;
import velho.model.interfaces.DatabaseObject;
import velho.view.MainWindow;

/**
 * The H2 database controller.
 *
 * @author Jose Uusitalo
 */
public class DatabaseController
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
	private static final SessionFactory sessionFactory = HibernateSessionFactory.getInstance();

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
	 * An observable list of {@link RemovalPlatform} objects for display in the user interface.
	 */
	private static ObservableList<Object> observableRemovalPlatforms = FXCollections.observableArrayList();

	/*
	 * -------------------------------- PRIVATE DATABASE METHODS --------------------------------
	 */

	/**
	 * Runs a database query with the given data.
	 *
	 * @param type query command
	 * @param tableName the {@link DatabaseTable}
	 * @param columns columns to select (can be <code>null</code>)
	 * @param columnValues values to set to the specified columns
	 * @param where conditions (can be <code>null</code>)
	 * @return
	 * 		<ul>
	 *         <li>if type is {@link DatabaseQueryType#UPDATE} or
	 *         {@link DatabaseQueryType#DELETE}: the number of rows that were
	 *         changed as a result of the query as an {@link Integer}</li>
	 *         <li>if type is {@link DatabaseQueryType#SELECT}: a Set containing
	 *         the selected data</li>
	 *         </ul>
	 * @throws NoDatabaseLinkException
	 */
	@Deprecated
	private static Object runQuery(final DatabaseQueryType type, final DatabaseTable tableName, final Map<DatabaseTable, String> joinOnValues, final String[] columns, final Map<String, Object> columnValues, final List<String> where) throws NoDatabaseLinkException
	{
		final Connection connection = getConnection();
		Statement statement = null;

		// Most other queries.
		final Set<Object> dataSet = new LinkedHashSet<Object>();

		try
		{
			ResultSet result = null;

			// Initialize a statement.
			statement = connection.createStatement();

			statement.execute(sqlBuilder(type, tableName.toString(), joinOnValues, columns, columnValues, where), Statement.RETURN_GENERATED_KEYS);

			switch (type)
			{
				case INSERT:
				case DELETE:
				case UPDATE:
					if (statement.getUpdateCount() > 0)
					{
						// Get the database rows that were created or an empty
						// set if none were created.
						result = statement.getGeneratedKeys();
						if (result != null)
						{
							// This is silly. Why isn't there an easier way to
							// get the number of rows in a ResultSet?
							result.last();
							final int rowCount = result.getRow();
							result.beforeFirst();

							if (rowCount == 0)
							{
								/*
								 * A successfull DELETE statement does not
								 * return anything but we need /something/ in
								 * the dataSet.
								 */
								dataSet.add(true);
							}
							else
							{
								// INSERT and UPDATE statements return the
								// updated/inserted rows.
								// Get all the IDs of the rows that were
								// updated.
								while (result.next())
									dataSet.add(result.getInt(1));
							}
						}

						try
						{
							if (result != null)
								result.close();
						}
						catch (final SQLException e)
						{
							e.printStackTrace();
						}
					}
					break;
				case SELECT:
					result = statement.getResultSet();

					if (columns.length == 1 && columns[0] != "*")
					{
						while (result.next())
							dataSet.add(result.getObject(columns[0]));
					}
					else
					{
						switch (tableName)
						{
							case USERS:
								while (result.next())
									dataSet.add(new User(result.getInt("user_id"), result.getString("first_name"), result.getString("last_name"), result.getString("pin"), result.getString("badge_id"), getRoleByID(result.getInt("role"))));
								break;

							/*
							 * case ROLES:
							 * if (columns.length == 1 && Arrays.asList(columns).contains("name"))
							 * {
							 * while (result.next())
							 * dataSet.add(result.getString("name"));
							 * }
							 * else if (columns.length == 1 && Arrays.asList(columns).contains("role_id"))
							 * {
							 * while (result.next())
							 * dataSet.add(result.getInt("role_id"));
							 * }
							 * break;
							 */
							case BRANDS:
								while (result.next())
									dataSet.add(new ProductBrand(result.getInt("brand_id"), result.getString("name")));
								break;

							case PRODUCTS:
								// @formatter:off
								while (result.next())
									dataSet.add(new Product(result.getInt("product_id"), result.getString("name"), getProductBrandByID(result.getInt("brand")), getProductCategoryByID(result.getInt("category"))));
								break;
							// @formatter:on

							case SHELVES:
								// @formatter:off
								while (result.next())
									dataSet.add(new Shelf(result.getInt("shelf_id"), result.getInt("levels")));
								break;
							// @formatter:on

							case REMOVALLIST_STATES:
								while (result.next())
									dataSet.add(new RemovalListState(result.getInt("removallist_state_id"), result.getString("name")));
								break;

							case REMOVALLISTS:
								// @formatter:off
								while (result.next())
									dataSet.add(new RemovalList(result.getInt("removallist_id"), getRemovalListStateByID(result.getInt("liststate"))));
								break;
							// @formatter:on

							case MANIFEST_STATES:
								while (result.next())
									dataSet.add(new ManifestState(result.getInt("manifest_state_id"), result.getString("name")));
								break;

							case REMOVALPLATFORMS:
								// @formatter:off
								while (result.next())
									dataSet.add(new RemovalPlatform(result.getInt("platform_id"), result.getDouble("free_space"), result.getDouble("free_space_warning")));
								break;
							// @formatter:on
							default:
								// Close all resources.
								try
								{
									result.close();
								}
								catch (final SQLException e)
								{
									e.printStackTrace();
								}

								try
								{
									statement.close();
								}
								catch (final SQLException e)
								{
									e.printStackTrace();
								}

								try
								{
									connection.close();
								}
								catch (final SQLException e)
								{
									e.printStackTrace();
								}
								throw new IllegalArgumentException();
						}
					}

					try
					{
						result.close();
					}
					catch (final SQLException e)
					{
						e.printStackTrace();
					}
					break;
				default:
					// Close all resources.
					try
					{
						statement.close();
					}
					catch (final SQLException e)
					{
						e.printStackTrace();
					}

					try
					{
						connection.close();
					}
					catch (final SQLException e)
					{
						e.printStackTrace();
					}
					throw new IllegalArgumentException();
			}
		}
		catch (final IllegalStateException e)
		{
			// Close all resources.
			try
			{
				if (statement != null)
					statement.close();
			}
			catch (final SQLException e1)
			{
				e.printStackTrace();
			}

			try
			{
				connection.close();
			}
			catch (final SQLException e2)
			{
				e.printStackTrace();
			}

			// Connection pool has been disposed = no database connection.
			throw new NoDatabaseLinkException();
		}
		catch (final SQLException e)
		{
			if (!e.toString().contains("Unique index or primary key violation"))
				e.printStackTrace();

			// If it was a UNIQUE constraint violation, continue normally as
			// those are handled separately.
			DBLOG.error("Silently ignored an SQL UNIQUE constraint violation. Begin message:");
			DBLOG.error(escape(e.getMessage()));
			DBLOG.error("End of message.");
		}

		// Close all resources.
		try
		{
			if (statement != null)
				statement.close();
		}
		catch (final SQLException e)
		{
			e.printStackTrace();
		}

		try
		{
			connection.close();
		}
		catch (final SQLException e)
		{
			e.printStackTrace();
		}

		switch (type)
		{
			case INSERT:
			case DELETE:
			case UPDATE:
				return dataSet;
			case SELECT:
				switch (tableName)
				{
					case USERS:
						// case ROLES:
					case TYPES:
					case BRANDS:
					case CATEGORIES:
					case PRODUCTS:
					case CONTAINERS:
					case SHELVES:
					case REMOVALLISTS:
					case REMOVALLIST_STATES:
					case MANIFESTS:
					case MANIFEST_STATES:
					case REMOVALPLATFORMS:
						return dataSet;
					default:
						throw new IllegalArgumentException();
				}
			default:
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Runs a raw SQL query on the database.
	 *
	 * @param sql SQL to run
	 * @return an Object containing the appropriate data
	 * @throws NoDatabaseLinkException
	 */
	private static Object runQuery(final String sql) throws NoDatabaseLinkException, UniqueKeyViolationException
	{
		final Connection connection = getConnection();
		Statement statement = null;

		// Update queries.
		Integer changed = new Integer(0);

		try
		{
			// Initialize a statement.
			statement = connection.createStatement();

			statement.execute(sql);

			changed = new Integer(statement.getUpdateCount());
		}
		catch (final IllegalStateException e)
		{
			// Close all resources.

			try
			{
				if (statement != null)
					statement.close();
			}
			catch (final SQLException e1)
			{
				e.printStackTrace();
			}

			try
			{
				connection.close();
			}
			catch (final SQLException e2)
			{
				e.printStackTrace();
			}

			// Connection pool has been disposed = no database connection.
			throw new NoDatabaseLinkException();
		}
		catch (final SQLException e)
		{
			if (!e.toString().contains("Unique index or primary key violation"))
				e.printStackTrace();

			// If it was a UNIQUE constraint violation, continue normally as
			// those are handled separately.
			DBLOG.error("Passing SQL UNIQUE constraint violation. Begin message:");
			DBLOG.error(escape(e.getMessage()));
			DBLOG.error("End of message.");

			// Close all resources.
			try
			{
				if (statement != null)
					statement.close();
			}
			catch (final SQLException e2)
			{
				e.printStackTrace();
			}

			try
			{
				connection.close();
			}
			catch (final SQLException e2)
			{
				e.printStackTrace();
			}

			throw new UniqueKeyViolationException();
		}

		// Close all resources.
		try
		{
			statement.close();
		}
		catch (final SQLException e)
		{
			e.printStackTrace();
		}

		try
		{
			connection.close();
		}
		catch (final SQLException e)
		{
			e.printStackTrace();
		}

		return changed;
	}

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
	private static Connection getConnection() throws NoDatabaseLinkException
	{
		checkLink();

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
				PopupController.error("Database is already in use. Please close the open application.");
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
	public static String sqlBuilder(final DatabaseQueryType type, final String tableName, final Map<DatabaseTable, String> joinOnCondition, final String[] columns, final Map<String, Object> columnValues, final List<String> where)
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
	 * Checks if a database link exists and throws a
	 * {@link NoDatabaseLinkException} exception if it doesn't. To be used
	 * when a database link must exist.
	 *
	 * @throws NoDatabaseLinkException
	 */
	public static void checkLink() throws NoDatabaseLinkException
	{
		if (connectionPool == null)
			throw new NoDatabaseLinkException();
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

		PopupController.warning("Database connection was temporarily lost. Please try again or restart the application.");
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
		catch (final NoDatabaseLinkException e)
		{
			e.printStackTrace();
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
	 * -------------------------------- PRIVATE GETTER METHODS --------------------------------
	 */

	/**
	 * Gets an object from the database with the given database ID.
	 *
	 * @param objectClass the name class of the {@link AbstractDatabaseObject} to get
	 * @param databaseID the database ID of the object
	 * @return the corresponding object or <code>null</code> for invalid ID
	 * @throws HibernateException when the query failed to commit and has been rolled back
	 */
	private static Object getByID(final Class<? extends AbstractDatabaseObject> objectClass, final int databaseID) throws HibernateException
	{
		if (databaseID < 1)
			return null;

		sessionFactory.getCurrentSession().beginTransaction();

		final Object result = sessionFactory.getCurrentSession().get(objectClass, databaseID);

		try
		{
			sessionFactory.getCurrentSession().getTransaction().commit();

		}
		catch (final HibernateException e)
		{
			sessionFactory.getCurrentSession().getTransaction().rollback();

			throw new HibernateException("Failed to commit.");
		}

		return result;
	}

	/**
	 * Gets the {@link UserRole} object according to the given ID.
	 *
	 * @param id the ordinal of the role
	 * @return the corresponding user role object or <code>null</code> if role was not found
	 */
	private static UserRole getRoleByID(final int id)
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
	 *            product boxes
	 * @return a list of product boxes that either contains at least the wanted
	 *         number of products, or if there were not
	 *         enough products, the same list that was given
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
			DBLOG.trace("Search status | Count: " + productCountSum + " / " + wantedProductCount + " | Would Be Index: " + wantedWouldBeIndex + " | Add Index: " + addCountIndex + " / " + boxProductCount.size());

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
					DBLOG.error("Search status | Count: " + productCountSum + " / " + wantedProductCount + " | Would Be Index: " + wantedWouldBeIndex + " | Add Index: " + addCountIndex + " / " + boxProductCount.size());
				}
			}
		}

		return resultingBoxes;
	}

	/*
	 * -------------------------------- PUBLIC GETTER METHODS --------------------------------
	 */

	/**
	 * Gets a map of columns and column names for displaying {@link User}
	 * objects in table views.
	 *
	 * @param withDeleteColumn get the delete button column?
	 *
	 * @return a map where the key is the column value and value is the column
	 *         name
	 */
	public static Map<String, String> getPublicUserDataColumns(final boolean withDeleteColumn)
	{
		final LinkedHashMap<String, String> cols = new LinkedHashMap<String, String>();
		cols.put("firstName", "First Name");
		cols.put("lastName", "Last Name");
		cols.put("roleName", "Role");

		if (withDeleteColumn)
			cols.put("deleteButton", "Delete");

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
	 *         name
	 */
	public static Map<String, String> getProductDataColumns(final boolean withAddColumn, final boolean withDeleteColumn)
	{
		final LinkedHashMap<String, String> cols = new LinkedHashMap<String, String>();

		if (withAddColumn)
			cols.put("addButton", "Add");

		if (withDeleteColumn)
			cols.put("deleteButton", "Delete");

		cols.put("productID", "ID");
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
	 *         name
	 */
	public static Map<String, String> getRemovalListDataColumns()
	{
		final LinkedHashMap<String, String> cols = new LinkedHashMap<String, String>();
		cols.put("databaseID", "ID");
		cols.put("state", "State");
		cols.put("size", "Size");
		cols.put("viewButton", "View");

		// Only managers and administrators can delete lists.
		if (LoginController.getCurrentUser().getRole().compareTo(UserRole.MANAGER) >= 0)
			cols.put("deleteButton", "Delete");

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
	 *         name
	 */
	public static Map<String, String> getProductSearchDataColumns(final boolean withAddColumn, final boolean withRemoveColumn)
	{
		final LinkedHashMap<String, String> cols = new LinkedHashMap<String, String>();

		if (withAddColumn)
			cols.put("addButton", "Add");

		if (withRemoveColumn)
			cols.put("removeButton", "Remove");

		cols.put("productID", "ID");
		cols.put("productName", "Name");
		cols.put("productBrand", "Brand");
		cols.put("productCategory", "Category");
		cols.put("expirationDate", "Expires");
		cols.put("boxID", "Box ID");
		cols.put("boxShelfSlot", "Shelf Slot");
		cols.put("boxProductCount", "Amount");

		return cols;
	}

	/**
	 * Gets a map of columns and column names for displaying {@link Manifest}
	 * objects in table views.
	 *
	 * @return a map where the key is the column value and value is the column
	 *         name
	 */
	public static Map<String, String> getManifestDataColumns()
	{
		final LinkedHashMap<String, String> cols = new LinkedHashMap<String, String>();
		cols.put("databaseID", "ID");
		cols.put("state", "State");
		cols.put("size", "Boxes");
		cols.put("driverID", "Driver");
		cols.put("orderedDate", "Ordered");
		cols.put("receivedDate", "Received");
		cols.put("viewButton", "View");

		return cols;
	}

	/**
	 * Gets the {@link ProductType} object from the database with the given ID.
	 *
	 * @param id the product type database ID
	 * @return the corresponding product type object or <code>null</code> for invalid ID
	 * @throws HibernateException when the query failed to commit and has been rolled back
	 */
	public static ProductType getProductTypeByID(final int id) throws HibernateException
	{
		return (ProductType) getByID(ProductType.class, id);
	}

	/**
	 * Gets the {@link ProductCategory} object from the database with the given ID.
	 *
	 * @param id the product category database ID
	 * @return the corresponding product category object or <code>null</code> for invalid ID
	 * @throws HibernateException when the query failed to commit and has been rolled back
	 */
	public static ProductCategory getProductCategoryByID(final int id) throws HibernateException
	{
		return (ProductCategory) getByID(ProductCategory.class, id);
	}

	/**
	 * Gets the {@link ProductBrand} object from the database with the given ID.
	 *
	 * @param id the product brand database ID
	 * @return the corresponding product brand object or <code>null</code> for invalid ID
	 * @throws HibernateException when the query failed to commit and has been rolled back
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
	 *         <code>null</code> for invalid credentials
	 * @see User#isValidBadgeID(String)
	 */
	public static User authenticateBadgeID(final String badgeID)
	{
		sessionFactory.getCurrentSession().beginTransaction();

		// @formatter:off
		@SuppressWarnings("unchecked")
		List<User> result = sessionFactory.getCurrentSession().createQuery("from User where badgeID = :id")
			   	   	   .setParameter("id", badgeID)
			   	   	   .list();
		// @formatter:on

		try
		{
			sessionFactory.getCurrentSession().getTransaction().commit();
		}
		catch (final HibernateException e)
		{
			sessionFactory.getCurrentSession().getTransaction().rollback();
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
	 *         <code>null</code> for invalid credentials
	 * @see User#isValidPIN(String)
	 */
	public static User authenticatePIN(final String firstName, final String lastName, final String pin)
	{
		sessionFactory.getCurrentSession().beginTransaction();

		// @formatter:off
		@SuppressWarnings("unchecked")
		List<User> result = sessionFactory.getCurrentSession().createQuery("from User "
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
			sessionFactory.getCurrentSession().getTransaction().commit();
		}
		catch (final HibernateException e)
		{
			sessionFactory.getCurrentSession().getTransaction().rollback();
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
	 * @return the corresponding product object or <code>null</code> for invalid ID
	 * @throws HibernateException when the query failed to commit and has been rolled back
	 */
	public static Product getProductByID(final int id) throws HibernateException
	{
		return (Product) getByID(Product.class, id);
	}

	/**
	 * Gets the {@link ProductBox} object from the database with the given ID.
	 *
	 * @param id the product box database ID
	 * @return the corresponding product box object or <code>null</code> for invalid ID
	 * @throws HibernateException when the query failed to commit and has been rolled back
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
	 *         logged in user (i.e. debug user) if the ID was negative
	 * @throws HibernateException when the query failed to commit and has been rolled back
	 */
	public static User getUserByID(final int id) throws HibernateException
	{
		// Debug account ID?
		if (id < 0)
			return LoginController.getCurrentUser();

		sessionFactory.getCurrentSession().beginTransaction();

		final User user = sessionFactory.getCurrentSession().get(User.class, id);

		sessionFactory.getCurrentSession().getTransaction().commit();

		return user;
	}

	/**
	 * Gets a {@link Product} from the database by its name.
	 *
	 * @param name unique name of the product
	 * @return the wanted product or <code>null</code> if the product is not present in the database
	 */
	public static Product getProductByName(final String name)
	{
		sessionFactory.getCurrentSession().beginTransaction();

		// @formatter:off
		@SuppressWarnings("unchecked")
		List<Product> result = sessionFactory.getCurrentSession().createQuery("from Product where name = :name")
			   	   	   .setParameter("name", name)
			   	   	   .list();
		// @formatter:on

		try
		{
			sessionFactory.getCurrentSession().getTransaction().commit();
		}
		catch (final HibernateException e)
		{
			sessionFactory.getCurrentSession().getTransaction().rollback();
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
	 * @return the corresponding shelf object or <code>null</code> for invalid ID
	 * @throws HibernateException when the query failed to commit and has been rolled back
	 */
	public static Shelf getShelfByID(final int id) throws HibernateException
	{
		return (Shelf) getByID(Shelf.class, id);
	}

	/**
	 * Gets the {@link ShelfLevel} object from the database with the given ID.
	 *
	 * @param id the shelf level database ID
	 * @return the corresponding shelf object or <code>null</code> for invalid ID
	 * @throws HibernateException when the query failed to commit and has been rolled back
	 */
	public static ShelfLevel getShelfLevelByID(final int id) throws HibernateException
	{
		return (ShelfLevel) getByID(ShelfLevel.class, id);
	}

	/**
	 * Gets the {@link ShelfSlot} object from the database with the given ID.
	 *
	 * @param id the shelf slot database ID
	 * @return the corresponding shelf object or <code>null</code> for invalid ID
	 * @throws HibernateException when the query failed to commit and has been rolled back
	 */
	public static ShelfSlot getShelfSlotByID(final int id) throws HibernateException
	{
		return (ShelfSlot) getByID(ShelfSlot.class, id);
	}

	/**
	 * Gets the {@link RemovalList} object from the database with the given ID.
	 *
	 * @param id the removal list database ID
	 * @return the corresponding removal list object or <code>null</code> for invalid ID
	 * @throws HibernateException when the query failed to commit and has been rolled back
	 */
	public static RemovalList getRemovalListByID(final int id) throws HibernateException
	{
		return (RemovalList) getByID(RemovalList.class, id);
	}

	/**
	 * Gets the {@link Manifest} object from the database with the given ID.
	 *
	 * @param id the manifest database ID
	 * @return the corresponding manifest object or <code>null</code> for invalid ID
	 * @throws HibernateException when the query failed to commit and has been rolled back
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
	 *            product box
	 *            database ID and the value is the number of products
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

			sessionFactory.getCurrentSession().beginTransaction();

			// First look for an exact amount.
			// @formatter:off
			boxes = sessionFactory.getCurrentSession().createQuery("from ProductBox as pb"
									  + " where pb.product.databaseID = :id"
									  + " and pb.productCount = :count"
									  + " order by pb.expirationDate asc")
				   	   	   .setParameter("id", productID)
				   	   	   .setParameter("count", wantedProductCount)
				   	   	   .list();
			// @formatter:on

			sessionFactory.getCurrentSession().getTransaction().commit();

			// Couldn't find a box with exactly the number of products wanted.
			if (boxes.isEmpty())
			{
				if (MainWindow.DEBUG_MODE)
					DBLOG.debug("Unable to find a product box with the wanted size of " + wantedProductCount + ". Looking from multiple boxes.");

				sessionFactory.getCurrentSession().beginTransaction();

				/*
				 * Remove the product count condition and find all product boxes with the wanted product ID.
				 */
				// @formatter:off
				boxes = sessionFactory.getCurrentSession().createQuery("from ProductBox as pb"
										  + " where pb.product.databaseID = :id"
										  + " order by pb.expirationDate asc")
						   	   .setParameter("id", productID)
						   	   .list();
				// @formatter:on

				sessionFactory.getCurrentSession().getTransaction().commit();

				// for (ProductBox b : boxes)
				// System.out.println(b.getExpirationDate());
				boxes = getBoxesContainingAtLeastProducts(boxes, wantedProductCount);
			}
			else if (boxes.size() > 1)
			{
				/*
				 * If multiple boxes are found with the exact wanted size, select one that will expire the soonest which
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
	 * Searches the database for product boxes with the given conditions.
	 *
	 * @param where conditions in SQL format
	 * @return a list of found product boxes as
	 *         {@link ProductBoxSearchResultRow} objects
	 * @throws NoDatabaseLinkException
	 */
	public static List<ProductBoxSearchResultRow> searchProductBox(final List<String> where) throws NoDatabaseLinkException
	{
		return searchProductBox(where, null);
	}

	/**
	 * Searches the database for product boxes with the given conditions and
	 * additionally from the given tables.
	 *
	 * @param where conditions in SQL format
	 * @param joins SQL join statements
	 * @return a list of found product boxes as
	 *         {@link ProductBoxSearchResultRow} objects
	 * @throws NoDatabaseLinkException
	 */
	public static List<ProductBoxSearchResultRow> searchProductBox(final List<String> where, final Map<DatabaseTable, String> joins) throws NoDatabaseLinkException
	{
		final List<ProductBoxSearchResultRow> foundProducts = FXCollections.observableArrayList();

		if (MainWindow.DEBUG_MODE)
			DBLOG.debug("Searching for a product box where: " + escape(where.toString()));

		final String[] columns = { "*" };

		final Map<DatabaseTable, String> join = new LinkedHashMap<DatabaseTable, String>();
		join.put(DatabaseTable.PRODUCTS, "containers.product = products.product_id");

		if (joins != null)
			join.putAll(joins);

		@SuppressWarnings("unchecked") final Set<ProductBox> result = (LinkedHashSet<ProductBox>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.CONTAINERS, join, columns, null, where));

		for (final ProductBox box : result)
			foundProducts.add(new ProductBoxSearchResultRow(box));

		DBLOG.trace("Updating product box search results.");
		observableProductBoxSearchResults.clear();
		observableProductBoxSearchResults.addAll(foundProducts);

		// Return the data for unit testing.
		return foundProducts;
	}

	/**
	 * Gets the {@link RemovalListState} object from the database with the given ID.
	 *
	 * @param id the removal list state database ID
	 * @return the corresponding removal list state object or <code>null</code> for invalid ID
	 * @throws HibernateException when the query failed to commit and has been rolled back
	 */
	public static RemovalListState getRemovalListStateByID(final int id) throws HibernateException
	{
		return (RemovalListState) getByID(RemovalListState.class, id);
	}

	/**
	 * Gets the {@link ManifestState} object from the database with the given ID.
	 *
	 * @param id the manifest state database ID
	 * @return the corresponding manifest state object or <code>null</code> for invalid ID
	 * @throws HibernateException when the query failed to commit and has been rolled back
	 */
	public static ManifestState getManifestStateByID(final int id) throws HibernateException
	{
		return (ManifestState) getByID(ManifestState.class, id);
	}

	/**
	 * Gets the {@link RemovalPlatform} object from the database with the given ID.
	 *
	 * @param id the removal platform database ID
	 * @return the corresponding removal platform object or <code>null</code> for invalid ID
	 * @throws HibernateException when the query failed to commit and has been rolled back
	 */
	public static RemovalPlatform getRemovalPlatformByID(final int id)
	{
		return (RemovalPlatform) getByID(RemovalPlatform.class, id);
	}

	/*
	 * -------------------------------- PUBLIC SETTER METHODS --------------------------------
	 */

	/**
	 * Loads sample data into the database.
	 * Assumes that a database exists.
	 *
	 * @return <code>true</code> if database changed as a result of this call
	 * @throws NoDatabaseLinkException
	 */
	public static boolean initializeDatabase() throws NoDatabaseException, NoDatabaseException, NoDatabaseLinkException
	{
		DBLOG.debug("Loading sample data to database...");

		if (!databaseExists())
			throw new NoDatabaseException();

		boolean changed = false;
		try
		{
			changed = (0 != (Integer) runQuery("RUNSCRIPT FROM './data/init.sql';"));
		}
		catch (UniqueKeyViolationException e)
		{
			e.printStackTrace();
		}

		if (changed)
			DBLOG.info("Sample data loaded.");

		return changed;
	}

	/**
	 * Loads sample data into the database if it does not yet exist there.
	 * Assumes that a database exists.
	 *
	 * @return <code>true</code> if database changed as a result of this call
	 * @throws NoDatabaseLinkException
	 */
	public static boolean loadSampleData() throws HibernateException, ParseException
	{
		// TODO: Find a way to return a proper boolean.
		return SampleData.createAll();
	}

	/**
	 * Deletes an object from the database.
	 *
	 * @param object object to delete
	 * @throws HibernateException when the object was not deleted
	 */
	private static void delete(final Object object) throws HibernateException
	{
		sessionFactory.getCurrentSession().beginTransaction();

		sessionFactory.getCurrentSession().delete(object);

		try
		{
			sessionFactory.getCurrentSession().getTransaction().commit();
		}
		catch (final HibernateException e)
		{
			sessionFactory.getCurrentSession().getTransaction().rollback();

			throw e;
		}

		// TODO: Figure out some way to return a boolean.
	}

	/**
	 * Deletes a user from the database.
	 *
	 * @param user user to delete
	 * @throws HibernateException when the user was not deleted
	 */
	public static void deleteUser(final User user) throws HibernateException
	{
		delete(user);

		// Update the observable list.
		getAllUsers();
	}

	/**
	 * Deletes a removal list from the database.
	 *
	 * @param list removal list to delete
	 * @throws HibernateException when the removal list was not deleted
	 */
	public static void deleteRemovalList(final RemovalList list) throws HibernateException
	{
		/*
		 * HIBERNATE NOTICE
		 * The child elements must be manually removed from the removal list collection before deleting the collection.
		 * If this is not done, Hibernate will attempt to delete all the child elements as well.
		 */

		if (list != null)
			list.clear();

		delete(list);

		if (list != null)
		{// Update the observable list.
			getAllRemovalLists();
		}
	}

	/*
	 * -------------------------------- SAVING DATA --------------------------------
	 */

	/**
	 * Creates a new or updates an existing object depending on whether the given object exists in the database.
	 *
	 * @param object new or existing {@link DatabaseObject} in the database
	 * @return the database ID of the inserted or updated object
	 * @throws HibernateException when data was not saved
	 */
	public static int saveOrUpdate(final DatabaseObject object)
	{
		try
		{
			sessionFactory.getCurrentSession().beginTransaction();

			if (object.getDatabaseID() < 1)
			{
				DBLOG.debug("Saving: " + object);
				sessionFactory.getCurrentSession().save(object);
			}
			else
			{
				DBLOG.debug("Updating: " + object);
				sessionFactory.getCurrentSession().update(object);
			}

			sessionFactory.getCurrentSession().flush();
			sessionFactory.getCurrentSession().clear();

			sessionFactory.getCurrentSession().getTransaction().commit();
		}
		catch (final Exception e)
		{
			sessionFactory.getCurrentSession().getTransaction().rollback();

			System.err.println(e.getMessage());
			throw e;
		}

		// TODO: Update observable lists.

		return object.getDatabaseID();
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
		sessionFactory.getCurrentSession().beginTransaction();

		sessionFactory.getCurrentSession().save(object);
		sessionFactory.getCurrentSession().flush();

		try
		{
			sessionFactory.getCurrentSession().getTransaction().commit();
		}
		catch (final HibernateException e)
		{
			sessionFactory.getCurrentSession().getTransaction().rollback();

			throw new HibernateException("Failed to commit.");
		}

		DBLOG.debug("Saved: " + object);

		// TODO: Update observable lists.

		return object.getDatabaseID();
	}

	/*
	 * -------------------------------- GETTERS --------------------------------
	 */

	/**
	 * Loads all objects of the specified type from the database into memory.
	 *
	 * @param className the name of the Java class of the objects to get
	 * @return a list of objects
	 * @throws HibernateException when the query failed to commit and has been rolled back
	 */
	private static List<Object> getAll(final String className) throws HibernateException
	{

		sessionFactory.getCurrentSession().beginTransaction();

		@SuppressWarnings("unchecked") final List<Object> result = sessionFactory.getCurrentSession().createQuery("from " + className).list();

		try
		{
			sessionFactory.getCurrentSession().getTransaction().commit();
		}
		catch (final HibernateException e)
		{
			sessionFactory.getCurrentSession().getTransaction().rollback();
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
	 * @throws HibernateException when the query failed to commit and has been rolled back
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
		final Session session = sessionFactory.openSession();

		// Transaction is created and closed automatically with the session.
		session.beginTransaction();

		@SuppressWarnings("unchecked") final List<String> result = session.createQuery("select badgeID from User where badgeID is not null").list();

		session.close();

		return result;
	}

	/**
	 * Loads all {@link RemovalPlatform} objects from the database into memory.
	 *
	 * @return a list of removal platforms in the database
	 * @throws HibernateException when the query failed to commit and has been rolled back
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
	 * @throws HibernateException when the query failed to commit and has been rolled back
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
	 * @throws HibernateException when the query failed to commit and has been rolled back
	 */
	public static List<Object> getAllShelfLevels()
	{
		return getAll("ShelfLevel");
	}

	/**
	 * Loads all {@link ShelfSlot} objects from the database into memory.
	 *
	 * @return a list of shelf slots in the database
	 * @throws HibernateException when the query failed to commit and has been rolled back
	 */
	public static List<Object> getAllShelfSlots()
	{
		return getAll("ShelfSlot");
	}

	/**
	 * Loads all {@link RemovalList} objects from the database into memory.
	 *
	 * @return a list of removal lists in the database
	 * @throws HibernateException when the query failed to commit and has been rolled back
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
	 * @throws HibernateException when the query failed to commit and has been rolled back
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
	 * @throws HibernateException when the query failed to commit and has been rolled back
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
	 * @throws HibernateException when the query failed to commit and has been rolled back
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
	 * @throws HibernateException when the query failed to commit and has been rolled back
	 */
	public static ObservableList<Object> getAllProductBrands() throws HibernateException
	{
		// TODO: Can this return a list of ProductBrand objects? Will the JavaFX lists still work?

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
	 * Gets an observable list of manifest states for changing a manifest's state.
	 * The list consists of states the currently logged in user is allowed to use in addition to the current state of
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
		sessionFactory.close();
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

}
