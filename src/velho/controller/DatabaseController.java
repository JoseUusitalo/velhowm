package velho.controller;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.h2.jdbcx.JdbcConnectionPool;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import velho.model.Manager;
import velho.model.Product;
import velho.model.ProductBox;
import velho.model.ProductBoxSearchResultRow;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.model.ProductContainer;
import velho.model.ProductType;
import velho.model.RemovalList;
import velho.model.RemovalListState;
import velho.model.Shelf;
import velho.model.User;
import velho.model.enums.DatabaseQueryType;
import velho.model.enums.DatabaseTable;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.model.interfaces.UserRole;
import velho.view.MainWindow;

/**
 * The H2 database controller.
 *
 * @author Jose Uusitalo
 */
public class DatabaseController
{
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

	/*
	 * ---- UI LISTS ----
	 */

	/**
	 * An observable list of users for display in the user interface.
	 */
	private static ObservableList<Object> observableUsers = FXCollections.observableArrayList();

	/**
	 * An observable list of {@link Product} objects display in the user
	 * interface.
	 */
	private static ObservableList<Object> observableProducts = FXCollections.observableArrayList();

	/**
	 * An observable list of {@link Product} search results for display in the
	 * user interface.
	 */
	private static ObservableList<Object> observableProductSearchResults = FXCollections.observableArrayList();

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

	/*
	 * ---- CACHE MAPS ----
	 */

	/**
	 * A map of {@link ProductBrand} objects loaded from the database.
	 */
	private static Map<Integer, ProductBrand> cachedProductBrands = new HashMap<Integer, ProductBrand>();

	/**
	 * A map of {@link ProductType} objects loaded from the database.
	 */
	private static Map<Integer, ProductType> cachedProductTypes = new HashMap<Integer, ProductType>();

	/**
	 * A map of {@link ProductCategory} objects loaded from the database.
	 */
	private static Map<Integer, ProductCategory> cachedProductCategories = new HashMap<Integer, ProductCategory>();

	/**
	 * A map of {@link Product} objects loaded from the database.
	 */
	private static Map<Integer, Product> cachedProducts = new HashMap<Integer, Product>();

	/**
	 * A map of {@link ProductContainer} objects loaded from the database.
	 */
	private static Map<Integer, ProductBox> cachedProductBoxes = new HashMap<Integer, ProductBox>();

	/**
	 * A map of {@link Shelf} objects loaded from the database.
	 */
	private static Map<Integer, Shelf> cachedShelves = new HashMap<Integer, Shelf>();

	/**
	 * A map of {@link RemovalList} objects loaded from the database.
	 */
	private static Map<Integer, RemovalList> cachedRemovalLists = new HashMap<Integer, RemovalList>();

	/**
	 * A map of {@link RemovalListState} objects loaded from the database.
	 */
	private static Map<Integer, RemovalListState> cachedRemovalListStates = new HashMap<Integer, RemovalListState>();

	/*
	 * -------------------------------- PRIVATE DATABASE METHODS --------------------------------
	 */

	/**
	 * Creates an SQL query out of the given data.
	 *
	 * @param type
	 *            query command
	 * @param tableName
	 *            name of the table
	 * @param columns
	 *            columns to select (can be <code>null</code>)
	 * @param where
	 *            conditions (can be <code>null</code>)
	 * @return an SQL query string
	 */
	private static String sqlBuilder(final DatabaseQueryType type, final DatabaseTable tableName, final Map<DatabaseTable, String> joinOnCondition, final String[] columns, final Map<String, Object> columnValues, final List<String> where)
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

		if (MainWindow.PRINT_SQL)
			System.out.println("[SQLBUILDER] " + sb.toString());

