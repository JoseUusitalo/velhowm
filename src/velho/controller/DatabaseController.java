package velho.controller;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import org.hibernate.Transaction;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import velho.model.HibernateSessionFactory;
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
import velho.model.User;
import velho.model.enums.DatabaseFileState;
import velho.model.enums.DatabaseQueryType;
import velho.model.enums.DatabaseTable;
import velho.model.enums.UserRole;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseLinkException;
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
	private final static String DB_URI = "jdbc:h2:./data/velho;MV_STORE=FALSE;MVCC=FALSE;";

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

	/*
	 * ---- CACHE MAPS ----
	 */

	/**
	 * A map of {@link ProductBrand} objects loaded from the database.
	 */
	@Deprecated
	private static Map<Integer, ProductBrand> cachedProductBrands = new HashMap<Integer, ProductBrand>();

	/**
	 * A map of {@link ProductType} objects loaded from the database.
	 */
	@Deprecated
	private static Map<Integer, ProductType> cachedProductTypes = new HashMap<Integer, ProductType>();

	/**
	 * A map of {@link ProductCategory} objects loaded from the database.
	 */
	@Deprecated
	private static Map<Integer, ProductCategory> cachedProductCategories = new HashMap<Integer, ProductCategory>();

	/**
	 * A map of {@link Product} objects loaded from the database.
	 */
	@Deprecated
	private static Map<Integer, Product> cachedProducts = new HashMap<Integer, Product>();

	/**
	 * A map of {@link ProductContainer} objects loaded from the database.
	 */
	@Deprecated
	private static Map<Integer, ProductBox> cachedProductBoxes = new HashMap<Integer, ProductBox>();

	/**
	 * A map of {@link Shelf} objects loaded from the database.
	 */
	@Deprecated
	private static Map<Integer, Shelf> cachedShelves = new HashMap<Integer, Shelf>();

	/**
	 * A map of {@link RemovalList} objects loaded from the database.
	 */
	@Deprecated
	private static Map<Integer, RemovalList> cachedRemovalLists = new HashMap<Integer, RemovalList>();

	/**
	 * A map of {@link RemovalListState} objects loaded from the database.
	 */
	@Deprecated
	private static Map<Integer, RemovalListState> cachedRemovalListStates = new HashMap<Integer, RemovalListState>();

	/**
	 * A map of {@link Manifest} objects loaded from the database.
	 */
	@Deprecated
	private static Map<Integer, Manifest> cachedManifests = new HashMap<Integer, Manifest>();

	/**
	 * A map of {@link RemovalPlatform} objects loaded from the database.
	 */
	@Deprecated
	private static Map<Integer, RemovalPlatform> cachedRemovalPlatforms = new HashMap<Integer, RemovalPlatform>();

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
	 * <ul>
	 * <li>if type is {@link DatabaseQueryType#UPDATE} or
	 * {@link DatabaseQueryType#DELETE}: the number of rows that were
	 * changed as a result of the query as an {@link Integer}</li>
	 * <li>if type is {@link DatabaseQueryType#SELECT}: a Set containing
	 * the selected data</li>
	 * </ul>
	 * @throws NoDatabaseLinkException
	 */
	@Deprecated
	private static Object runQuery(final DatabaseQueryType type, final DatabaseTable tableName, final Map<DatabaseTable, String> joinOnValues,
			final String[] columns, final Map<String, Object> columnValues, final List<String> where) throws NoDatabaseLinkException
	{
		final Connection connection = getConnection();
		Statement statement = null;

		// Most other queries.
		final Set<Object> dataSet = new LinkedHashSet<Object>();

		// Putting boxes on shelves.
		Map<Integer, ArrayList<Integer[]>> shelfBoxMap = null;
		Map<Integer, ArrayList<Integer>> listBoxMap = null;

		try
		{
			ResultSet result = null;

			// Initialize a statement.
			statement = connection.createStatement();

			statement.execute(sqlBuilder(type, tableName, joinOnValues, columns, columnValues, where), Statement.RETURN_GENERATED_KEYS);

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
									dataSet.add(new User(result.getInt("user_id"), result.getString("first_name"), result.getString("last_name"),
											getRoleByID(result.getInt("role"))));
								break;

							case ROLES:
								if (columns.length == 1 && Arrays.asList(columns).contains("name"))
								{
									while (result.next())
										dataSet.add(result.getString("name"));
								}
								else if (columns.length == 1 && Arrays.asList(columns).contains("role_id"))
								{
									while (result.next())
										dataSet.add(result.getInt("role_id"));
								}
								break;

							case BRANDS:
								while (result.next())
									dataSet.add(new ProductBrand(result.getInt("brand_id"), result.getString("name")));
								break;

							case CATEGORIES:
								while (result.next())
									dataSet.add(new ProductCategory(result.getInt("category_id"), result.getString("name"),
											getProductTypeByID(result.getInt("type"))));
								break;

							case PRODUCTS:
								// @formatter:off
								while (result.next())
									dataSet.add(new Product(result.getInt("product_id"), result.getString("name"), getProductBrandByID(result.getInt("brand"), true), getProductCategoryByID(result.getInt("category"), true), result.getInt("popularity")));
								break;
							// @formatter:on

							case CONTAINERS:
								// @formatter:off
								while (result.next())
									dataSet.add(new ProductBox(result.getInt("container_id"), result.getDate("expiration_date"), result.getInt("max_size"), getProductByID(result.getInt("product")), result.getInt("product_count")));
								// FIXME: Containers are not set to their respective shelves!
								break;
							// @formatter:on

							case SHELVES:
								// @formatter:off
								while (result.next())
									dataSet.add(new Shelf(result.getInt("shelf_id"), result.getInt("max_levels"), result.getInt("max_shelfslots_per_level"), result.getInt("max_productboxes_per_shelfslot")));
								break;
							// @formatter:on

							case SHELF_PRODUCTBOXES:
								shelfBoxMap = new HashMap<Integer, ArrayList<Integer[]>>();
								Integer[] coords = null;
								Integer shelfID = null;
								ArrayList<Integer[]> list;

								while (result.next())
								{
									coords = new Integer[3];
									// Get all the data.
									shelfID = result.getInt("shelf");
									coords[0] = result.getInt("productbox");
									coords[1] = result.getInt("shelflevel_index");
									coords[2] = result.getInt("shelfslot_index");

									// Does this shelf already have boxes in it?
									if (shelfBoxMap.containsKey(shelfID))
									{
										// Add to the list.
										shelfBoxMap.get(shelfID).add(coords);
									}
									else
									{
										// Create a new list and put in the
										// data.
										list = new ArrayList<Integer[]>();
										list.add(coords);
										shelfBoxMap.put(shelfID, list);
									}
								}

								break;

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

							case MANIFESTS:
								// @formatter:off
								while (result.next())
									dataSet.add(new Manifest(result.getInt("manifest_id"), getManifestStateByID(result.getInt("state")), result.getInt("driver_id"), result.getDate("date_ordered"), result.getDate("date_received")));
								break;
							// @formatter:on

							case REMOVALLIST_PRODUCTBOXES:
								listBoxMap = new HashMap<Integer, ArrayList<Integer>>();
								Integer listID = null;
								ArrayList<Integer> boxIDs = new ArrayList<Integer>();

								while (result.next())
								{
									listID = result.getInt("removallist");

									// Does this removal list already have boxes
									// in it?
									if (listBoxMap.containsKey(listID))
									{
										// Add to the list.
										listBoxMap.get(listID).add(result.getInt("productbox"));
									}
									else
									{
										// Create a new list and put in the
										// data.
										boxIDs = new ArrayList<Integer>();
										boxIDs.add(result.getInt("productbox"));
										listBoxMap.put(listID, boxIDs);
									}
								}
								break;

							case MANIFEST_PRODUCTBOXES:
								listBoxMap = new HashMap<Integer, ArrayList<Integer>>();
								Integer manifestID = null;
								ArrayList<Integer> pboxIDs = new ArrayList<Integer>();

								while (result.next())
								{
									manifestID = result.getInt("manifest");

									// Does this removal list already have boxes
									// in it?
									if (listBoxMap.containsKey(manifestID))
									{
										// Add to the list.
										listBoxMap.get(manifestID).add(result.getInt("productbox"));
									}
									else
									{
										// Create a new list and put in the
										// data.
										pboxIDs = new ArrayList<Integer>();
										pboxIDs.add(result.getInt("productbox"));
										listBoxMap.put(manifestID, pboxIDs);
									}
								}
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
					case ROLES:
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
					case SHELF_PRODUCTBOXES:
						return shelfBoxMap;
					case REMOVALLIST_PRODUCTBOXES:
						if (columns.length == 1 && columns[0] != "*")
							return dataSet;
						return listBoxMap;
					case MANIFEST_PRODUCTBOXES:
						if (columns.length == 1 && columns[0] != "*")
							return dataSet;
						return listBoxMap;
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
	 * @param sql
	 * SQL to run
	 * @return an Object containing the appropriate data
	 * @throws NoDatabaseLinkException
	 */
	@Deprecated
	private static Object runQuery(final String sql) throws NoDatabaseLinkException
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

		return changed;
	}

	/**
	 * Attempts to re-link the database.
	 */
	@Deprecated
	private static void relink()
	{
		DBLOG.info("Attempting to relink database.");
		try
		{
			// Just in case.
			unlink();
		}
		catch (final NoDatabaseLinkException e)
		{
			// Do nothing. This is expected.
		}

		try
		{
			link();
		}
		catch (final ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (final ExistingDatabaseLinkException e)
		{
			e.printStackTrace();
		}
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
	@Deprecated
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
			else
			{
				relink();
			}
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
	public static String sqlBuilder(final DatabaseQueryType type, final DatabaseTable tableName, final Map<DatabaseTable, String> joinOnCondition,
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
		sb.append(tableName.toString().toLowerCase());

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
	@Deprecated
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
	@Deprecated
	public static void checkLink() throws NoDatabaseLinkException
	{
		if (connectionPool == null)
			throw new NoDatabaseLinkException();
	}

	/**
	 * Attempts to re-link the database.
	 */
	@Deprecated
	public static void tryReLink()
	{
		try
		{
			// Just in case.
			unlink();
		}
		catch (final NoDatabaseLinkException e)
		{
			// Do nothing. This is expected.
		}

		try
		{
			link();
		}
		catch (final ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (final ExistingDatabaseLinkException e)
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
	 * @throws ClassNotFoundException
	 * when the H2 driver was unable to load
	 * @throws ExistingDatabaseLinkException
	 * when a database link already exists
	 */
	@Deprecated
	public static DatabaseFileState link() throws ClassNotFoundException, ExistingDatabaseLinkException
	{
		if (connectionPool != null)
			throw new ExistingDatabaseLinkException();

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
		}
		else
		{
			state = DatabaseFileState.DOES_NOT_EXIST;
			DBLOG.info("Database does not exist, creating a new database.");
		}

		// Create a connection pool.
		connectionPool = JdbcConnectionPool.create(uri, USERNAME, "@_Vry $ECURE pword2");

		// Try getting a connection. If the database does not exist, it is
		// created.
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
	 *
	 * @throws NoDatabaseLinkException
	 * when attempting unlink a database when no database link
	 * exists
	 */
	@Deprecated
	public static void unlink() throws NoDatabaseLinkException
	{
		if (connectionPool == null)
			throw new NoDatabaseLinkException();

		connectionPool.dispose();
		connectionPool = null;
		DBLOG.info("Database unlinked.");
	}

	/**
	 * Links and initializes the database.
	 *
	 * @return <code>true</code> if database is linked, initialized, and ready to use
	 * @throws ClassNotFoundException
	 * @throws ExistingDatabaseLinkException}
	 * @throws NoDatabaseLinkException
	 */
	public static boolean connectAndInitialize() throws ClassNotFoundException, ExistingDatabaseLinkException, NoDatabaseLinkException
	{
		// TODO: Remove connection part, only do initialization.

		final DatabaseFileState state = link();
		boolean initialized = true;

		switch (state)
		{
			case DOES_NOT_EXIST:
				return false;
			case CREATED:
				// Only initialize the database if it does not exist.
				initialized = initializeDatabase();
				break;
			case EXISTING:
			default:
				// Do nothing, database is ready to use.
		}

		return isLinked() && initialized;
	}

	/*
	 * -------------------------------- PRIVATE GETTER METHODS --------------------------------
	 */

	/**
	 * Gets an object from the database with the given database ID.
	 *
	 * @param className the name of the class of the object
	 * @param idColumnName the name of the database ID column
	 * @param databaseID the database ID of the object
	 * @return the corresponding object or <code>null</code> for invalid ID
	 * @throws HibernateException when the query failed to commit and has been rolled back
	 */
	private static Object getByID(final String className, final String idColumnName, final int databaseID) throws HibernateException
	{
		if (databaseID < 1)
			return null;

		final Session session = sessionFactory.openSession();
		session.beginTransaction();

		@SuppressWarnings("unchecked")
		final List<RemovalList> result = session.createQuery("from " + className + " where " + idColumnName + " = :id").setParameter("id", databaseID).list();

		try
		{
			session.getTransaction().commit();
			session.close();
		}
		catch (final HibernateException e)
		{
			session.getTransaction().rollback();
			session.close();
			throw new HibernateException("Failed to commit.");
		}

		// Only one result should be returned by the query.
		return result.get(0);
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
	 * -------------------------------- PUBLIC GETTER METHODS --------------------------------
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
	 * name
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
		// cols.put("popularity", "Popularity");
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
	 * name
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
	 * name
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
	 * Gets the {@link ProductType} object from the given type ID.
	 *
	 * @param typeid product type database ID
	 * @return the corresponding product type object or <code>null</code> for invalid ID
	 * @throws NoDatabaseLinkException
	 */
	public static ProductType getProductTypeByID(final int typeid) throws NoDatabaseLinkException
	{
		if (!cachedProductTypes.containsKey(typeid))
		{
			final String[] columns = { "name" };
			final List<String> where = new ArrayList<String>();
			where.add("type_id = " + new Integer(typeid));

			@SuppressWarnings("unchecked")
			final Set<String> result = (LinkedHashSet<String>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.TYPES, null, columns, null, where));

			if (result.size() == 0)
				return null;

			final ProductType p = new ProductType(typeid, result.iterator().next());

			// Store for reuse.
			DBLOG.trace("Caching: " + p);
			cachedProductTypes.put(p.getDatabaseID(), p);

			return p;
		}

		DBLOG.trace("Loading ProductType " + typeid + " from cache.");
		return cachedProductTypes.get(typeid);
	}

	/**
	 * Gets the {@link ProductCategory} object from the given category ID.
	 *
	 * @param categoryid product category database ID
	 * @param getCached Get all product categories from the cache instead of rebuilding the cache from the database but
	 * only if the cached category IDs match the ones in the database.
	 * @return the corresponding product category object or <code>null</code> for invalid ID
	 * @throws NoDatabaseLinkException
	 */
	public static ProductCategory getProductCategoryByID(final int categoryid, final boolean getCached) throws NoDatabaseLinkException
	{
		if (!cachedProductCategories.containsKey(categoryid) || !getCached)
		{
			final String[] columns = { "category_id", "name", "type" };
			final List<String> where = new ArrayList<String>();
			where.add("category_id = " + new Integer(categoryid));

			@SuppressWarnings("unchecked")
			final Set<Object> result = (LinkedHashSet<Object>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.CATEGORIES, null, columns, null, where));

			if (result.size() == 0)
				return null;

			final ProductCategory p = (ProductCategory) result.iterator().next();

			// Store for reuse.
			if (getCached)
				DBLOG.trace("Caching: " + p);
			else
				DBLOG.trace("Updating cache: " + p);

			cachedProductCategories.put(p.getDatabaseID(), p);
			return p;
		}

		DBLOG.trace("Loading ProductCategory " + categoryid + " from cache.");
		return cachedProductCategories.get(categoryid);
	}

	/**
	 * Gets the {@link ProductBrand} object from the given brand ID.
	 *
	 * @param brandid product brand database ID
	 * @param getCached Get all product brands from the cache instead of rebuilding the cache from the database but only
	 * if the cached brand IDs match the ones in the database.
	 * @return the corresponding product brand object or <code>null</code> for invalid ID
	 * @throws NoDatabaseLinkException
	 */
	public static ProductBrand getProductBrandByID(final int brandid, final boolean getCached) throws NoDatabaseLinkException
	{
		if (!cachedProductBrands.containsKey(brandid) || !getCached)
		{
			final String[] columns = { "name" };
			final List<String> where = new ArrayList<String>();
			where.add("brand_id = " + new Integer(brandid));

			@SuppressWarnings("unchecked")
			final Set<String> result = (LinkedHashSet<String>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.BRANDS, null, columns, null, where));

			if (result.size() == 0)
				return null;

			final ProductBrand p = new ProductBrand(brandid, result.iterator().next());

			// Store for reuse.
			if (getCached)
				DBLOG.trace("Caching: " + p);
			else
				DBLOG.trace("Updating cache: " + p);

			cachedProductBrands.put(p.getDatabaseID(), p);
			return p;
		}

		DBLOG.trace("Loading ProductBrand " + brandid + " from cache.");
		return cachedProductBrands.get(brandid);
	}

	/**
	 * Gets the database ID of the given user role name.
	 *
	 * @param roleName the name of the role
	 * @return the database ID of the given role (a value greater than 0) or
	 * <code>-1</code> if the role does not exist in the database'
	 * @throws NoDatabaseLinkException
	 */
	public static int getRoleID(final String roleName) throws NoDatabaseLinkException
	{
		final String[] columns = { "role_id" };
		final List<String> where = new ArrayList<String>();
		where.add("name = '" + roleName + "'");

		@SuppressWarnings("unchecked")
		final Set<Integer> result = (LinkedHashSet<Integer>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.ROLES, null, columns, null, where));

		if (result.size() == 0)
			return -1;

		// Only one result as IDs are unique.
		return result.iterator().next().intValue();
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
	 * <p>
	 * Authenticates a user with the given badge ID string.
	 * </p>
	 * <p>
	 * Warning: Assumes that the badge ID is technically valid.
	 * </p>
	 *
	 * @param badgeID a badge ID string
	 * @return a {@link User} object representing the authenticated user or
	 * <code>null</code> for invalid credentials
	 * @throws NoDatabaseLinkException
	 * @see User#isValidBadgeID(String)
	 */
	public static User authenticateBadgeID(final String badgeID) throws NoDatabaseLinkException
	{
		try
		{
			Integer.parseInt(badgeID);
		}
		catch (final NumberFormatException e)
		{
			// Although badge IDs are stored as string, they are still numbers.
			return null;
		}

		final String[] columns = { "user_id", "first_name", "last_name", "role" };
		final List<String> where = new ArrayList<String>();
		where.add("badge_id = " + badgeID);

		@SuppressWarnings("unchecked")
		final Set<User> result = (LinkedHashSet<User>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.USERS, null, columns, null, where));

		if (result.size() == 0)
			return null;

		return result.iterator().next();
	}

	/**
	 * <p>
	 * Authenticates a user with the given PIN string.
	 * </p>
	 * <p>
	 * Warning: Assumes that the PIN is technically valid.
	 * </p>
	 *
	 * @param pin is a PIN string
	 * @return a {@link User} object representing the authenticated user or
	 * <code>null</code> for invalid credentials
	 * @throws NoDatabaseLinkException
	 * @see User#isValidPIN(String)
	 */
	public static User authenticatePIN(final String firstName, final String lastName, final String pin) throws NoDatabaseLinkException
	{
		final String[] columns = { "user_id", "first_name", "last_name", "role" };
		final List<String> where = new ArrayList<String>();
		where.add("first_name = '" + firstName + "'");
		where.add("last_name = '" + lastName + "'");
		where.add("pin = " + pin);

		@SuppressWarnings("unchecked")
		final Set<User> result = (LinkedHashSet<User>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.USERS, null, columns, null, where));

		if (result.size() == 0)
			return null;

		return result.iterator().next();
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
		return (Product) getByID("Product", "product_id", id);
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
		return (ProductBox) getByID("ProductBox", "container_id", id);
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

		final Session session = sessionFactory.openSession();

		final User user = session.get(User.class, id);

		session.close();

		return user;
	}

	/**
	 * Gets the product ID by its name.
	 *
	 * @param name unique name of the product
	 * @return the database ID of the product or <code>-1</code> if the product
	 * is not present in the database
	 */
	public static int getProductIDFromName(final String name) throws NoDatabaseLinkException
	{
		final String[] columns = { "product_id" };
		final List<String> where = new ArrayList<String>();
		where.add("name = '" + name + "'");

		@SuppressWarnings("unchecked")
		final Set<Integer> result = (LinkedHashSet<Integer>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.PRODUCTS, null, columns, null, where));

		if (result.size() == 0)
			return -1;

		// Only one result as names are unique.
		return result.iterator().next();
	}

	/**
	 * Gets the {@link Shelf} object from the given shelf ID.
	 *
	 * @param shelfid shelf database ID
	 * @return the corresponding shelf object or <code>null</code> for invalid ID
	 * @throws NoDatabaseLinkException
	 */
	public static Shelf getShelfByID(final int shelfid, final boolean getCached) throws NoDatabaseLinkException
	{
		if (!cachedShelves.containsKey(shelfid) || !getCached)
		{
			final String[] columns = { "*" };
			final List<String> where = new ArrayList<String>();
			where.add("shelf_id = " + new Integer(shelfid));

			@SuppressWarnings("unchecked")
			final Set<Object> result = (LinkedHashSet<Object>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.SHELVES, null, columns, null, where));

			if (result.size() == 0)
				return null;

			final Shelf s = (Shelf) result.iterator().next();

			// Store for reuse.
			if (getCached)
				DBLOG.trace("Caching: " + s);
			else
				DBLOG.trace("Updating cache: " + s);

			cachedShelves.put(s.getDatabaseID(), s);
			setContainersToShelf(shelfid);

			return cachedShelves.get(shelfid);
		}

		DBLOG.trace("Loading Shelf " + shelfid + " from cache.");
		return cachedShelves.get(shelfid);
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
		return (RemovalList) getByID("RemovalList", "removallist_id", id);
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
		return (Manifest) getByID("Manifest", "manifest_id", id);
	}

	/**
	 * Gets a random valid shelf slot ID string.
	 *
	 * @return random valid shelf slot ID string
	 */
	public static String getRandomShelfSlot() throws NoDatabaseLinkException
	{
		final String[] columns = { "shelf_id" };

		@SuppressWarnings("unchecked")
		final Set<Integer> result = (LinkedHashSet<Integer>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.SHELVES, null, columns, null, null));

		final List<Integer> list = new ArrayList<Integer>(result);

		Collections.shuffle(list);

		final Shelf randomShelf = getShelfByID(list.get(0), true);
		final int randomLevel = (int) (Math.round(Math.random() * (randomShelf.getLevelCount() - 1) + 1));
		final int randomSlotIndex = (int) (Math.round(Math.random() * (randomShelf.getShelfSlotCount() / randomShelf.getLevelCount() - 1)));

		return Shelf.coordinatesToShelfSlotID(list.get(0), randomLevel, randomSlotIndex, true);
	}

	/**
	 * Gets a list product codes in the database.
	 *
	 * @return a list of integer product codes
	 */
	public static List<Integer> getProductCodeList() throws NoDatabaseLinkException
	{
		final String[] columns = { "product_id" };

		@SuppressWarnings("unchecked")
		final Set<Integer> result = (LinkedHashSet<Integer>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.PRODUCTS, null, columns, null, null));
		final List<Integer> ints = new ArrayList<Integer>();
		ints.addAll(result);
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
	 * Gets an {@link ObservableList} of all removal lists.
	 *
	 * @return a cached list of removal lists
	 */
	public static ObservableList<Object> getObservableRemovalLists() throws NoDatabaseLinkException
	{
		final String[] columns = { "*" };

		@SuppressWarnings("unchecked")
		final Set<RemovalList> result = (LinkedHashSet<RemovalList>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.REMOVALLISTS, null, columns, null,
				null));

		final Iterator<RemovalList> it = result.iterator();

		DBLOG.debug("Caching all removal lists.");

		RemovalList list = null;

		cachedRemovalLists.clear();

		while (it.hasNext())
		{
			list = it.next();
			cachedRemovalLists.put(list.getDatabaseID(), list);
		}
		setAllContainersToAllRemovalLists();

		observableRemovalLists.clear();
		observableRemovalLists.addAll(cachedRemovalLists.values());

		DBLOG.debug("Observable & cached list of RemovalLists updated:");
		DBLOG.debug(cachedRemovalLists);
		DBLOG.debug(observableRemovalLists);

		return observableRemovalLists;
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

		final Session session = sessionFactory.openSession();

		// For every unique string representing a product.
		for (final Integer productID : productData.keySet())
		{
			boxes = new ArrayList<ProductBox>();
			wantedProductCount = productData.get(productID);

			DBLOG.debug("Looking for [" + productID + "] of size " + wantedProductCount);

			// First look for an exact amount.
			// @formatter:off
			boxes = session.createQuery("from ProductBox as pb"
									  + " where pb.product.databaseID = :id"
									  + " and pb.productCount = :count"
									  + " order by pb.expirationDate asc")
				   	   	   .setParameter("id", productID)
				   	   	   .setParameter("count", wantedProductCount)
				   	   	   .list();
			// @formatter:on

			// Couldn't find a box with exactly the number of products wanted.
			if (boxes.isEmpty())
			{
				if (MainWindow.DEBUG_MODE)
					DBLOG.debug("Unable to find a product box with the wanted size of " + wantedProductCount + ". Looking from multiple boxes.");

				/*
				 * Remove the product count condition and find all product boxes with the wanted product ID.
				 * This could be done with the getByID() private method but since we already have a session open and
				 * more importantly this query is done in a loop, it is faster to do it here.
				 */
				// @formatter:off
				boxes = session.createQuery("from ProductBox as pb"
										  + " where pb.product.databaseID = :id"
										  + " order by pb.expirationDate asc")
						   	   .setParameter("id", productID)
						   	   .list();
				// @formatter:on

				System.out.println("boxes---------------------------");
				for (ProductBox b : boxes)
					System.out.println(b.getExpirationDate());
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

		session.close();

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
	 * {@link ProductBoxSearchResultRow} objects
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
	 * {@link ProductBoxSearchResultRow} objects
	 * @throws NoDatabaseLinkException
	 */
	public static List<ProductBoxSearchResultRow> searchProductBox(final List<String> where, final Map<DatabaseTable, String> joins)
			throws NoDatabaseLinkException
	{
		final List<ProductBoxSearchResultRow> foundProducts = FXCollections.observableArrayList();

		if (MainWindow.DEBUG_MODE)
			DBLOG.debug("Searching for a product box where: " + escape(where.toString()));

		final String[] columns = { "*" };

		final Map<DatabaseTable, String> join = new LinkedHashMap<DatabaseTable, String>();
		join.put(DatabaseTable.PRODUCTS, "containers.product = products.product_id");

		if (joins != null)
			join.putAll(joins);

		@SuppressWarnings("unchecked")
		final Set<ProductBox> result = (LinkedHashSet<ProductBox>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.CONTAINERS, join, columns, null, where));

		for (final ProductBox box : result)
		{
			if (!cachedProductBoxes.containsKey(box.getDatabaseID()))
			{
				// Store for reuse.
				DBLOG.trace("Caching: " + box);

				cachedProductBoxes.put(box.getDatabaseID(), box);
			}

			foundProducts.add(new ProductBoxSearchResultRow(box));
		}

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
		return (RemovalListState) getByID("RemovalListState", "removallist_state_id", id);
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
		return (ManifestState) getByID("ManifestState", "manifest_state_id", id);
	}

	public static RemovalPlatform getRemovalPlatformByID(final int platformid, final boolean getCached) throws NoDatabaseLinkException
	{
		if (!cachedRemovalPlatforms.containsKey(platformid) || !getCached)
		{
			final String[] columns = { "*" };
			final List<String> where = new ArrayList<String>();
			where.add("platform_id = " + new Integer(platformid));

			@SuppressWarnings("unchecked")
			final Set<RemovalPlatform> result = (LinkedHashSet<RemovalPlatform>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.REMOVALPLATFORMS, null,
					columns, null, where));

			if (result.size() == 0)
				return null;

			final RemovalPlatform r = result.iterator().next();

			// Store for reuse.
			if (getCached)
				DBLOG.trace("Caching: " + r);
			else
				DBLOG.trace("Updating cache: " + r);

			cachedRemovalPlatforms.put(r.getDatabaseID(), r);
			return r;
		}

		DBLOG.trace("Loading RemovalPlatform " + platformid + " from cache.");
		return cachedRemovalPlatforms.get(platformid);
	}

	/*
	 * -------------------------------- PRIVATE SETTER METHODS --------------------------------
	 */

	/**
	 * Places the loaded {@link ProductContainer} objects into {@link Shelf}
	 * objects.
	 *
	 * @throws NoDatabaseLinkException
	 */
	private static void setContainersToShelf(final int shelfid) throws NoDatabaseLinkException
	{
		DBLOG.debug("Placing product boxes on shelf " + shelfid + "...");

		final String[] columns = { "*" };

		final List<String> where = new ArrayList<String>();
		where.add("shelf = " + shelfid);

		final Shelf shelf = getShelfByID(shelfid, true);

		DBLOG.trace(shelf);

		@SuppressWarnings("unchecked")
		final Map<Integer, ArrayList<Integer[]>> shelfBoxMap = (HashMap<Integer, ArrayList<Integer[]>>) runQuery(DatabaseQueryType.SELECT,
				DatabaseTable.SHELF_PRODUCTBOXES, null, columns, null, where);

		// If the shelf is not empty.
		if (!shelfBoxMap.isEmpty())
		{
			ProductBox box = null;
			String shelfSlotID = null;

			final ArrayList<Integer[]> boxes = shelfBoxMap.get(shelfid);

			for (final Integer[] data : boxes)
			{
				box = getProductBoxByID(data[0]);

				DBLOG.trace("Placing box: " + box.toString());
				DBLOG.trace("Box: " + data[0] + " Slf: " + shelfid + " Lvl: " + data[1] + " Slt: " + data[2]);

				// data[1] is the level index
				shelfSlotID = Shelf.coordinatesToShelfSlotID(shelfid, data[1] + 1, data[2], true);
				DBLOG.trace("Placed: " + shelf.addToSlot(shelfSlotID, box, false));
				DBLOG.trace(shelf);
			}

			DBLOG.debug("Product boxes placed on shelf " + shelfid + ".");
		}
		else
		{
			DBLOG.debug("Nothing to place.");
		}
	}

	/**
	 * Places the correct product boxes in the cached removal list.
	 *
	 * @param listid removal list to place product boxes into
	 * @throws NoDatabaseLinkException
	 */
	@Deprecated
	private static void setContainersToRemovalList(final int listid) throws NoDatabaseLinkException
	{
		DBLOG.debug("Placing product boxes on removal list " + listid + "...");

		final List<String> where = new ArrayList<String>();
		where.add("removallist = " + listid);

		final String[] columns = { "productbox" };
		@SuppressWarnings("unchecked")
		final Set<Integer> removalListBoxes = (LinkedHashSet<Integer>) runQuery(DatabaseQueryType.SELECT, DatabaseTable.REMOVALLIST_PRODUCTBOXES, null, columns,
				null, where);

		if (!removalListBoxes.isEmpty())
		{
			for (final Integer id : removalListBoxes)
				cachedRemovalLists.get(listid).addProductBox(getProductBoxByID(id));

			DBLOG.debug("Product boxes placed on removal list " + listid + ".");
		}
		else
			DBLOG.debug("Nothing to place.");
	}

	@Deprecated
	private static void setContainersToManifest(final int manifestid) throws NoDatabaseLinkException
	{
		DBLOG.debug("Placing product boxes on manifest " + manifestid + "...");

		final List<String> where = new ArrayList<String>();
		where.add("manifest = " + manifestid);

		final String[] columns = { "productbox" };
		@SuppressWarnings("unchecked")
		final Set<Integer> manifestBoxes = (LinkedHashSet<Integer>) runQuery(DatabaseQueryType.SELECT, DatabaseTable.MANIFEST_PRODUCTBOXES, null, columns, null,
				where);

		if (!manifestBoxes.isEmpty())
		{
			Set<ProductBox> boxes = new HashSet<ProductBox>();
			for (final Integer id : manifestBoxes)
				boxes.add(getProductBoxByID(id));

			cachedManifests.get(manifestid).setBoxes(boxes);

			DBLOG.debug("Product boxes placed on manifest " + manifestid + ".");
		}
		else
			DBLOG.debug("Nothing to place.");
	}
	/*
	 * -------------------------------- PUBLIC SETTER METHODS --------------------------------
	 */

	/**
	 * Initializes the database.
	 *
	 * @return <code>true</code> if database changed as a result of this call
	 * @throws NoDatabaseLinkException
	 */
	public static boolean initializeDatabase() throws NoDatabaseLinkException
	{
		DBLOG.info("Initializing database...");

		final boolean changed = (0 != (Integer) runQuery("RUNSCRIPT FROM './data/init.sql';"));

		if (changed)
			DBLOG.info("Database initialized.");

		return changed;
	}

	/**
	 * Deletes an object from the database.
	 *
	 * @param object object to delete
	 * @throws HibernateException when the object was not deleted
	 */
	private static void delete(final Object object) throws HibernateException
	{
		final Session session = sessionFactory.openSession();
		session.beginTransaction();

		session.delete(object);

		try
		{
			session.getTransaction().commit();
			session.close();
		}
		catch (final HibernateException e)
		{
			session.getTransaction().rollback();
			session.close();
			throw new HibernateException("Failed to delete.");
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
		delete(list);

		// Update the observable list.
		getAllRemovalLists();
	}

	/**
	 * <p>
	 * Adds a new user to the database. If database changed as a result of the
	 * call, updates the {@link ObservableList} of user data shown in the UI.
	 * </p>
	 * <p>
	 * Warning: Assumes that given data is valid.
	 * </p>
	 *
	 * @param badgeID badge ID of the user
	 * @param pin PIN of the user
	 * @param firstName first name of the user
	 * @param lastName last name of the user
	 * @param roleID the ID of the role of the user
	 * @return <code>true</code> if user was added
	 * @throws NoDatabaseLinkException
	 * when database link was lost
	 */
	public static boolean insertUser(final String badgeID, final String pin, final String firstName, final String lastName, final int roleID)
			throws NoDatabaseLinkException
	{
		final Map<String, Object> values = new LinkedHashMap<String, Object>();

		// If no pin is defined, add with badge ID.
		if (pin == null || pin.isEmpty())
			values.put("badge_id", badgeID);
		else
			values.put("pin", pin);

		values.put("first_name", firstName);
		values.put("last_name", lastName);
		values.put("role", roleID);

		@SuppressWarnings("unchecked")
		final boolean changed = (0 < ((Set<Integer>) runQuery(DatabaseQueryType.INSERT, DatabaseTable.USERS, null, null, values, null)).size());

		return changed;
	}

	/**
	 * Adds the given product box to the shelf slot it specifies.
	 *
	 * @param productBox product box to update in the database
	 * @return <code>true</code> if the database was updated
	 */
	@SuppressWarnings("unchecked")
	public static boolean addProductBoxToShelfSlot(final ProductBox productBox, final String shelfSlotID) throws NoDatabaseLinkException
	{
		final Object[] tokens = Shelf.tokenizeShelfSlotID(shelfSlotID);
		final int shelfID = Integer.parseInt(((String) tokens[0]).substring(1));

		final Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("shelflevel_index", (int) tokens[1] - 1);
		values.put("shelfslot_index", tokens[2]);

		boolean changed = false;

		// If the product box is not in a shelf yet, INSERT.
		if (productBox.getShelfSlot() == null)
		{
			values.put("shelf", shelfID);
			values.put("productbox", productBox.getDatabaseID());
			changed = (0 < ((LinkedHashSet<Object>) runQuery(DatabaseQueryType.INSERT, DatabaseTable.SHELF_PRODUCTBOXES, null, null, values, null)).size());
		}
		else
		{
			// Otherwise UPDATE.

			final List<String> where = new ArrayList<String>();
			where.add("productbox = " + productBox.getDatabaseID());
			changed = (0 < ((LinkedHashSet<Object>) runQuery(DatabaseQueryType.UPDATE, DatabaseTable.SHELF_PRODUCTBOXES, null, null, values, where)).size());
		}

		// Update the cache.
		if (changed)
			getShelfByID(shelfID, false);

		return changed;
	}

	/**
	 * Removes the given product box from it's shelf slot.
	 *
	 * @param productBox product box to remove from it's shelf slot
	 */
	public static boolean removeProductBoxFromShelfSlot(final ProductBox productBox) throws NoDatabaseLinkException
	{
		final List<String> where = new ArrayList<String>();
		where.add("productbox = " + productBox.getDatabaseID());

		@SuppressWarnings("unchecked")
		final boolean changed = (0 < ((LinkedHashSet<Object>) runQuery(DatabaseQueryType.DELETE, DatabaseTable.SHELF_PRODUCTBOXES, null, null, null, where))
				.size());

		final Object[] tokens = Shelf.tokenizeShelfSlotID(productBox.getShelfSlot());

		// Update the cache.
		if (changed)
			getShelfByID(Integer.parseInt(((String) tokens[0]).substring(1)), false);

		return changed;
	}

	/*
	 * -------------------------------- SAVING DATA --------------------------------
	 */

	/**
	 * Creates a new or updates an existing object depending on whether the given object exists in the database.
	 *
	 * @param object new or existing object in the database
	 * @return the database ID of the inserted or updated object
	 * @throws HibernateException when data was not saved
	 */
	public static int save(final RemovalList object)
	{
		// TODO: Generalize when all tests have been updated to manually rollback.

		final Session session = sessionFactory.openSession();
		final Transaction transaction = session.beginTransaction();

		session.saveOrUpdate(object);

		try
		{
			transaction.commit();
			session.close();
		}
		catch (final HibernateException e)
		{
			transaction.rollback();
			session.close();
			throw new HibernateException("Failed to commit.");
		}

		DBLOG.debug("Saved/Updated: " + object);

		return object.getDatabaseID();
	}

	/**
	 * Creates a new or updates an existing object depending on whether the given object exists in the database.
	 *
	 * @param object new or existing object in the database
	 * @return the database ID of the inserted or updated object
	 * @throws HibernateException when data was not saved
	 */
	public static int save(final Manifest object)
	{
		// TODO: Generalize when all tests have been updated to manually rollback.

		final Session session = sessionFactory.openSession();
		final Transaction transaction = session.beginTransaction();

		session.saveOrUpdate(object);

		try
		{
			transaction.commit();
			session.close();
		}
		catch (final HibernateException e)
		{
			transaction.rollback();
			session.close();
			throw new HibernateException("Failed to commit.");
		}

		DBLOG.debug("Saved/Updated: " + object);

		return object.getDatabaseID();
	}

	/**
	 * Updates an existing product box in the database with the data from the given product box or creates a new one if
	 * it doesn't exist.
	 * A database ID of -1 signifies a brand new {@link ProductBox}.
	 *
	 * @param productbox object to save
	 * @return the database ID of the inserted object or -1 if saving failed
	 * @throws NoDatabaseLinkException
	 */
	private static int save(final ProductBox productbox) throws NoDatabaseLinkException
	{
		final Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("expiration_date", getH2DateFormat(productbox.getExpirationDate()));
		values.put("product", productbox.getProduct().getDatabaseID());
		values.put("max_size", productbox.getMaxSize());
		values.put("product_count", productbox.getProductCount());

		final List<String> where = new ArrayList<String>();

		DatabaseQueryType query;
		int dbID = productbox.getDatabaseID();

		// If the object does not exist yet, INSERT.
		if (dbID < 1)
			query = DatabaseQueryType.INSERT;
		else
		{
			query = DatabaseQueryType.UPDATE;
			where.add("container_id = " + dbID);
		}

		// Insert/Update
		@SuppressWarnings("unchecked")
		final Set<Integer> result = (Set<Integer>) runQuery(query, DatabaseTable.CONTAINERS, null, null, values, where);

		if (result.size() == 0)
		{
			DBLOG.error("Failed to save: " + productbox);
			return -1;
		}

		// Get the inserted database ID (if the given object was inserted
		// instead of updated).
		if (dbID < 1)
			dbID = result.iterator().next();

		// Update the cache.
		DBLOG.debug(query.toString() + ": " + getProductBoxByID(dbID));

		return dbID;
	}

	/**
	 * Updates an existing removal platform in the database with the data from
	 * the given removal platform or creates a
	 * new one if it doesn't exist.
	 * A database ID of -1 signifies a brand new {@link RemovalPlatform}.
	 *
	 * @param platform new or existing removal platform
	 * @return <code>true</code> if existing data was updated or a new removal platform
	 * was created in the database
	 */
	@SuppressWarnings("unchecked")
	public static boolean save(final RemovalPlatform platform) throws NoDatabaseLinkException
	{
		int platformID = platform.getDatabaseID();

		final Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("platform_id", platform.getDatabaseID());
		values.put("free_space", platform.getFreeSpacePercent());
		values.put("free_space_warning", platform.getFreeSpaceLeftWarningPercent());

		final List<String> where = new ArrayList<String>();

		DatabaseQueryType query;

		// If the object does not exist yet, INSERT.
		if (platformID < 1)
			query = DatabaseQueryType.INSERT;
		else
		{
			query = DatabaseQueryType.UPDATE;
			where.add("platform_id = " + platformID);
		}

		// Insert/Update the object
		Set<Integer> result = (LinkedHashSet<Integer>) runQuery(query, DatabaseTable.REMOVALPLATFORMS, null, null, values, where);

		if (result.size() == 0)
		{
			DBLOG.error("Failed to save: " + platform);
			return false;
		}

		// Update the cache.
		DBLOG.debug(query.toString() + ": " + getRemovalPlatformByID(platformID, false));

		return true;
	}

	/**
	 * Updates an existing product in the database with the data from
	 * the given product or creates a
	 * new one if it doesn't exist.
	 * A database ID of -1 signifies a brand new {@link Product}.
	 *
	 * @param product new or existing product
	 * @return <code>true</code> if existing data was updated or a new product
	 * was created in the database
	 */
	public static int save(final Product product) throws NoDatabaseLinkException
	{
		final Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("name", product.getName());
		values.put("brand", product.getBrand().getDatabaseID());
		values.put("category", product.getCategory().getDatabaseID());
		values.put("popularity", product.getPopularity());

		final List<String> where = new ArrayList<String>();

		DatabaseQueryType query;
		int dbID = product.getDatabaseID();

		// If the object does not exist yet, INSERT.
		if (dbID < 1)
			query = DatabaseQueryType.INSERT;
		else
		{
			query = DatabaseQueryType.UPDATE;
			where.add("product_id = " + dbID);
		}

		// Insert/Update
		@SuppressWarnings("unchecked")
		final Set<Integer> result = (Set<Integer>) runQuery(query, DatabaseTable.PRODUCTS, null, null, values, where);

		if (result.size() == 0)
		{
			DBLOG.error("Failed to save: " + product);
			return -1;
		}

		// Get the inserted database ID (if the given object was inserted
		// instead of updated).
		if (dbID < 1)
			dbID = result.iterator().next();

		// Update the cache.
		DBLOG.debug(query.toString() + ": " + getProductByID(dbID));

		// Update the observable list.
		observableProducts.clear();
		observableProducts.addAll(cachedProducts.values());

		return dbID;
	}

	/**
	 * Updates an existing product brand in the database with the data from
	 * the given product brand or creates a new one if it doesn't exist.
	 * A database ID of -1 signifies a brand new {@link ProductBrand}.
	 *
	 * @param brand new or existing product brand
	 * @return <code>true</code> if existing data was updated or a new product brand
	 * was created in the database
	 */
	public static int save(final ProductBrand brand) throws NoDatabaseLinkException
	{
		final Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("name", brand.getName());

		final List<String> where = new ArrayList<String>();

		DatabaseQueryType query;
		int dbID = brand.getDatabaseID();

		// If the object does not exist yet, INSERT.
		if (dbID < 1)
			query = DatabaseQueryType.INSERT;
		else
		{
			query = DatabaseQueryType.UPDATE;
			where.add("brand_id = " + dbID);
		}

		// Insert/Update
		@SuppressWarnings("unchecked")
		final Set<Integer> result = (Set<Integer>) runQuery(query, DatabaseTable.BRANDS, null, null, values, where);

		if (result.size() == 0)
		{
			DBLOG.error("Failed to save: " + brand);
			return -1;
		}

		// Get the inserted database ID (if the given object was inserted
		// instead of updated).
		if (dbID < 1)
			dbID = result.iterator().next();

		// Update the cache.
		DBLOG.debug(query.toString() + ": " + getProductBrandByID(dbID, false));

		// Update the observable list.
		observableProductBrands.clear();
		observableProductBrands.addAll(cachedProductBrands.values());

		return dbID;
	}

	/**
	 * Updates an existing product brand in the database with the data from
	 * the given product brand or creates a new one if it doesn't exist.
	 * A database ID of -1 signifies a brand new {@link ProductBrand}.
	 *
	 * @param category new or existing product brand
	 * @return <code>true</code> if existing data was updated or a new product brand
	 * was created in the database
	 */
	public static int save(final ProductCategory category) throws NoDatabaseLinkException
	{
		final Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("name", category.getName());
		values.put("type", category.getType().getDatabaseID());

		final List<String> where = new ArrayList<String>();

		DatabaseQueryType query;
		int dbID = category.getDatabaseID();

		// If the object does not exist yet, INSERT.
		if (dbID < 1)
			query = DatabaseQueryType.INSERT;
		else
		{
			query = DatabaseQueryType.UPDATE;
			where.add("category_id = " + dbID);
		}

		// Insert/Update
		@SuppressWarnings("unchecked")
		final Set<Integer> result = (Set<Integer>) runQuery(query, DatabaseTable.CATEGORIES, null, null, values, where);

		if (result.size() == 0)
		{
			DBLOG.error("Failed to save: " + category);
			return -1;
		}

		// Get the inserted database ID (if the given object was inserted
		// instead of updated).
		if (dbID < 1)
			dbID = result.iterator().next();

		// Update the cache.
		DBLOG.debug(query.toString() + ": " + getProductCategoryByID(dbID, false));

		// Update the observable list.
		observableProductCategories.clear();
		observableProductCategories.addAll(cachedProductCategories.values());

		return dbID;
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
	@SuppressWarnings("unchecked")
	private static List<Object> getAll(final String className) throws HibernateException
	{
		final Session session = sessionFactory.openSession();
		session.beginTransaction();

		final List<Object> result = session.createQuery("from " + className).list();

		try
		{
			session.getTransaction().commit();
			session.close();
		}
		catch (final HibernateException e)
		{
			session.getTransaction().rollback();
			session.close();
			throw new HibernateException("Failed to commit.");
		}

		DBLOG.debug("Loaded all " + className + " objects.");

		return result;
	}

	/**
	 * Loads data from database into memory.
	 */
	@Deprecated
	public static void loadData() throws NoDatabaseLinkException
	{
		// TODO: Get rid of this.
		DBLOG.info("Loading data from database...");

		// getAllProductBoxes();
		getAllShelves();
		// getAllRemovalLists();

		DBLOG.info("Data loaded from database.");
	}

	/**
	 * Places the loaded {@link ProductContainer} objects into {@link Shelf}
	 * objects.
	 *
	 * @throws NoDatabaseLinkException
	 */
	@Deprecated
	private static void setAllContainersToAllShelves() throws NoDatabaseLinkException
	{
		// TODO: Get rid of this.
		final String[] columns = { "*" };
		@SuppressWarnings("unchecked")
		final Map<Integer, ArrayList<Integer[]>> shelfBoxMap = (HashMap<Integer, ArrayList<Integer[]>>) runQuery(DatabaseQueryType.SELECT,
				DatabaseTable.SHELF_PRODUCTBOXES, null, columns, null, null);

		int boxcount = 0;
		Shelf shelf;
		ProductBox box;
		String slot;

		for (final Integer shelfID : shelfBoxMap.keySet())
		{
			if (cachedShelves.containsKey(shelfID))
			{
				final ArrayList<Integer[]> boxes = shelfBoxMap.get(shelfID);
				boxcount += boxes.size();

				for (final Integer[] data : boxes)
				{
					if (cachedProductBoxes.containsKey(data[0]))
					{
						shelf = cachedShelves.get(shelfID);
						box = cachedProductBoxes.get(data[0]);
						slot = Shelf.coordinatesToShelfSlotID(shelfID, data[1] + 1, data[2], true);

						DBLOG.trace("Adding: " + box);
						DBLOG.trace("To: " + shelf);
						DBLOG.trace("Into: " + slot);
						// Do not update the database as this method loads the
						// data from the database into objects.
						DBLOG.trace("Added: " + shelf.addToSlot(slot, box, false));
						DBLOG.trace("Result: " + shelf);
					}
				}
			}
		}

		DBLOG.debug(boxcount + " ProductBoxes placed on " + shelfBoxMap.size() + " Shelves.");
	}

	/**
	 * Places the loaded {@link ProductContainer} objects into
	 * {@link RemovalList} objects.
	 *
	 * @throws NoDatabaseLinkException
	 */
	@Deprecated
	private static void setAllContainersToAllRemovalLists() throws NoDatabaseLinkException
	{
		// TODO: Get rid of this.
		final String[] columns = { "*" };
		@SuppressWarnings("unchecked")
		final Map<Integer, ArrayList<Integer>> removalListBoxes = (HashMap<Integer, ArrayList<Integer>>) runQuery(DatabaseQueryType.SELECT,
				DatabaseTable.REMOVALLIST_PRODUCTBOXES, null, columns, null, null);

		int boxcount = 0;

		for (final Integer removalListID : removalListBoxes.keySet())
		{
			final ArrayList<Integer> boxIDs = removalListBoxes.get(removalListID);
			boxcount += boxIDs.size();

			for (final Integer id : boxIDs)
			{
				// Do not update the database as this method loads the data from
				// the database into objects.
				getRemovalListByID(removalListID).addProductBox(getProductBoxByID(id));
			}
		}

		DBLOG.debug(boxcount + " ProductBoxes placed on " + removalListBoxes.size() + " RemovalLists.");
	}

	/**
	 * Places the loaded {@link ProductContainer} objects into
	 * {@link RemovalList} objects.
	 *
	 * @throws NoDatabaseLinkException
	 */
	@Deprecated
	private static void setAllContainersToAllManifests() throws NoDatabaseLinkException
	{
		// TODO: Get rid of this.
		final String[] columns = { "*" };
		@SuppressWarnings("unchecked")
		final Map<Integer, ArrayList<Integer>> manifestBoxes = (HashMap<Integer, ArrayList<Integer>>) runQuery(DatabaseQueryType.SELECT,
				DatabaseTable.MANIFEST_PRODUCTBOXES, null, columns, null, null);

		int boxcount = 0;

		for (final Integer manifestID : manifestBoxes.keySet())
		{
			final ArrayList<Integer> boxIDs = manifestBoxes.get(manifestID);
			boxcount += boxIDs.size();

			/*
			 * TODO: This bit could be optimized. I'm looping through the same list twice.
			 */

			Set<ProductBox> boxes = new LinkedHashSet<ProductBox>();

			for (final Integer id : boxIDs)
				boxes.add(getProductBoxByID(id));

			getManifestByID(manifestID).setBoxes(boxes);
		}

		DBLOG.debug(boxcount + " ProductBoxes placed on " + manifestBoxes.size() + " Manifests.");
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
	 * Loads the following objects into memory:
	 * <ul>
	 * <li>{@link Shelf}</li>
	 * </ul>
	 *
	 * @throws NoDatabaseLinkException
	 */
	private static void getAllShelves() throws NoDatabaseLinkException
	{
		final String[] columns = { "*" };
		@SuppressWarnings("unchecked")
		final Set<Shelf> result = (Set<Shelf>) runQuery(DatabaseQueryType.SELECT, DatabaseTable.SHELVES, null, columns, null, null);

		final Iterator<Shelf> it = result.iterator();
		// Store for reuse.
		while (it.hasNext())
		{
			final Shelf s = it.next();
			DBLOG.trace("Caching: " + s);
			cachedShelves.put(s.getDatabaseID(), s);
		}

		DBLOG.info("All " + result.size() + " Shelves cached.");

		setAllContainersToAllShelves();
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
	 * Clears all cached data.
	 */
	@Deprecated
	public static void clearAllCaches()
	{
		// TODO: Get rid of this.
		DBLOG.info("Clearing all cached data.");
		cachedProductBoxes.clear();
		cachedProductBrands.clear();
		cachedProductCategories.clear();
		cachedProducts.clear();
		cachedProductTypes.clear();
		cachedRemovalLists.clear();
		cachedRemovalListStates.clear();
		cachedShelves.clear();
	}

	/**
	 * Gets all the {@link RemovalList} objects that have been loaded from the
	 * database at some point during the execution of the program.
	 *
	 * @return a map of cached removal lists where the key is the database ID
	 * and the value is the object
	 */
	@Deprecated
	public static Map<Integer, RemovalList> getCachedRemovalLists()
	{
		// TODO: Get rid of this.
		return cachedRemovalLists;
	}

	/**
	 * Clears the observable list of product box search results.
	 */
	public static void clearSearchResults()
	{
		DBLOG.info("Clearing search results.");
		observableProductBoxSearchResults.clear();
	}
}
