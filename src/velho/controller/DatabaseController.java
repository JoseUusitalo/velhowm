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

import org.h2.jdbcx.JdbcConnectionPool;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import velho.model.Product;
import velho.model.ProductBox;
import velho.model.ProductBoxSearchResultRow;
import velho.model.ProductBrand;
import velho.model.ProductCategory;
import velho.model.ProductType;
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
	private final static String DB_URI = "jdbc:h2:./data/velho;";

	/**
	 * User name for the system itself.
	 */
	private final static String USERNAME = "VELHOWM";

	/**
	 * The path to the database file in the file system.
	 */
	private final static String DB_FILEPATH = "./data/velho.mv.db";

	/**
	 * A pool where database connections are acquired from.
	 */
	private static JdbcConnectionPool connectionPool;

	/**
	 * An observable list of users for display in the user interface.
	 */
	private static ObservableList<Object> userViewList = FXCollections.observableArrayList();

	/**
	 * An observable list of products for display in the user interface.
	 */
	private static ObservableList<Object> productViewList = FXCollections.observableArrayList();

	/**
	 * An observable list of product search results for display in the user interface.
	 */
	private static ObservableList<Object> productSearchResultViewList = FXCollections.observableArrayList();

	/**
	 * A map of {@link ProductBrand} objects loaded from the database.
	 */
	private static Map<Integer, ProductBrand> loadedProductBrands = new HashMap<Integer, ProductBrand>();

	/**
	 * A map of {@link ProductType} objects loaded from the database.
	 */
	private static Map<Integer, ProductType> loadedProductTypes = new HashMap<Integer, ProductType>();

	/**
	 * A map of {@link ProductCategory} objects loaded from the database.
	 */
	private static Map<Integer, ProductCategory> loadedProductCategories = new HashMap<Integer, ProductCategory>();

	/**
	 * A map of {@link Product} objects loaded from the database.
	 */
	private static Map<Integer, Product> loadedProducts = new HashMap<Integer, Product>();

	/**
	 * A map of {@link ProductContainer} objects loaded from the database.
	 */
	private static Map<Integer, ProductBox> loadedProductBoxes = new HashMap<Integer, ProductBox>();

	/**
	 * A map of {@link Shelf} objects loaded from the database.
	 */
	private static Map<Integer, Shelf> loadedShelves = new HashMap<Integer, Shelf>();

	/*
	 * PRIVATE DATABASE METHODS
	 */

	/**
	 * Creates an SQL query out of the given data.
	 *
	 * @param type query command
	 * @param tableName name of the table
	 * @param columns columns to select (can be <code>null</code>)
	 * @param where conditions (can be <code>null</code>)
	 * @return an SQL query string
	 */
	private static String sqlBuilder(final DatabaseQueryType type, final DatabaseTable tableName, final Map<DatabaseTable, String> joinOnCondition,
			final String[] columns, final Map<String, Object> columnValues, final Map<String, Object> where)
	{
		StringBuilder sb = new StringBuilder();

		// Command
		sb.append(type.toString());
		sb.append(" ");

		// Columns
		if (columns != null)
		{
			int count = columns.length;
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
			default:
				sb.append("FROM ");
				break;
		}

		// Table name.
		sb.append(tableName.toString().toLowerCase());

		// Join.
		if (joinOnCondition != null)
		{
			Iterator<DatabaseTable> it = joinOnCondition.keySet().iterator();
			DatabaseTable key = null;

			while (it.hasNext())
			{
				sb.append(" JOIN ");

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

			Iterator<String> it = columnValues.keySet().iterator();
			String key = null;

			while (it.hasNext())
			{
				key = it.next();

				sb.append(key);
				sb.append("=");

				Object value = columnValues.get(key);

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
		if (where != null)
		{
			sb.append(" WHERE ");

			Iterator<String> it = where.keySet().iterator();
			String key = null;

			while (it.hasNext())
			{
				key = it.next();

				sb.append(key);
				sb.append(" = ");

				Object value = where.get(key);

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
					sb.append(" AND ");
			}
		}

		sb.append(";");

		if (MainWindow.DEBUG_MODE)
			System.out.println("[SQLBUILDER] " + sb.toString());

		return sb.toString();
	}

	/**
	 * Runs a database query with the given data.
	 *
	 * @param type query command
	 * @param tableName name of the table
	 * @param columns columns to select (can be <code>null</code>)
	 * @param where conditions (can be <code>null</code>)
	 * @return <ul>
	 * <li>if type is {@link DatabaseQueryType#UPDATE} or {@link DatabaseQueryType#DELETE}:
	 * the number of rows that were changed as a result of the query as an {@link Integer}</li>
	 * <li>if type is {@link DatabaseQueryType#SELECT}: a Set containing the selected data</li>
	 * </ul>
	 * @throws NoDatabaseLinkException
	 */
	private static Object runQuery(final DatabaseQueryType type, final DatabaseTable tableName, final Map<DatabaseTable, String> joinOnValues,
			final String[] columns, final Map<String, Object> columnValues, final Map<String, Object> where) throws NoDatabaseLinkException
	{
		Connection connection = getConnection();
		Statement statement = null;

		// Update queries.
		int changed = 0;

		// Most other queries.
		Set<Object> dataSet = new LinkedHashSet<Object>();

		// Putting boxes on shelves.
		Map<Integer, ArrayList<Integer[]>> shelfBoxMap = null;

		try
		{
			// Initialize a statement.
			statement = connection.createStatement();

			statement.execute(sqlBuilder(type, tableName, joinOnValues, columns, columnValues, where));

			switch (type)
			{
				case INSERT:
				case DELETE:
				case UPDATE:
					changed = statement.getUpdateCount();
					break;
				case SELECT:
					ResultSet result = null;
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

							case CATEGORIES:
								while (result.next())
									dataSet.add(new ProductCategory(result.getInt("category_id"), result.getString("name"),
											getProductTypeByID(result.getInt("type"))));
								break;

							case PRODUCTS:
								// @formatter:off
								while (result.next())
									dataSet.add(new Product(
											result.getInt("product_id"),
											result.getString("name"),
											result.getDate("expiration_date"),
											getProductBrandByID(result.getInt("brand")),
											getProductCategoryByID(result.getInt("category")),
											result.getInt("popularity")));
								break;
								// @formatter:on

							case CONTAINERS:
								// @formatter:off
								while (result.next())
									dataSet.add(new ProductBox(
											result.getInt("container_id"),
											result.getInt("max_size"),
											getProductByID(result.getInt("product")),
											result.getInt("product_count")));
								break;
								// @formatter:on

							case SHELVES:
								// @formatter:off
								while (result.next())
									dataSet.add(new Shelf(result.getInt("shelf_id"), result.getInt("max_levels"), result.getInt("max_shelfslots_per_level"),
											result.getInt("max_productboxes_per_shelfslot")));
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
							default:
								throw new IllegalArgumentException();
						}
					}
					break;
				default:
					throw new IllegalArgumentException();
			}
		}
		catch (IllegalStateException e)
		{
			// Close all resources.

			try
			{
				if (statement != null)
					statement.close();
			}
			catch (SQLException e1)
			{
				e.printStackTrace();
			}

			try
			{
				connection.close();
			}
			catch (SQLException e2)
			{
				e.printStackTrace();
			}

			// Connection pool has been disposed = no database connection.
			throw new NoDatabaseLinkException();
		}
		catch (SQLException e)
		{
			if (!e.toString().contains("Unique index or primary key violation"))
				e.printStackTrace();

			// If it was a UNIQUE constraint violation, continue normally as those are handled separately.
			System.out.println("[DatabaseController] Silently ignored an SQL UNIQUE constraint violation.");
		}

		// Close all resources.
		try
		{
			if (statement != null)
				statement.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		try
		{
			connection.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		switch (type)
		{
			case INSERT:
			case DELETE:
			case UPDATE:
				return changed;
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
					case PRODUCTBOX_PRODUCTS:
					case SHELVES:
						return dataSet;
					case SHELF_PRODUCTBOXES:
						return shelfBoxMap;
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
	private static Object runQuery(final String sql) throws NoDatabaseLinkException
	{
		Connection connection = getConnection();
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
		catch (IllegalStateException e)
		{
			// Close all resources.

			try
			{
				if (statement != null)
					statement.close();
			}
			catch (SQLException e1)
			{
				e.printStackTrace();
			}

			try
			{
				connection.close();
			}
			catch (SQLException e2)
			{
				e.printStackTrace();
			}

			// Connection pool has been disposed = no database connection.
			throw new NoDatabaseLinkException();
		}
		catch (SQLException e)
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
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		try
		{
			connection.close();
		}
		catch (SQLException e)
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
		}
		catch (NoDatabaseLinkException e)
		{
			// Do nothing. This is expected.
		}

		try
		{
			link();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (ExistingDatabaseLinkException e)
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
	 * Checks for a database link and gets a new connection to the database for running statements.
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
		catch (SQLException e)
		{
			if (!e.getMessage().contains("Database may be already in use"))
			{
				relink();
			}
			else
			{
				e.printStackTrace();
			}
		}
		return connection;
	}

	/*
	 * PUBLIC DATABASE METHODS
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
	 * Checks if a database link exists and throws a {@link NoDatabaseConnectionException} exception if it
	 * doesn't.
	 * To be used when a database link must exist.
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
		}
		catch (NoDatabaseLinkException e)
		{
			// Do nothing. This is expected.
		}

		try
		{
			link();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (ExistingDatabaseLinkException e)
		{
			e.printStackTrace();
		}

		PopupController.warning("Database connection was temporarily lost. Please try again or restart the application.");
	}

	/**
	 * Creates the link to the database.
	 * Use {@link #unlink()} to close the connection.
	 *
	 * @return <code>true</code> if the link was created successfully
	 * @throws ClassNotFoundException when the H2 driver was unable to load
	 * @throws ExistingDatabaseLinkException when a database link already exists
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

		// Create a connection pool and the database if it does not exist.
		connectionPool = JdbcConnectionPool.create(uri, USERNAME, "@_Vry $ECURE pword2");

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
	 * Shuts down the connection to the database.
	 * Use {@link #link()} to connect to the database again.
	 * @throws NoDatabaseLinkException when attempting unlink a database when no database link exists
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
	 * PUBLIC DATABASE GETTER METHODS
	 */

	/**
	 * Gets a map of columns and column names for displaying {@link #getPublicUserDataList()} data in a table.
	 *
	 * @return a map where the key is the column value and value is the column name
	 */
	public static Map<String, String> getPublicUserDataColumns(final boolean withDeleteColumn)
	{
		LinkedHashMap<String, String> cols = new LinkedHashMap<String, String>();
		cols.put("firstName", "First Name");
		cols.put("lastName", "Last Name");
		cols.put("roleName", "Role");

		if (withDeleteColumn)
			cols.put("deleteButton", "Delete");

		return cols;
	}

	/**
	 * Gets a map of columns and column names for displaying {@link #getPublicProductDataList()()} data in a table.
	 *
	 * @return a map where the key is the column value and value is the column name
	 */
	public static Map<String, String> getPublicProductDataColumns()
	{
		LinkedHashMap<String, String> cols = new LinkedHashMap<String, String>();
		cols.put("name", "Name");
		cols.put("expirationDate", "Expires");
		cols.put("brand", "Brand");
		cols.put("category", "Category");
		cols.put("popularity", "Popularity");

		return cols;
	}

	public static Map<String, String> getProductSearchDataColumns()
	{
		LinkedHashMap<String, String> cols = new LinkedHashMap<String, String>();
		cols.put("productID", "ID");
		cols.put("productName", "Name");
		cols.put("productBrand", "Brand");
		cols.put("productCategory", "Category");
		cols.put("boxShelfSlot", "Shelf Slot");
		cols.put("boxProductCount", "Amount");

		return cols;
	}

	/**
	 * Gets the database ID of the given user role name.
	 *
	 * @param roleName the name of the role
	 * @return the database ID of the given role (a value greater than 0) or <code>-1</code> if the role does not exist
	 * in the database
	 */
	public static int getRoleID(final String roleName) throws NoDatabaseLinkException
	{
		final String[] columns = { "role_id" };
		Map<String, Object> where = new LinkedHashMap<String, Object>();
		where.put("name", roleName);

		@SuppressWarnings("unchecked")
		Set<Integer> result = (LinkedHashSet<Integer>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.ROLES, null, columns, null, where));

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
		final String[] columns = { "name" };

		@SuppressWarnings("unchecked")
		Set<String> result = (LinkedHashSet<String>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.ROLES, null, columns, null, null));

		return result;
	}

	/**
	 * <p>Authenticates a user with the given badge ID string.</p>
	 * <p>Warnign: Assumes that the badge ID is techinically valid.</p>
	 *
	 * @param badgeID a badge ID string
	 * @return a {@link User} object representing the authenticated user or <code>null</code> for invalid credentials
	 * @throws NoDatabaseLinkException
	 * @see {@link User#isValidBadgeID(String)}
	 */
	public static User authenticateBadgeID(final String badgeID) throws NoDatabaseLinkException
	{
		final String[] columns = { "user_id", "first_name", "last_name", "role" };
		Map<String, Object> where = new LinkedHashMap<String, Object>();
		where.put("badge_id", badgeID);

		@SuppressWarnings("unchecked")
		Set<User> result = (LinkedHashSet<User>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.USERS, null, columns, null, where));

		if (result.size() == 0)
			return null;

		return result.iterator().next();
	}

	/**
	 * <p>Authenticates a user with the given PIN string.</p>
	 * <p>Warnign: Assumes that the PIN is techinically valid.</p>
	 *
	 * @param pin a PIN string
	 * @return a {@link User} object representing the authenticated user or <code>null</code> for invalid credentials
	 * @throws NoDatabaseLinkException
	 * @see {@link User#isValidPIN(String)}
	 */
	public static User authenticatePIN(final String firstName, final String lastName, final String pin) throws NoDatabaseLinkException
	{
		final String[] columns = { "user_id", "first_name", "last_name", "role" };
		Map<String, Object> where = new LinkedHashMap<String, Object>();
		where.put("first_name", firstName);
		where.put("last_name", lastName);
		where.put("pin", pin);

		@SuppressWarnings("unchecked")
		Set<User> result = (LinkedHashSet<User>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.USERS, null, columns, null, where));

		if (result.size() == 0)
			return null;

		return result.iterator().next();
	}

	/**
	 * Gets the {@link UserRole} object from the given role ID.
	 *
	 * @param roleid role database ID
	 * @return the corresponding user role object
	 * @throws NoDatabaseLinkException
	 */
	private static UserRole getRoleByID(final int roleid) throws NoDatabaseLinkException
	{
		final String[] columns = { "name" };
		Map<String, Object> where = new LinkedHashMap<String, Object>();
		where.put("role_id", new Integer(roleid));

		@SuppressWarnings("unchecked")
		Set<String> result = (LinkedHashSet<String>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.ROLES, null, columns, null, where));

		if (result.size() == 0)
			return null;

		return UserController.stringToRole(result.iterator().next());
	}

	/**
	 * Gets the {@link ProductType} object from the given type ID.
	 *
	 * @param typeid product type database ID
	 * @return the corresponding product type object
	 * @throws NoDatabaseLinkException
	 */
	private static ProductType getProductTypeByID(final int typeid) throws NoDatabaseLinkException
	{
		if (!loadedProductTypes.containsKey(typeid))
		{
			final String[] columns = { "name" };
			Map<String, Object> where = new LinkedHashMap<String, Object>();
			where.put("type_id", new Integer(typeid));

			@SuppressWarnings("unchecked")
			Set<String> result = (LinkedHashSet<String>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.TYPES, null, columns, null, where));

			if (result.size() == 0)
				return null;

			final ProductType p = new ProductType(typeid, result.iterator().next());

			// Store for reuse.
			System.out.println("Caching: " + p);
			loadedProductTypes.put(p.getDatabaseID(), p);
			return p;
		}

		System.out.println("Loading category " + typeid + " from cache.");
		return loadedProductTypes.get(typeid);
	}

	/**
	 * Gets the {@link ProductCategory} object from the given category ID.
	 *
	 * @param categoryid product category database ID
	 * @return the corresponding product category object
	 * @throws NoDatabaseLinkException
	 */
	private static ProductCategory getProductCategoryByID(final int categoryid) throws NoDatabaseLinkException
	{
		if (!loadedProductCategories.containsKey(categoryid))
		{
			final String[] columns = { "category_id", "name", "type" };
			Map<String, Object> where = new LinkedHashMap<String, Object>();
			where.put("category_id", new Integer(categoryid));

			@SuppressWarnings("unchecked")
			Set<Object> result = (LinkedHashSet<Object>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.CATEGORIES, null, columns, null, where));

			if (result.size() == 0)
				return null;

			final ProductCategory p = (ProductCategory) result.iterator().next();

			// Store for reuse.
			System.out.println("Caching: " + p);
			loadedProductCategories.put(p.getDatabaseID(), p);
			return p;
		}

		System.out.println("Loading category " + categoryid + " from cache.");
		return loadedProductCategories.get(categoryid);
	}

	/**
	 * Gets the {@link ProductBrand} object from the given brand ID.
	 *
	 * @param brandid product brand database ID
	 * @return the corresponding product brand object
	 * @throws NoDatabaseLinkException
	 */
	private static ProductBrand getProductBrandByID(final int brandid) throws NoDatabaseLinkException
	{
		if (!loadedProductBrands.containsKey(brandid))
		{
			final String[] columns = { "name" };
			Map<String, Object> where = new LinkedHashMap<String, Object>();
			where.put("brand_id", new Integer(brandid));

			@SuppressWarnings("unchecked")
			Set<String> result = (LinkedHashSet<String>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.BRANDS, null, columns, null, where));

			if (result.size() == 0)
				return null;

			final ProductBrand p = new ProductBrand(brandid, result.iterator().next());

			// Store for reuse.
			System.out.println("Caching: " + p);
			loadedProductBrands.put(p.getDatabaseID(), p);
			return p;
		}

		System.out.println("Loading brand " + brandid + " from cache.");
		return loadedProductBrands.get(brandid);
	}

	/**
	 * Gets the {@link Product} object from the given product ID.
	 *
	 * @param productid product database ID
	 * @return the corresponding product object
	 * @throws NoDatabaseLinkException
	 */
	private static Product getProductByID(final int productid) throws NoDatabaseLinkException
	{
		if (!loadedProducts.containsKey(productid))
		{
			final String[] columns = { "*" };
			Map<String, Object> where = new LinkedHashMap<String, Object>();
			where.put("product_id", new Integer(productid));

			@SuppressWarnings("unchecked")
			Set<Object> result = (LinkedHashSet<Object>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.PRODUCTS, null, columns, null, where));

			if (result.size() == 0)
				return null;

			final Product p = (Product) result.iterator().next();

			// Store for reuse.
			System.out.println("Caching: " + p);
			loadedProducts.put(p.getProductID(), p);
			return p;
		}

		System.out.println("Loading product " + productid + " from cache.");
		return loadedProducts.get(productid);
	}

	/**
	 * Gets the {@link Product} object from the given product ID.
	 *
	 * @param productid product database ID
	 * @return the corresponding product object
	 * @throws NoDatabaseLinkException
	 */
	public static ProductBox getProductBoxByID(final int productboxid) throws NoDatabaseLinkException
	{
		if (!loadedProductBoxes.containsKey(productboxid))
		{
			final String[] columns = { "*" };
			Map<String, Object> where = new LinkedHashMap<String, Object>();
			where.put("container_id", new Integer(productboxid));

			@SuppressWarnings("unchecked")
			Set<ProductBox> result = (LinkedHashSet<ProductBox>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.CONTAINERS, null, columns, null, where));

			if (result.size() == 0)
				return null;

			final ProductBox p = result.iterator().next();

			// Store for reuse.
			System.out.println("Caching: " + p);
			loadedProductBoxes.put(p.getBoxID(), p);
			return p;
		}

		System.out.println("Loading product box " + productboxid + " from cache.");
		return loadedProductBoxes.get(productboxid);
	}

	/**
	 * Gets the {@link Product} object from the given product name.
	 *
	 * @param name the exact product name
	 * @return the corresponding product object
	 * @throws NoDatabaseLinkException
	 */
	private static List<ProductBox> getProductBoxesByProductName(final Map<String, Object> where) throws NoDatabaseLinkException
	{
		final String[] columns = { "productbox_products.productbox" };

		Map<DatabaseTable, String> join = new LinkedHashMap<DatabaseTable, String>();
		join.put(DatabaseTable.CONTAINERS, "productbox_products.productbox = containers.container_id");
		join.put(DatabaseTable.PRODUCTS, "productbox_products.product = products.product_id");

		@SuppressWarnings("unchecked")
		Set<Integer> result = (LinkedHashSet<Integer>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.PRODUCTBOX_PRODUCTS, join, columns, null, where));

		/*
		@formatter:off
		Set<Integer> result = (LinkedHashSet<Integer>) (runQuery(
				  "SELECT productbox_products.productbox"
				+ " FROM productbox_products"
				+ " JOIN containers"
				+ " ON productbox_products.productbox = containers.container_id"
				+ " JOIN products"
				+ " ON productbox_products.product = products.product_id"
				+ " WHERE products.name = '" + productName + "'"
				+ " AND containers.product_count = " + productCount + ";"));
		@formatter:on
		*/
		List<ProductBox> boxes = new ArrayList<ProductBox>();
		Iterator<Integer> it = result.iterator();

		while (it.hasNext())
			boxes.add(getProductBoxByID(it.next()));

		return boxes;
	}

	/**
	 * Gets the {@link Product} object from the given product name.
	 *
	 * @param name the exact product name
	 * @return the corresponding product object
	 * @throws NoDatabaseLinkException
	 */
	private static List<ProductBox> getProductBoxesByProductID(final Map<String, Object> where) throws NoDatabaseLinkException
	{
		final String[] columns = { "container_id" };

		@SuppressWarnings("unchecked")
		Set<Integer> result = (LinkedHashSet<Integer>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.CONTAINERS, null, columns, null, where));

		List<ProductBox> boxes = new ArrayList<ProductBox>();
		Iterator<Integer> it = result.iterator();

		while (it.hasNext())
			boxes.add(getProductBoxByID(it.next()));

		return boxes;
	}

	/**
	 * Gets user data by their database ID.
	 *
	 * @param id database ID of the user
	 * @return a {@link User} object or <code>null</code> if a user with that ID was not found
	 */
	public static User getUserByID(final int id) throws NoDatabaseLinkException
	{
		final String[] columns = { "user_id", "first_name", "last_name", "role" };
		Map<String, Object> where = new LinkedHashMap<String, Object>();
		where.put("user_id", new Integer(id));

		@SuppressWarnings("unchecked")
		Set<User> result = (LinkedHashSet<User>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.USERS, null, columns, null, where));

		if (result.size() == 0)
			return null;

		// Only one result as IDs are unique.
		return result.iterator().next();
	}

	/**
	 * Gets the {@link Shelf} object from the given shelf ID.
	 *
	 * @param shelfid shelf database ID
	 * @return the corresponding shelf object
	 * @throws NoDatabaseLinkException
	 */
	public static Shelf getShelfByID(final int shelfid) throws NoDatabaseLinkException
	{
		if (!loadedShelves.containsKey(shelfid))
		{
			final String[] columns = { "*" };
			Map<String, Object> where = new LinkedHashMap<String, Object>();
			where.put("shelf_id", new Integer(shelfid));

			@SuppressWarnings("unchecked")
			Set<Object> result = (LinkedHashSet<Object>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.SHELVES, null, columns, null, where));

			if (result.size() == 0)
				return null;

			final Shelf s = (Shelf) result.iterator().next();

			// Store for reuse.
			System.out.println("Caching: " + s);
			loadedShelves.put(s.getDatabaseID(), s);
			return s;
		}

		System.out.println("Loading shelf " + shelfid + " from cache.");
		return loadedShelves.get(shelfid);
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
		Set<Integer> result = (LinkedHashSet<Integer>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.PRODUCTS, null, columns, null, null));
		List<Integer> ints = new ArrayList<Integer>();
		ints.addAll(result);
		return ints;
	}

	/**
	 * Gets an {@link ObservableList} of product data in shelves.
	 *
	 * @return a list of products in the database currently on shelves
	 */
	public static ObservableList<Object> getPublicProductDataList()
	{
		productViewList.clear();
		productViewList.addAll(loadedProducts.values());
		return productViewList;
	}

	/**
	 * Gets an {@link ObservableList} of product search results.
	 *
	 * @return a list of products searched by the user
	 */
	public static ObservableList<Object> getProductSearchResultViewList()
	{
		return productSearchResultViewList;
	}

	/**
	 * Gets an {@link ObservableList} of user names and roles.
	 *
	 * @return a list of users in the database
	 * @throws NoDatabaseLinkException
	 */
	public static ObservableList<Object> getPublicUserDataList() throws NoDatabaseLinkException
	{
		final String[] columns = { "user_id", "first_name", "last_name", "role" };

		@SuppressWarnings("unchecked")
		Set<User> result = (LinkedHashSet<User>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.USERS, null, columns, null, null));

		Iterator<User> it = result.iterator();

		userViewList.clear();
		while (it.hasNext())
			userViewList.add(it.next());

		System.out.println("User list updated.");
		return userViewList;
	}

	public static void searchProduct_BoxShelfSlots(final Map<String, Integer> productData) throws NoDatabaseLinkException
	{
		List<ProductBoxSearchResultRow> foundProducts = FXCollections.observableArrayList();
		Map<String, Object> where = null;
		List<ProductBox> boxes = null;

		// For every unique string representing a product.
		for (final String productString : productData.keySet())
		{
			boxes = new ArrayList<ProductBox>();
			where = new LinkedHashMap<String, Object>();

			// Determine if the string representing the product is a product ID.
			try
			{
				where.put("product", Integer.parseInt(productString));
				where.put("product_count", productData.get(productString));

				boxes = getProductBoxesByProductID(where);
			}
			catch (NumberFormatException e)
			{
				// Else search by name.
				where.put("products.name", productString);
				where.put("containers.product_count", productData.get(productString));

				boxes = getProductBoxesByProductName(where);
			}

			for (final ProductBox box : boxes)
				foundProducts.add(new ProductBoxSearchResultRow(box));
		}

		// Remove nulls.
		foundProducts.removeAll(Collections.singleton(null));

		/*
		 * List<ProductBoxSearchResultRow> rows = new ArrayList<ProductBoxSearchResultRow>();
		 *
		 * for (final ProductBox box : loadedProductBoxes.values())
		 * {
		 * rows.add(new ProductBoxSearchResultRow(box));
		 * }
		 */

		System.out.println("Updating product box search results.");
		productSearchResultViewList.clear();
		productSearchResultViewList.addAll(foundProducts);
	}

	/*
	 * PUBLIC DATABASE SETTER METHODS
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

		boolean changed = (0 != (Integer) runQuery("RUNSCRIPT FROM './data/init.sql';"));

		System.out.println("Database initialized.");
		return changed;
	}

	/**
	 * <p>Adds a new user to the database.
	 * If database changed as a result of the call, updates the {@link ObservableList} of user data shown in the UI.</p>
	 * <p>Warning: Assumes that given data is valid.</p>
	 *
	 * @param badgeID badge ID of the user
	 * @param pin PIN of the user
	 * @param firstName first name of the user
	 * @param lastName last name of the user
	 * @param roleID the ID of the role of the user
	 * @return <code>true</code> if user was added
	 * @throws NoDatabaseLinkException when database link was lost
	 */
	public static boolean addUser(final String badgeID, final String pin, final String firstName, final String lastName, final int roleID)
			throws NoDatabaseLinkException
	{
		Map<String, Object> values = new LinkedHashMap<String, Object>();

		// If no pin is defined, add with badge ID.
		if (pin == null || pin.isEmpty())
			values.put("badge_id", badgeID);
		else
			values.put("pin", pin);

		values.put("first_name", firstName);
		values.put("last_name", lastName);
		values.put("role", roleID);

		boolean changed = (0 != (Integer) runQuery(DatabaseQueryType.INSERT, DatabaseTable.USERS, null, null, values, null));

		// Update the user list displayed in the UI after adding a new user.
		if (changed)
			getPublicUserDataList();

		return changed;
	}

	/**
	 * Removes a user with the specified database row ID.
	 * If database changed as a result of the call, updates the {@link ObservableList} of user data shown in the UI.
	 *
	 * @param databaseID the database ID of the user to delete
	 * @return <code>true</code> if user was deleted
	 * @throws NoDatabaseLinkException
	 */
	public static boolean removeUser(final int databaseID) throws NoDatabaseLinkException
	{
		Map<String, Object> where = new LinkedHashMap<String, Object>();
		where.put("user_id", new Integer(databaseID));

		boolean changed = (0 != (Integer) (runQuery(DatabaseQueryType.DELETE, DatabaseTable.USERS, null, null, null, where)));

		// Update the user list displayed in the UI if database changed.
		if (changed)
			getPublicUserDataList();

		return changed;
	}

	/**
	 * Loads data from database into memory.
	 */
	public static void loadData()
	{
		System.out.println("[DatabaseController] Loading data from database...");

		try
		{
			loadProductBoxes();
			loadShelves();
		}
		catch (NoDatabaseLinkException e)
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
	 * @throws NoDatabaseLinkException
	 */
	private static void loadProductBoxes() throws NoDatabaseLinkException
	{
		System.out.println("[DatabaseController] Loading product containers...");

		final String[] columns = { "*" };
		@SuppressWarnings("unchecked")
		Set<ProductBox> result = (Set<ProductBox>) runQuery(DatabaseQueryType.SELECT, DatabaseTable.CONTAINERS, null, columns, null, null);

		Iterator<ProductBox> it = result.iterator();

		// Store for reuse.
		while (it.hasNext())
		{
			final ProductBox p = it.next();
			System.out.println("Caching: " + p);
			loadedProductBoxes.put(p.getBoxID(), p);
		}

		System.out.println("[DatabaseController] Product containers loaded.");
	}

	/**
	 * Loads the following objects into memory:
	 * <ul>
	 * <li>{@link Shelf}</li>
	 * </ul>
	 * @throws NoDatabaseLinkException
	 */
	private static void loadShelves() throws NoDatabaseLinkException
	{
		System.out.println("[DatabaseController] Loading shelves...");

		final String[] columns = { "*" };
		@SuppressWarnings("unchecked")
		Set<Shelf> result = (Set<Shelf>) runQuery(DatabaseQueryType.SELECT, DatabaseTable.SHELVES, null, columns, null, null);

		Iterator<Shelf> it = result.iterator();
		// Store for reuse.
		while (it.hasNext())
		{
			final Shelf s = it.next();
			System.out.println("Caching: " + s);
			loadedShelves.put(s.getDatabaseID(), s);
		}

		System.out.println("[DatabaseController] Shelves loaded.");

		setContainersToShelves();
	}

	/**
	 * Places the loaded {@link ProductContainer} objects into {@link Shelf} objects.
	 *
	 * @throws NoDatabaseLinkException
	 */
	private static void setContainersToShelves() throws NoDatabaseLinkException
	{
		System.out.println("[DatabaseController] Placing product boxes on shelves...");

		final String[] columns = { "*" };
		@SuppressWarnings("unchecked")
		Map<Integer, ArrayList<Integer[]>> shelfBoxMap = (HashMap<Integer, ArrayList<Integer[]>>) runQuery(DatabaseQueryType.SELECT,
				DatabaseTable.SHELF_PRODUCTBOXES, null, columns, null, null);

		for (final Integer shelfID : shelfBoxMap.keySet())
		{
			if (loadedShelves.containsKey(shelfID))
			{
				ArrayList<Integer[]> boxes = shelfBoxMap.get(shelfID);

				for (final Integer[] data : boxes)
				{
					if (loadedProductBoxes.containsKey(data[0]))
					{
						loadedShelves.get(shelfID).addToSlot(Shelf.coordinatesToShelfSlotID(shelfID, data[1], data[2]), loadedProductBoxes.get(data[0]));
					}
				}
			}
		}

		System.out.println("[DatabaseController] Product boxes placed on shelves.");
	}

	public static String getRandomShelfSlot()
	{
		return "S1-1-0";
	}
}