		return sb.toString();
	}

	/**
	 * Runs a database query with the given data.
	 *
	 * @param type
	 *            query command
	 * @param tableName
	 *            name of the table
	 * @param columns
	 *            columns to select (can be <code>null</code>)
	 * @param where
	 *            conditions (can be <code>null</code>)
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
	private static Object runQuery(final DatabaseQueryType type, final DatabaseTable tableName, final Map<DatabaseTable, String> joinOnValues, final String[] columns, final Map<String, Object> columnValues, final List<String> where) throws NoDatabaseLinkException
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
						// Get the database rows that were created or an empty set if none were created.
						result = statement.getGeneratedKeys();
						if (result != null)
						{
							// This is silly. Why isn't there an easier way to get the number of rows in a ResultSet?
							result.last();
							final int rowCount = result.getRow();
							result.beforeFirst();

							if (rowCount == 0)
							{
								/*
								 * A successfull DELETE statement does not return anything but we need /something/ in
								 * the dataSet.
								 */
								dataSet.add(true);
							}
							else
							{
								// INSERT and UPDATE statements return the updated/inserted rows.
								// Get all the IDs of the rows that were updated.
								while (result.next())
									dataSet.add(result.getInt(1));
							}
						}

						try
						{
							if (result != null)
								result.close();
						} catch (SQLException e)
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
									dataSet.add(new User(result.getInt("user_id"), result.getString("first_name"), result.getString("last_name"), getRoleByID(result.getInt("role"))));
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
									dataSet.add(new ProductCategory(result.getInt("category_id"), result.getString("name"), getProductTypeByID(result.getInt("type"))));
								break;

							case PRODUCTS:
								// @formatter:off
								while (result.next())
									dataSet.add(new Product(result.getInt("product_id"), result.getString("name"), getProductBrandByID(result.getInt("brand")), getProductCategoryByID(result.getInt("category")), result.getInt("popularity")));
								break;
							// @formatter:on

							case CONTAINERS:
								// @formatter:off
								while (result.next())
									dataSet.add(new ProductBox(result.getInt("container_id"), result.getDate("expiration_date"), result.getInt("max_size"), getProductByID(result.getInt("product")), result.getInt("product_count")));
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
										// Create a new list and put in the data.
										list = new ArrayList<Integer[]>();
										list.add(coords);
										shelfBoxMap.put(shelfID, list);
									}
								}

								break;

							case REMOVALLISTS:
								// @formatter:off
								while (result.next())
									dataSet.add(new RemovalList(result.getInt("removallist_id"), getRemovalListStateByID(result.getInt("liststate"))));
								break;
							// @formatter:on

							case REMOVALLIST_PRODUCTBOXES:
								listBoxMap = new HashMap<Integer, ArrayList<Integer>>();
								Integer listID = null;
								ArrayList<Integer> boxIDs = new ArrayList<Integer>();

								while (result.next())
								{
									listID = result.getInt("removallist");

									// Does this removal list already have boxes in it?
									if (listBoxMap.containsKey(listID))
									{
										// Add to the list.
										listBoxMap.get(listID).add(result.getInt("productbox"));
									}
									else
									{
										// Create a new list and put in the data.
										boxIDs = new ArrayList<Integer>();
										boxIDs.add(result.getInt("productbox"));
										listBoxMap.put(listID, boxIDs);
									}
								}

								break;
							default:
								// Close all resources.
								try
								{
									result.close();
								} catch (final SQLException e)
								{
									e.printStackTrace();
								}

								try
								{
									statement.close();
								} catch (final SQLException e)
								{
									e.printStackTrace();
								}

								try
								{
									connection.close();
								} catch (final SQLException e)
								{
									e.printStackTrace();
								}
								throw new IllegalArgumentException();
						}
					}

					try
					{
						result.close();
					} catch (SQLException e)
					{
						e.printStackTrace();
					}
					break;
				default:
					// Close all resources.
					try
					{
						statement.close();
					} catch (final SQLException e)
					{
						e.printStackTrace();
					}

					try
					{
						connection.close();
					} catch (final SQLException e)
					{
						e.printStackTrace();
					}
					throw new IllegalArgumentException();
			}
		} catch (final IllegalStateException e)
		{
			// Close all resources.
			try
			{
				if (statement != null)
					statement.close();
			} catch (final SQLException e1)
			{
				e.printStackTrace();
			}

			try
			{
				connection.close();
			} catch (final SQLException e2)
			{
				e.printStackTrace();
			}

			// Connection pool has been disposed = no database connection.
			throw new NoDatabaseLinkException();
		} catch (final SQLException e)
		{
			if (!e.toString().contains("Unique index or primary key violation"))
				e.printStackTrace();

			// If it was a UNIQUE constraint violation, continue normally as those are handled separately.
			System.out.println("[DatabaseController] Silently ignored an SQL UNIQUE constraint violation with: \n");
			System.out.println(e.getMessage());
			System.out.println("\n[DatabaseController] End of message.");
		}

		// Close all resources.
		try
		{
			if (statement != null)
				statement.close();
		} catch (final SQLException e)
		{
			e.printStackTrace();
		}

		try
		{
			connection.close();
		} catch (final SQLException e)
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
						return dataSet;
					case SHELF_PRODUCTBOXES:
						return shelfBoxMap;
					case REMOVALLIST_PRODUCTBOXES:
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
	 *            SQL to run
	 * @return an Object containing the appropriate data
	 * @throws NoDatabaseLinkException
	 */
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
		} catch (final IllegalStateException e)
		{
			// Close all resources.

			try
			{
				if (statement != null)
					statement.close();
			} catch (final SQLException e1)
			{
				e.printStackTrace();
			}

			try
			{
				connection.close();
			} catch (final SQLException e2)
			{
				e.printStackTrace();
			}

			// Connection pool has been disposed = no database connection.
			throw new NoDatabaseLinkException();
		} catch (final SQLException e)
		{
			if (!e.toString().contains("Unique index or primary key violation"))
				e.printStackTrace();

			// If it was a UNIQUE constraint violation, continue normally as those are handled separately.
			System.out.println("[DatabaseController] Silently ignored an SQL UNIQUE constraint violation with: \n");
			System.out.println(e.getMessage());
			System.out.println("\n[DatabaseController] End of message.");
		}

		// Close all resources.
		try
		{
			if (statement != null)
				statement.close();
		} catch (final SQLException e)
		{
			e.printStackTrace();
		}

		try
		{
			connection.close();
		} catch (final SQLException e)
		{
			e.printStackTrace();
		}

		return changed;
	}

	/**
	 * Attempts to re-link the database.
	 */
	private static void relink()
	{
		System.out.println("[DatabaseController] Attempting relink.");
		try
		{
			// Just in case.
			unlink();
		} catch (final NoDatabaseLinkException e)
		{
			// Do nothing. This is expected.
		}

		try
		{
			link();
		} catch (final ClassNotFoundException e)
		{
			e.printStackTrace();
		} catch (final ExistingDatabaseLinkException e)
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
	private static Connection getConnection() throws NoDatabaseLinkException
	{
		checkLink();

		Connection connection = null;
		try
		{
			connection = connectionPool.getConnection();
		} catch (final SQLException e)
		{
			if (e.getMessage().contains("Database may be already in use"))
			{
				System.out.println("Database is already in use.");
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
	 * {@link NoDatabaseConnectionException} exception if it doesn't. To be used
	 * when a database link must exist.
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
		try
		{
			// Just in case.
			unlink();
		} catch (final NoDatabaseLinkException e)
		{
			// Do nothing. This is expected.
		}

		try
		{
			link();
		} catch (final ClassNotFoundException e)
		{
			e.printStackTrace();
		} catch (final ExistingDatabaseLinkException e)
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
	 *             when the H2 driver was unable to load
	 * @throws ExistingDatabaseLinkException
	 *             when a database link already exists
	 */
	public static boolean link() throws ClassNotFoundException, ExistingDatabaseLinkException
	{
		if (connectionPool != null)
			throw new ExistingDatabaseLinkException();

		// Load the driver.
		Class.forName("org.h2.Driver");

		String uri = DB_URI;

		/*
		 * If the database does not exists, it will be created with the connection pool.
		 * Otherwise add this check after the URI for extra security.
		 */
		if (databaseExists())
		{
			uri += ";IFEXISTS=TRUE";
		}
		else
			System.out.println("Database does not exist, creating a new database.");

		// Create a connection pool.
		connectionPool = JdbcConnectionPool.create(uri, USERNAME, "@_Vry $ECURE pword2");

		// Try getting a connection. If the database does not exist, it is created.
		try
		{
			final Connection c = getConnection();

			if (c == null)
				return false;

			c.close();
		} catch (final NoDatabaseLinkException e)
		{
			e.printStackTrace();
		} catch (final SQLException e)
		{
			System.out.println("whoa");
			e.printStackTrace();
		}

		if (databaseExists())
		{
			if (isLinked())
			{
				System.out.println("Database linked.");
				return true;
			}

			System.out.println("Database linking failed.");
			return false;
		}

		System.out.println("Database creation failed.");
		return false;
	}

	/**
	 * Shuts down the connection to the database. Use {@link #link()} to connect
	 * to the database again.
	 *
	 * @throws NoDatabaseLinkException
	 *             when attempting unlink a database when no database link
	 *             exists
	 */
	public static void unlink() throws NoDatabaseLinkException
	{
		if (connectionPool == null)
			throw new NoDatabaseLinkException();

		connectionPool.dispose();
		connectionPool = null;
		System.out.println("Database unlinked.");
	}

	/**
	 * Links and initializes the database.
	 */
	public static boolean connectAndInitialize() throws ClassNotFoundException, ExistingDatabaseLinkException, NoDatabaseLinkException
	{
		return DatabaseController.link() && DatabaseController.initializeDatabase();
	}

	/*
	 * -------------------------------- PRIVATE GETTER METHODS --------------------------------
	 */

	/**
	 * Gets the {@link UserRole} object from the given role ID.
	 *
	 * @param roleid
	 *            role database ID
	 * @return the corresponding user role object
	 * @throws NoDatabaseLinkException
	 */
	private static UserRole getRoleByID(final int roleid) throws NoDatabaseLinkException
	{
		final String[] columns =
		{ "name" };
		final List<String> where = new ArrayList<String>();
		where.add("role_id = " + new Integer(roleid));

		@SuppressWarnings("unchecked") final Set<String> result = (LinkedHashSet<String>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.ROLES, null, columns, null, where));

		if (result.size() == 0)
			return null;

		return UserController.stringToRole(result.iterator().next());
	}

	/**
	 * Gets the {@link Product} object from the given product name.
	 *
	 * @param name
	 *            the exact product name
	 * @return the corresponding product object
	 * @throws NoDatabaseLinkException
	 */
	@SuppressWarnings("unused") private static List<ProductBox> getProductBoxesByProductName(final List<String> where) throws NoDatabaseLinkException
	{
		final String[] columns =
		{ "containers.container_id" };

		final Map<DatabaseTable, String> join = new LinkedHashMap<DatabaseTable, String>();
		join.put(DatabaseTable.PRODUCTS, "containers.product = products.product_id");

		@SuppressWarnings("unchecked") final Set<Integer> result = (LinkedHashSet<Integer>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.CONTAINERS, join, columns, null, where));

		final List<ProductBox> boxes = new ArrayList<ProductBox>();
		final Iterator<Integer> it = result.iterator();

		while (it.hasNext())
			boxes.add(getProductBoxByID(it.next()));

		return boxes;
	}

	/**
	 * Gets the {@link Product} object from the given product name.
	 *
	 * @param name
	 *            the exact product name
	 * @return the corresponding product object
	 * @throws NoDatabaseLinkException
	 */
	private static List<ProductBox> getProductBoxesByProductID(final List<String> where) throws NoDatabaseLinkException
	{
		final String[] columns =
		{ "container_id" };

		@SuppressWarnings("unchecked") final Set<Integer> result = (LinkedHashSet<Integer>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.CONTAINERS, null, columns, null, where));

		final List<ProductBox> boxes = new ArrayList<ProductBox>();
		final Iterator<Integer> it = result.iterator();

		while (it.hasNext())
			boxes.add(getProductBoxByID(it.next()));

		return boxes;
	}

	/**
	 * Gets the {@link RemovalListState} object from the given database ID.
	 *
	 * @param stateid
	 *            removal list state database ID
	 * @return the corresponding removal list state object
	 * @throws NoDatabaseLinkException
	 */
	private static RemovalListState getRemovalListStateByID(final int stateid) throws NoDatabaseLinkException
	{
		if (!cachedRemovalListStates.containsKey(stateid))
		{
			final String[] columns =
			{ "name" };
			final List<String> where = new ArrayList<String>();
			where.add("removallist_state_id = " + new Integer(stateid));

			@SuppressWarnings("unchecked") final Set<String> result = (LinkedHashSet<String>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.REMOVALLIST_STATES, null, columns, null, where));

			if (result.size() == 0)
				return null;

			final RemovalListState s = new RemovalListState(stateid, result.iterator().next());

			// Store for reuse.

			if (MainWindow.PRINT_CACHE_MESSAGES)
				System.out.println("Caching: " + s);

			cachedRemovalListStates.put(s.getDatabaseID(), s);
			return s;
		}

		if (MainWindow.PRINT_CACHE_MESSAGES)
			System.out.println("Loading removal list state " + stateid + " from cache.");
		return cachedRemovalListStates.get(stateid);
	}

	/**
	 * Gets the {@link ProductType} object from the given type ID.
	 *
	 * @param typeid
	 *            product type database ID
	 * @return the corresponding product type object
	 * @throws NoDatabaseLinkException
	 */
	private static ProductType getProductTypeByID(final int typeid) throws NoDatabaseLinkException
	{
		if (!cachedProductTypes.containsKey(typeid))
		{
			final String[] columns =
			{ "name" };
			final List<String> where = new ArrayList<String>();
			where.add("type_id = " + new Integer(typeid));

			@SuppressWarnings("unchecked") final Set<String> result = (LinkedHashSet<String>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.TYPES, null, columns, null, where));

			if (result.size() == 0)
				return null;

			final ProductType p = new ProductType(typeid, result.iterator().next());

			// Store for reuse.

			if (MainWindow.PRINT_CACHE_MESSAGES)
				System.out.println("Caching: " + p);

			cachedProductTypes.put(p.getDatabaseID(), p);
			return p;
		}

		if (MainWindow.PRINT_CACHE_MESSAGES)
			System.out.println("Loading category " + typeid + " from cache.");
		return cachedProductTypes.get(typeid);
	}

	/**
	 * Gets the {@link ProductCategory} object from the given category ID.
	 *
	 * @param categoryid
	 *            product category database ID
	 * @return the corresponding product category object
	 * @throws NoDatabaseLinkException
	 */
	private static ProductCategory getProductCategoryByID(final int categoryid) throws NoDatabaseLinkException
	{
		if (!cachedProductCategories.containsKey(categoryid))
		{
			final String[] columns =
			{ "category_id", "name", "type" };
			final List<String> where = new ArrayList<String>();
			where.add("category_id = " + new Integer(categoryid));

			@SuppressWarnings("unchecked") final Set<Object> result = (LinkedHashSet<Object>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.CATEGORIES, null, columns, null, where));

			if (result.size() == 0)
				return null;

			final ProductCategory p = (ProductCategory) result.iterator().next();

			// Store for reuse.
			if (MainWindow.PRINT_CACHE_MESSAGES)
				System.out.println("Caching: " + p);

			cachedProductCategories.put(p.getDatabaseID(), p);
			return p;
		}

		if (MainWindow.PRINT_CACHE_MESSAGES)
			System.out.println("Loading category " + categoryid + " from cache.");
		return cachedProductCategories.get(categoryid);
	}

	/**
	 * Gets the {@link ProductBrand} object from the given brand ID.
	 *
	 * @param brandid
	 *            product brand database ID
	 * @return the corresponding product brand object
	 * @throws NoDatabaseLinkException
	 */
	private static ProductBrand getProductBrandByID(final int brandid) throws NoDatabaseLinkException
	{
		if (!cachedProductBrands.containsKey(brandid))
		{
			final String[] columns =
			{ "name" };
			final List<String> where = new ArrayList<String>();
			where.add("brand_id = " + new Integer(brandid));

			@SuppressWarnings("unchecked") final Set<String> result = (LinkedHashSet<String>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.BRANDS, null, columns, null, where));

			if (result.size() == 0)
				return null;

			final ProductBrand p = new ProductBrand(brandid, result.iterator().next());

			// Store for reuse.
			if (MainWindow.PRINT_CACHE_MESSAGES)
				System.out.println("Caching: " + p);

			cachedProductBrands.put(p.getDatabaseID(), p);
			return p;
		}

		if (MainWindow.PRINT_CACHE_MESSAGES)
			System.out.println("Loading brand " + brandid + " from cache.");
		return cachedProductBrands.get(brandid);
	}

	/**
	 * Gets the {@link Product} object from the given product ID.
	 *
	 * @param productid
	 *            product database ID
	 * @return the corresponding product object
	 * @throws NoDatabaseLinkException
	 */
	private static Product getProductByID(final int productid) throws NoDatabaseLinkException
	{
		if (!cachedProducts.containsKey(productid))
		{
			final String[] columns =
			{ "*" };
			final List<String> where = new ArrayList<String>();
			where.add("product_id = " + new Integer(productid));

			@SuppressWarnings("unchecked") final Set<Object> result = (LinkedHashSet<Object>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.PRODUCTS, null, columns, null, where));

			if (result.size() == 0)
				return null;

			final Product p = (Product) result.iterator().next();

			// Store for reuse.
			if (MainWindow.PRINT_CACHE_MESSAGES)
				System.out.println("Caching: " + p);

			cachedProducts.put(p.getProductID(), p);
			return p;
		}

		if (MainWindow.PRINT_CACHE_MESSAGES)
			System.out.println("Loading product " + productid + " from cache.");
		return cachedProducts.get(productid);
	}

	/**
	 * Gets the product box that will expire the soonest.
	 *
	 * @param boxes
	 *            boxes to search from
	 * @return the oldest product box
	 */
	private static ProductBox getOldestProductBox(final List<ProductBox> boxes)
	{
		System.out.println("+++got boxes " + boxes);
		ProductBox oldest = boxes.get(0);

		for (final ProductBox box : boxes)
		{
			if (box.getExpirationDate() != null)
			{
				// Current box has an expiration date.
				if (box.getExpirationDate() != null)
				{
					// Oldest box has an expiration date.
					if (box.getExpirationDate().before(oldest.getExpirationDate()))
					{
						// Current box expires first.
						oldest = box;
					}
				}
				else
				{
					// Current box expires first.
					oldest = box;
				}
			}
			// Else current box does not have an expiration date.
		}

		return oldest;
	}

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

		if (MainWindow.DEBUG_MODE)
		{
			if (emptyCount == 0)
				System.out.println("Found " + boxes.size() + " product boxes.");
			else
				System.out.println("Found " + boxes.size() + " product boxes of which " + emptyCount + " were empty.");
		}

		if (MainWindow.DEBUG_MODE)
		{
			System.out.println("Found product box sizes:");
			for (final Integer i : boxProductCount.keySet())
				System.out.println(i + " (x" + boxProductCount.get(i).size() + ")");
		}

		// Convert the ordered key set to an arrray for binary search.
		Integer[] boxSizeArray = new Integer[boxProductCount.size()];
		boxSizeArray = boxProductCount.keySet().toArray(boxSizeArray);

		// Find the index at which the wantedProductCount Integer would be in, if it were in the array.
		wantedWouldBeIndex = -Arrays.binarySearch(boxSizeArray, wantedProductCount) - 1;
		addCountIndex = wantedWouldBeIndex;

		if (MainWindow.DEBUG_MODE)
			System.out.println("Wanted box of size " + wantedProductCount + " would have been at index " + wantedWouldBeIndex + ".");

		// Keep adding boxes to the foundProducts until we reach the wanted product count.
		while (productCountSum < wantedProductCount)
		{
			System.out.println("Search status | Count: " + productCountSum + " / " + wantedProductCount + " | Would Be Index: " + wantedWouldBeIndex + " | Add Index: " + addCountIndex + " / " + boxProductCount.size());

			// Looking for too few products, show the smallest box of that product instead.
			if (wantedWouldBeIndex == 0)
				addCountIndex++;

			try
			{
				// The next smallest product boxes.
				// Can throw an IndexOutOfBounds exception.
				nextSmallestBoxes = boxProductCount.get(boxSizeArray[--addCountIndex]);

				if (MainWindow.DEBUG_MODE)
					System.out.println("The next smallest product boxes at index " + addCountIndex + " are: " + nextSmallestBoxes);

				for (final ProductBox box : nextSmallestBoxes)
				{
					// Does adding this box to the set still keep the product counter under the wanted amount?
					// OR does a box that small not exist?
					if (wantedWouldBeIndex == 0 || addCountIndex == 0 || ((productCountSum + box.getProductCount() <= wantedProductCount)))
					{
						// Add up the product count.
						productCountSum += box.getProductCount();

						if (MainWindow.DEBUG_MODE)
							System.out.println(box + " added to list, current product count sum is " + productCountSum + ".");

						// Add the found box to the set.
						resultingBoxes.add(box);
					}
					else if (lookingForLarger)
					{
						// Allow going over the wanted size limit if looking for a larger box.
						// Add up the product count.
						productCountSum += box.getProductCount();

						if (MainWindow.DEBUG_MODE)
							System.out.println(box + " added to list, current product count sum is " + productCountSum + ".");

						// Add the found box to the set.
						resultingBoxes.add(box);

						// Found the box that we wanted.
						break;
					}
					else
					{
						// Else break the loop and find the next smallest boxes.
						if (MainWindow.DEBUG_MODE)
							System.out.println("Adding " + box + " to list would put the product count over the limit.");

						break;
					}
				}
			} catch (final IndexOutOfBoundsException e)
			{
				if (resultingBoxes.size() == boxes.size())
				{
					// All boxes are in the result. Too bad.
					System.out.println("Unable to find that many products. Listing all available boxes.");
					break;
				}
				else if (wantedProductCount > productCountSum)
				{
					/*
					 * All boxes are not in the result.
					 * Still have not reached the wanted product count.
					 * Restart the loop at one higher index.
					 * Repeat until either the wanted count is reached or all products have been added to the result.
					 */
					System.out.println("Unable to build a product box list of that size from smaller boxes. Going one size larger.");
					resultingBoxes.clear();
					productCountSum = 0;
					wantedWouldBeIndex++;
					addCountIndex = wantedWouldBeIndex;
					lookingForLarger = true;

					// FIXME: Infinite loop when looking for more products than are available.
				}
				else
				{
					System.out.println("what");
					System.out.println("Search status | Count: " + productCountSum + " / " + wantedProductCount + " | Would Be Index: " + wantedWouldBeIndex + " | Add Index: " + addCountIndex + " / " + boxProductCount.size());
				}
			}
		}

		return resultingBoxes;
	}

	/*
	 * -------------------------------- PUBLIC GETTER METHODS --------------------------------
	 */

	/**
	 * Gets a map of columns and column names for displaying
	 * {@link #getPublicUserDataList()} data in a table.
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
	 * Gets a map of columns and column names for displaying
	 * {@link #getPublicProductDataList()} data in a table.
	 *
	 * @return a map where the key is the column value and value is the column
	 *         name
	 */
	public static Map<String, String> getPublicProductDataColumns(final boolean withAddColumn, final boolean withDeleteColumn)
	{
		final LinkedHashMap<String, String> cols = new LinkedHashMap<String, String>();

		if (withAddColumn)
			cols.put("addButton", "Add");

		if (withDeleteColumn)
			cols.put("deleteButton", "Delete");

		cols.put("name", "Name");
		cols.put("brand", "Brand");
		cols.put("category", "Category");
		cols.put("popularity", "Popularity");

		return cols;
	}

	/**
	 * Gets a map of columns and column names for displaying
	 * {@link #getPublicProductDataList()} data in a table.
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
		if (LoginController.getCurrentUser().getRole().compareTo(new Manager()) >= 0)
			cols.put("deleteButton", "Delete");

		return cols;
	}

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
		cols.put("boxShelfSlot", "Shelf Slot");
		cols.put("boxProductCount", "Amount");

		return cols;
	}

	/**
	 * Gets the database ID of the given user role name.
	 *
	 * @param roleName
	 *            the name of the role
	 * @return the database ID of the given role (a value greater than 0) or
	 *         <code>-1</code> if the role does not exist in the database
	 */
	public static int getRoleID(final String roleName) throws NoDatabaseLinkException
	{
		final String[] columns =
		{ "role_id" };
		final List<String> where = new ArrayList<String>();
		where.add("name = '" + roleName + "'");

		@SuppressWarnings("unchecked") final Set<Integer> result = (LinkedHashSet<Integer>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.ROLES, null, columns, null, where));

		if (result.size() == 0)
			return -1;

		// Only one result as IDs are unique.
		return result.iterator().next().intValue();
	}

	/**
	 * Gets a set of user role names in the database.
	 *
	 * @return a set of user role names
	 * @throws NoDatabaseLinkException
	 */
	public static Set<String> getUserRoleNames() throws NoDatabaseLinkException
	{
		final String[] columns =
		{ "name" };

		@SuppressWarnings("unchecked") final Set<String> result = (LinkedHashSet<String>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.ROLES, null, columns, null, null));

		return result;
	}

	/**
	 * <p>
	 * Authenticates a user with the given badge ID string.
	 * </p>
	 * <p>
	 * Warnign: Assumes that the badge ID is techinically valid.
	 * </p>
	 *
	 * @param badgeID
	 *            a badge ID string
	 * @return a {@link User} object representing the authenticated user or
	 *         <code>null</code> for invalid credentials
	 * @throws NoDatabaseLinkException
	 * @see {@link User#isValidBadgeID(String)}
	 */
	public static User authenticateBadgeID(final String badgeID) throws NoDatabaseLinkException
	{
		try
		{
			Integer.parseInt(badgeID);
		} catch (final NumberFormatException e)
		{
			// Although badge IDs are stored as string, they are still numbers.
			return null;
		}

		final String[] columns =
		{ "user_id", "first_name", "last_name", "role" };
		final List<String> where = new ArrayList<String>();
		where.add("badge_id = " + badgeID);

		@SuppressWarnings("unchecked") final Set<User> result = (LinkedHashSet<User>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.USERS, null, columns, null, where));

		if (result.size() == 0)
			return null;

		return result.iterator().next();
	}

	/**
	 * <p>
	 * Authenticates a user with the given PIN string.
	 * </p>
	 * <p>
	 * Warnign: Assumes that the PIN is techinically valid.
	 * </p>
	 *
	 * @param pin
	 *            a PIN string
	 * @return a {@link User} object representing the authenticated user or
	 *         <code>null</code> for invalid credentials
	 * @throws NoDatabaseLinkException
	 * @see {@link User#isValidPIN(String)}
	 */
	public static User authenticatePIN(final String firstName, final String lastName, final String pin) throws NoDatabaseLinkException
	{
		final String[] columns =
		{ "user_id", "first_name", "last_name", "role" };
		final List<String> where = new ArrayList<String>();
		where.add("first_name = '" + firstName + "'");
		where.add("last_name = '" + lastName + "'");
		where.add("pin = " + pin);

		@SuppressWarnings("unchecked") final Set<User> result = (LinkedHashSet<User>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.USERS, null, columns, null, where));

		if (result.size() == 0)
			return null;

		return result.iterator().next();
	}

	/**
	 * Gets the {@link Product} object from the given product ID.
	 *
	 * @param productid
	 *            product database ID
	 * @return the corresponding product object
	 * @throws NoDatabaseLinkException
	 */
	public static ProductBox getProductBoxByID(final int productboxid) throws NoDatabaseLinkException
	{
		if (!cachedProductBoxes.containsKey(productboxid))
		{
			final String[] columns =
			{ "*" };
			final List<String> where = new ArrayList<String>();
			where.add("container_id = " + new Integer(productboxid));

			@SuppressWarnings("unchecked") final Set<ProductBox> result = (LinkedHashSet<ProductBox>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.CONTAINERS, null, columns, null, where));

			if (result.size() == 0)
				return null;

			final ProductBox p = result.iterator().next();

			// Store for reuse.
			if (MainWindow.PRINT_CACHE_MESSAGES)
				System.out.println("Caching: " + p);

			cachedProductBoxes.put(p.getBoxID(), p);
			return p;
		}

		if (MainWindow.PRINT_CACHE_MESSAGES)
			System.out.println("Loading product box " + productboxid + " from cache.");
		return cachedProductBoxes.get(productboxid);
	}

	/**
	 * Gets user data by their database ID.
	 *
	 * @param id
	 *            database ID of the user
	 * @return a {@link User} object or <code>null</code> if a user with that ID
	 *         was not found
	 */
	public static User getUserByID(final int id) throws NoDatabaseLinkException
	{
		final String[] columns =
		{ "user_id", "first_name", "last_name", "role" };
		final List<String> where = new ArrayList<String>();
		where.add("user_id = " + new Integer(id));

		@SuppressWarnings("unchecked") final Set<User> result = (LinkedHashSet<User>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.USERS, null, columns, null, where));

		if (result.size() == 0)
			return null;

		// Only one result as IDs are unique.
		return result.iterator().next();
	}

	/**
	 * Gets the product ID by its name.
	 *
	 * @param name
	 *            unique name of the product
	 * @return the database ID of the product or <code>-1</code> if the product
	 *         is not present in the database
	 */
	public static int getProductIDFromName(final String name) throws NoDatabaseLinkException
	{
		final String[] columns =
		{ "product_id" };
		final List<String> where = new ArrayList<String>();
		where.add("name = '" + name + "'");

		@SuppressWarnings("unchecked") final Set<Integer> result = (LinkedHashSet<Integer>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.PRODUCTS, null, columns, null, where));

		if (result.size() == 0)
			return -1;

		// Only one result as names are unique.
		return result.iterator().next();
	}

	/**
	 * Gets the {@link Shelf} object from the given shelf ID.
	 *
	 * @param shelfid
	 *            shelf database ID
	 * @return the corresponding shelf object
	 * @throws NoDatabaseLinkException
	 */
	public static Shelf getShelfByID(final int shelfid, final boolean getCached) throws NoDatabaseLinkException
	{
		if (!cachedShelves.containsKey(shelfid) || !getCached)
		{
			final String[] columns =
			{ "*" };
			final List<String> where = new ArrayList<String>();
			where.add("shelf_id = " + new Integer(shelfid));

			@SuppressWarnings("unchecked") final Set<Object> result = (LinkedHashSet<Object>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.SHELVES, null, columns, null, where));

			if (result.size() == 0)
				return null;

			final Shelf s = (Shelf) result.iterator().next();

			// Store for reuse.
			if (getCached)
			{
				if (MainWindow.PRINT_CACHE_MESSAGES)
					System.out.println("Caching: " + s);
			}
			else
			{
				if (MainWindow.PRINT_CACHE_MESSAGES)
					System.out.println("Updating cache: " + s);
			}

			cachedShelves.put(s.getDatabaseID(), s);

			setContainersToShelf(shelfid);

			return cachedShelves.get(shelfid);
		}

		if (MainWindow.PRINT_CACHE_MESSAGES)
			System.out.println("Loading shelf " + shelfid + " from cache.");
		return cachedShelves.get(shelfid);
	}

	/**
	 * Gets the {@link RemovalList} object from the given removal list ID.
	 *
	 * @param listid
	 *            removal list database ID
	 * @return the corresponding removal list object
	 * @throws NoDatabaseLinkException
	 */
	public static RemovalList getRemovalListByID(final int listid, final boolean getCached) throws NoDatabaseLinkException
	{
		if (!cachedRemovalLists.containsKey(listid) || !getCached)
		{
			final String[] columns =
			{ "*" };
			final List<String> where = new ArrayList<String>();
			where.add("removallist_id = " + listid);

			@SuppressWarnings("unchecked") final Set<Object> result = (LinkedHashSet<Object>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.REMOVALLISTS, null, columns, null, where));

			if (result.size() == 0)
				return null;

			final RemovalList r = (RemovalList) result.iterator().next();

			// Store for reuse.
			if (getCached)
			{
				if (MainWindow.PRINT_CACHE_MESSAGES)
					System.out.println("Caching: " + r);
			}
			else
			{
				if (MainWindow.PRINT_CACHE_MESSAGES)
					System.out.println("Updating cache: " + r);
			}

			cachedRemovalLists.put(r.getDatabaseID(), r);

			setContainersToRemovalList(listid);

			return cachedRemovalLists.get(listid);
		}

		if (MainWindow.PRINT_CACHE_MESSAGES)
			System.out.println("Loading removal list " + listid + " from cache.");
		return cachedRemovalLists.get(listid);
	}

	/**
	 * Gets a random valid shelf slot ID string.
	 *
	 * @return random valid shelf slot ID string
	 */
	public static String getRandomShelfSlot() throws NoDatabaseLinkException
	{
		final String[] columns =
		{ "shelf_id" };

		@SuppressWarnings("unchecked") final Set<Integer> result = (LinkedHashSet<Integer>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.SHELVES, null, columns, null, null));

		final List<Integer> list = new ArrayList<Integer>(result);

		Collections.shuffle(list);

		final Shelf randomShelf = getShelfByID(list.get(0), true);
		final int randomLevel = (int) (Math.round(Math.random() * (randomShelf.getLevelCount() - 1) + 1));
		final int randomSlotIndex = (int) (Math.round(Math.random() * (randomShelf.getShelfSlotCount() / randomShelf.getLevelCount() - 1)));

		System.out.println("CheckProtocol " + list.get(0) + " ! " + randomShelf);

		return Shelf.coordinatesToShelfSlotID(list.get(0), randomLevel, randomSlotIndex, true);
	}

	/**
	 * Gets a list product codes in the database.
	 *
	 * @return a list of integer product codes
	 */
	public static List<Integer> getProductCodeList() throws NoDatabaseLinkException
	{
		final String[] columns =
		{ "product_id" };

		@SuppressWarnings("unchecked") final Set<Integer> result = (LinkedHashSet<Integer>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.PRODUCTS, null, columns, null, null));
		final List<Integer> ints = new ArrayList<Integer>();
		ints.addAll(result);
		return ints;
	}

	/**
	 * Gets an {@link ObservableList} of product data in shelves.
	 *
	 * @return a list of products in the database currently on shelves
	 */
	public static ObservableList<Object> getObservableProducts()
	{
		observableProducts.clear();
		observableProducts.addAll(cachedProducts.values());
		return observableProducts;
	}

	/**
	 * Gets an {@link ObservableList} of cached product search results.
	 *
	 * @return a cached list of products searched by the user
	 */
	public static ObservableList<Object> getObservableProductSearchResults()
	{
		return observableProductSearchResults;
	}

	/**
	 * Gets an {@link ObservableList} of cached removal lists.
	 *
	 * @return a cached list of removal lists
	 */
	public static ObservableList<Object> getObservableRemovalLists()
	{
		observableRemovalLists.clear();
		observableRemovalLists.addAll(cachedRemovalLists.values());
		return observableRemovalLists;
	}

	/**
	 * Gets an {@link ObservableList} of cached user names and roles.
	 *
	 * @return a cached list of users in the database
	 * @throws NoDatabaseLinkException
	 */
	public static ObservableList<Object> getObservableUsers() throws NoDatabaseLinkException
	{
		final String[] columns =
		{ "user_id", "first_name", "last_name", "role" };

		@SuppressWarnings("unchecked") final Set<User> result = (LinkedHashSet<User>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.USERS, null, columns, null, null));

		final Iterator<User> it = result.iterator();

		observableUsers.clear();
		while (it.hasNext())
			observableUsers.add(it.next());

		System.out.println("User list updated.");
		return observableUsers;
	}

	/**
	 * Searches the database for product boxes of the specified size.
	 *
	 * @param productData
	 *            a map of data to search for where the key is the product box
	 *            database ID and the value is the number of products
	 * @throws NoDatabaseLinkException
	 */
	public static List<ProductBoxSearchResultRow> searchProductBoxByDataList(final Map<Integer, Integer> productData) throws NoDatabaseLinkException
	{
		final List<ProductBoxSearchResultRow> foundProducts = FXCollections.observableArrayList();

		List<String> where = null;
		Integer wantedProductCount = null;
		List<ProductBox> boxes = null;

		// For every unique string representing a product.
		for (final Integer productID : productData.keySet())
		{
			boxes = new ArrayList<ProductBox>();
			where = new ArrayList<String>();
			wantedProductCount = productData.get(productID);

			System.out.println("Looking for [" + productID + "] of size " + wantedProductCount);
			where.add("product = " + productID);

			// First look for an exact amount.
			where.add("product_count = " + wantedProductCount);

			boxes = getProductBoxesByProductID(where);

			// Couldn't find a box with exactly the number of products wanted.
			if (boxes.isEmpty())
			{
				if (MainWindow.DEBUG_MODE)
					System.out.println("Unable to find a product box with the wanted size of " + wantedProductCount + ". Looking from multiple boxes.");

				// Remove the product count condition and find all product boxes with the wanted product ID.
				where.remove(1);
				boxes = getBoxesContainingAtLeastProducts(getProductBoxesByProductID(where), wantedProductCount);
			}
			else if (boxes.size() > 1)
			{
				// If found multiple boxes with the exact size, select one that will expire the soonest.
				final ProductBox oldest = getOldestProductBox(boxes);

				boxes.clear();
				boxes.add(oldest);
			}

			for (final ProductBox box : boxes)
				foundProducts.add(new ProductBoxSearchResultRow(box));
		}

		// Remove nulls.
		foundProducts.removeAll(Collections.singleton(null));

		System.out.println("Updating product box search results.");
		observableProductSearchResults.clear();
		observableProductSearchResults.addAll(foundProducts);

		// Return the data for unit testing.
		return foundProducts;
	}

	public static List<ProductBoxSearchResultRow> searchProductBox(final List<String> where) throws NoDatabaseLinkException
	{
		final List<ProductBoxSearchResultRow> foundProducts = FXCollections.observableArrayList();

		if (MainWindow.DEBUG_MODE)
			System.out.println("Searching for a product box where: " + where);

		final String[] columns =
		{ "*" };

		final Map<DatabaseTable, String> join = new LinkedHashMap<DatabaseTable, String>();
		join.put(DatabaseTable.PRODUCTS, "containers.product = products.product_id");

		@SuppressWarnings("unchecked") final Set<ProductBox> result = (LinkedHashSet<ProductBox>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.CONTAINERS, join, columns, null, where));

		System.out.println("\n\nSEARCH RESULT\n\n" + result);

		for (final ProductBox box : result)
		{
			if (!cachedProductBoxes.containsKey(box.getBoxID()))
			{
				// Store for reuse.
				if (MainWindow.PRINT_CACHE_MESSAGES)
					System.out.println("Caching: " + box);

				cachedProductBoxes.put(box.getBoxID(), box);
			}

			foundProducts.add(new ProductBoxSearchResultRow(box));
		}

		System.out.println("Updating product box search results.");
		observableProductSearchResults.clear();
		observableProductSearchResults.addAll(foundProducts);

		// Return the data for unit testing.
		return foundProducts;
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
		System.out.println("[DatabaseController] Placing product boxes on shelf " + shelfid + "...");

		final String[] columns =
		{ "*" };

		final List<String> where = new ArrayList<String>();
		where.add("shelf = " + shelfid);

		final Shelf shelf = getShelfByID(shelfid, true);

		@SuppressWarnings("unchecked") final Map<Integer, ArrayList<Integer[]>> shelfBoxMap = (HashMap<Integer, ArrayList<Integer[]>>) runQuery(DatabaseQueryType.SELECT, DatabaseTable.SHELF_PRODUCTBOXES, null, columns, null, where);

		// If the shelf is not empty.
		if (!shelfBoxMap.isEmpty())
		{
			ProductBox box = null;
			String shelfSlotID = null;

			final ArrayList<Integer[]> boxes = shelfBoxMap.get(shelfid);

			for (final Integer[] data : boxes)
			{
				box = getProductBoxByID(data[0]);

				// data[1] is the level index
				shelfSlotID = Shelf.coordinatesToShelfSlotID(shelfid, data[1] + 1, data[2], true);
				shelf.addToSlot(shelfSlotID, box, false);
			}

			System.out.println("[DatabaseController] Product boxes placed on shelf " + shelfid + ".");
		}
		else
		{
			System.out.println("[DatabaseController] Nothing to place.");
		}
	}

	/**
	 * Places the correct product boxes in the cached removal list.
	 *
	 * @param listid
	 *            removal list to place product boxes into
	 * @throws NoDatabaseLinkException
	 */
	private static void setContainersToRemovalList(final int listid) throws NoDatabaseLinkException
	{
		System.out.println("[DatabaseController] Placing product boxes on removal list " + listid + "...");

		final List<String> where = new ArrayList<String>();
		where.add("removallist = " + listid);

		final String[] columns =
		{ "productbox" };
		@SuppressWarnings("unchecked") final Set<Integer> removalListBoxes = (LinkedHashSet<Integer>) runQuery(DatabaseQueryType.SELECT, DatabaseTable.REMOVALLIST_PRODUCTBOXES, null, columns, null, where);

		if (!removalListBoxes.isEmpty())
		{
			for (final Integer id : removalListBoxes)
				cachedRemovalLists.get(listid).addProductBox(getProductBoxByID(id));

			System.out.println("[DatabaseController] Product boxes placed on removal list " + listid + ".");
		}
		else
			System.out.println("[DatabaseController] Nothing to place.");

	}

	/*
	 * -------------------------------- PUBLIC SETTER METHODS --------------------------------
	 */

	/**
	 * Initializes the database.
	 *
	 * @return <code>true</code> if database changed as a result of this call
	 * @throws NoDatabaseConnectionException
	 */
	public static boolean initializeDatabase() throws NoDatabaseLinkException
	{
		System.out.println("Initializing database...");

		final boolean changed = (0 != (Integer) runQuery("RUNSCRIPT FROM './data/init.sql';"));

		System.out.println("Database initialized.");
		return changed;
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
	 * @param badgeID
	 *            badge ID of the user
	 * @param pin
	 *            PIN of the user
	 * @param firstName
	 *            first name of the user
	 * @param lastName
	 *            last name of the user
	 * @param roleID
	 *            the ID of the role of the user
	 * @return <code>true</code> if user was added
	 * @throws NoDatabaseLinkException
	 *             when database link was lost
	 */
	public static boolean addUser(final String badgeID, final String pin, final String firstName, final String lastName, final int roleID) throws NoDatabaseLinkException
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

		@SuppressWarnings("unchecked") final boolean changed = (0 < ((Set<Integer>) runQuery(DatabaseQueryType.INSERT, DatabaseTable.USERS, null, null, values, null)).size());

		// Update the user list displayed in the UI after adding a new user.
		if (changed)
			getObservableUsers();

		return changed;
	}

	/**
	 * Removes a user with the specified database row ID. If database changed as
	 * a result of the call, updates the {@link ObservableList} of user data
	 * shown in the UI.
	 *
	 * @param databaseID
	 *            the database ID of the user to delete
	 * @return <code>true</code> if user was deleted
	 * @throws NoDatabaseLinkException
	 */
	public static boolean removeUser(final int databaseID) throws NoDatabaseLinkException
	{
		final List<String> where = new ArrayList<String>();
		where.add("user_id = " + new Integer(databaseID));

		@SuppressWarnings("unchecked") final boolean changed = (0 < ((Set<Integer>) (runQuery(DatabaseQueryType.DELETE, DatabaseTable.USERS, null, null, null, where))).size());

		// Update the user list displayed in the UI if database changed.
		if (changed)
			getObservableUsers();

		return changed;
	}

	/**
	 * Adds the given product box to the shelf slot it specifies.
	 *
	 * @param productBox
	 *            product box to update in the database
	 * @return <code>true</code> if the database was updated
	 */
	@SuppressWarnings("unchecked") public static boolean addProductBoxToShelfSlot(final ProductBox productBox, final String shelfSlotID) throws NoDatabaseLinkException
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
			values.put("productbox", productBox.getBoxID());
			changed = (0 < ((LinkedHashSet<Object>) runQuery(DatabaseQueryType.INSERT, DatabaseTable.SHELF_PRODUCTBOXES, null, null, values, null)).size());
		}
		else
		{
			// Otherwise UPDATE.

			final List<String> where = new ArrayList<String>();
			where.add("productbox = " + productBox.getBoxID());
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
	 * @param productBox
	 *            product box to remove from it's shelf slot
	 */
	public static boolean removeProductBoxFromShelfSlot(final ProductBox productBox) throws NoDatabaseLinkException
	{
		final List<String> where = new ArrayList<String>();
		where.add("productbox = " + productBox.getBoxID());

		@SuppressWarnings("unchecked") final boolean changed = (0 < ((LinkedHashSet<Object>) runQuery(DatabaseQueryType.DELETE, DatabaseTable.SHELF_PRODUCTBOXES, null, null, null, where)).size());

		final Object[] tokens = Shelf.tokenizeShelfSlotID(productBox.getShelfSlot());

		// Update the cache.
		if (changed)
			getShelfByID(Integer.parseInt(((String) tokens[0]).substring(1)), false);

		return changed;
	}

	/**
	 * Updates an existing removal list in the database with the data from the
	 * given removal list or creates a new one if it doesn't exist.
	 *
	 * @param removalList
	 *            new or existing removal list
	 * @return <code>true</code> if existing data was updated or a new removal
	 *         list was created in the database
	 */
	@SuppressWarnings("unchecked") public static boolean updateRemovalList(final RemovalList removalList) throws NoDatabaseLinkException
	{
		int listID = removalList.getDatabaseID();

		final Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("liststate", removalList.getState().getDatabaseID());

		final List<String> where = new ArrayList<String>();

		DatabaseQueryType query;

		// If the removal list does not exist yet, INSERT.
		if (listID < 1)
			query = DatabaseQueryType.INSERT;
		else
			query = DatabaseQueryType.UPDATE;

		// Insert/Update the list itself
		Set<Integer> result = (LinkedHashSet<Integer>) runQuery(query, DatabaseTable.REMOVALLISTS, null, null, values, null);

		if (result.size() == 0)
			return false;

		// Get the inserted removal list database ID.
		if (listID < 1)
			listID = result.iterator().next();

		// Even if multiple rows were changed, they all have the same ID.
		where.add("removallist = " + listID);

		/*
		 * Delete all boxes from the list in the database.
		 * We don't really care whether anything was deleted or not, if there was something, it is now deleted.
		 */
		runQuery(DatabaseQueryType.DELETE, DatabaseTable.REMOVALLIST_PRODUCTBOXES, null, null, null, where);

		// Add all boxes to the database.
		Iterator<Object> it = removalList.getObservableBoxes().iterator();
		values.clear();
		values.put("removallist", listID);
		values.put("productbox", -1);

		while (it.hasNext())
		{
			// Remove the previous box ID.
			values.remove("productbox");

			// Put in the new box ID.
			values.put("productbox", ((ProductContainer) it.next()).getBoxID());

			// Run the query.
			result = (LinkedHashSet<Integer>) runQuery(DatabaseQueryType.INSERT, DatabaseTable.REMOVALLIST_PRODUCTBOXES, null, null, values, null);

			if (result.size() == 0)
				return false;
		}

		// Update the cache.
		getRemovalListByID(listID, false);

		return true;
	}

	/*
	 * -------------------------------- CACHING --------------------------------
	 */

	/**
	 * Loads data from database into memory.
	 */
	public static void loadData(final boolean silent)
	{
		if (!silent)
			System.out.println("[DatabaseController] Loading data from database...");

		try
		{
			loadProductBoxes(silent);
			loadShelves(silent);
			loadRemovalLists(silent);
		} catch (final NoDatabaseLinkException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Loads the following objects into memory:
	 * <ul>
	 * <li>{@link ProductBrand}</li>
	 * <li>{@link ProductType}</li>
	 * <li>{@link ProductCategory}</li>
	 * <li>{@link Product}</li>
	 * <li>{@link ProductContainer}</li>
	 * </ul>
	 *
	 * @throws NoDatabaseLinkException
	 */
	private static void loadProductBoxes(final boolean silent) throws NoDatabaseLinkException
	{
		if (!silent)
			System.out.println("[DatabaseController] Loading product containers...");

		final String[] columns =
		{ "*" };
		@SuppressWarnings("unchecked") final Set<ProductBox> result = (Set<ProductBox>) runQuery(DatabaseQueryType.SELECT, DatabaseTable.CONTAINERS, null, columns, null, null);

		final Iterator<ProductBox> it = result.iterator();

		// Store for reuse.
		while (it.hasNext())
		{
			final ProductBox p = it.next();

			if (MainWindow.PRINT_CACHE_MESSAGES)
				System.out.println("Caching: " + p);

			cachedProductBoxes.put(p.getBoxID(), p);
		}

		if (!silent)
			System.out.println("[DatabaseController] Product containers loaded.");
	}

	/**
	 * Loads the following objects into memory:
	 * <ul>
	 * <li>{@link Shelf}</li>
	 * </ul>
	 *
	 * @throws NoDatabaseLinkException
	 */
	private static void loadShelves(final boolean silent) throws NoDatabaseLinkException
	{
		if (!silent)
			System.out.println("[DatabaseController] Loading shelves...");

		final String[] columns =
		{ "*" };
		@SuppressWarnings("unchecked") final Set<Shelf> result = (Set<Shelf>) runQuery(DatabaseQueryType.SELECT, DatabaseTable.SHELVES, null, columns, null, null);

		final Iterator<Shelf> it = result.iterator();
		// Store for reuse.
		while (it.hasNext())
		{
			final Shelf s = it.next();

			if (MainWindow.PRINT_CACHE_MESSAGES)
				System.out.println("Caching: " + s);

			cachedShelves.put(s.getDatabaseID(), s);
		}

		if (!silent)
			System.out.println("[DatabaseController] Shelves loaded.");

		setAllContainersToAllShelves(silent);
	}

	/**
	 * Places the loaded {@link ProductContainer} objects into {@link Shelf}
	 * objects.
	 *
	 * @throws NoDatabaseLinkException
	 */
	private static void setAllContainersToAllShelves(final boolean silent) throws NoDatabaseLinkException
	{
		if (!silent)
			System.out.println("[DatabaseController] Placing product boxes on shelves...");

		final String[] columns =
		{ "*" };
		@SuppressWarnings("unchecked") final Map<Integer, ArrayList<Integer[]>> shelfBoxMap = (HashMap<Integer, ArrayList<Integer[]>>) runQuery(DatabaseQueryType.SELECT, DatabaseTable.SHELF_PRODUCTBOXES, null, columns, null, null);

		for (final Integer shelfID : shelfBoxMap.keySet())
		{
			if (cachedShelves.containsKey(shelfID))
			{
				final ArrayList<Integer[]> boxes = shelfBoxMap.get(shelfID);

				for (final Integer[] data : boxes)
				{
					if (cachedProductBoxes.containsKey(data[0]))
					{
						// Do not update the database as this method loads the data from the database into objects.
						cachedShelves.get(shelfID).addToSlot(Shelf.coordinatesToShelfSlotID(shelfID, data[1] + 1, data[2], true), cachedProductBoxes.get(data[0]), false);
					}
				}
			}
		}

		if (!silent)
			System.out.println("[DatabaseController] Product boxes placed on shelves.");
	}

	/**
	 * Loads the following objects into memory:
	 * <ul>
	 * <li>{@link RemovalListState}</li>
	 * <li>{@link RemovalList}</li>
	 * </ul>
	 *
	 * @throws NoDatabaseLinkException
	 */
	private static void loadRemovalLists(final boolean silent) throws NoDatabaseLinkException
	{
		if (!silent)
			System.out.println("[DatabaseController] Loading removal lists...");

		final String[] columns =
		{ "*" };
		@SuppressWarnings("unchecked") final Set<RemovalList> result = (Set<RemovalList>) runQuery(DatabaseQueryType.SELECT, DatabaseTable.REMOVALLISTS, null, columns, null, null);

		final Iterator<RemovalList> it = result.iterator();

		// Store for reuse.
		while (it.hasNext())
		{
			final RemovalList r = it.next();

			if (MainWindow.PRINT_CACHE_MESSAGES)
				System.out.println("Caching: " + r);

			cachedRemovalLists.put(r.getDatabaseID(), r);
		}

		if (!silent)
			System.out.println("[DatabaseController] Removal lists loaded.");

		setAllContainersToAllRemovalLists(silent);
	}

	/**
	 * Places the loaded {@link ProductContainer} objects into
	 * {@link RemovalList} objects.
	 *
	 * @throws NoDatabaseLinkException
	 */
	private static void setAllContainersToAllRemovalLists(final boolean silent) throws NoDatabaseLinkException
	{
		if (!silent)
			System.out.println("[DatabaseController] Placing product boxes on all removal lists...");

		final String[] columns =
		{ "*" };
		@SuppressWarnings("unchecked") final Map<Integer, ArrayList<Integer>> removaListBoxes = (HashMap<Integer, ArrayList<Integer>>) runQuery(DatabaseQueryType.SELECT, DatabaseTable.REMOVALLIST_PRODUCTBOXES, null, columns, null, null);

		for (final Integer removalListID : removaListBoxes.keySet())
		{
			if (cachedRemovalLists.containsKey(removalListID))
			{
				final ArrayList<Integer> boxIDs = removaListBoxes.get(removalListID);

				for (final Integer id : boxIDs)
				{
					if (cachedProductBoxes.containsKey(id))
					{
						// Do not update the database as this method loads the data from the database into objects.
						cachedRemovalLists.get(removalListID).addProductBox(cachedProductBoxes.get(id));
					}
				}
			}
		}

		if (!silent)
			System.out.println("[DatabaseController] Product boxes placed on all removal lists.");
	}

	/**
	 * Loads all {@link ProductCategory} objects from the database into the
	 * cache.
	 *
	 * @return an {@link ObservableList} of all product categories
	 */
	public static ObservableList<Object> getAllProductCategories() throws NoDatabaseLinkException
	{
		if (MainWindow.PRINT_CACHE_MESSAGES)
			System.out.println("Caching all Product Categories.");

		final String[] columns =
		{ "*" };

		@SuppressWarnings("unchecked") final Set<ProductCategory> result = (LinkedHashSet<ProductCategory>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.CATEGORIES, null, columns, null, null));

		if (result.size() == 0)
			System.out.println("No Product Categories present in the database.");

		final Iterator<ProductCategory> it = result.iterator();

		// Store for reuse.
		while (it.hasNext())
		{
			final ProductCategory p = it.next();

			if (MainWindow.PRINT_CACHE_MESSAGES)
				System.out.println("Caching: " + p);

			cachedProductCategories.put(p.getDatabaseID(), p);
		}

		if (MainWindow.PRINT_CACHE_MESSAGES)
			System.out.println("All Product Categories cached.");

		observableProductCategories.clear();
		observableProductCategories.addAll(cachedProductCategories.values());

		return observableProductCategories;
	}

	/**
	 * Loads all {@link ProductBrand} objects from the database into the cache.
	 *
	 * @return an {@link ObservableList} of all product brands
	 */
	public static ObservableList<Object> getAllProductBrands() throws NoDatabaseLinkException
	{
		if (MainWindow.PRINT_CACHE_MESSAGES)
			System.out.println("Caching all Product Brands.");

		final String[] columns =
		{ "*" };

		@SuppressWarnings("unchecked") final Set<ProductBrand> result = (LinkedHashSet<ProductBrand>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.BRANDS, null, columns, null, null));

		if (result.size() == 0)
			System.out.println("No Product Brands present in the database.");

		final Iterator<ProductBrand> it = result.iterator();

		// Store for reuse.
		while (it.hasNext())
		{
			final ProductBrand p = it.next();

			if (MainWindow.PRINT_CACHE_MESSAGES)
				System.out.println("Caching: " + p);

			cachedProductBrands.put(p.getDatabaseID(), p);
		}

		if (MainWindow.PRINT_CACHE_MESSAGES)
			System.out.println("All Product Brands cached.");

		observableProductBrands.clear();
		observableProductBrands.addAll(cachedProductBrands.values());
		return observableProductBrands;
	}

	public static ObservableList<Object> getAllRemovalListStates()
	{
		observableRemovalListStates.clear();
		observableRemovalListStates.addAll(cachedRemovalListStates.values());
		return observableRemovalListStates;
	}

	/**
	 * Clears all cached data.
	 */
	public static void clearAllCaches()
	{
		System.out.println("[DatabaseController] Clearing all cached data.");
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
	 *         and the value is the object
	 */
	public static Map<Integer, RemovalList> getCachedRemovalLists()
	{
		return cachedRemovalLists;
	}
}
