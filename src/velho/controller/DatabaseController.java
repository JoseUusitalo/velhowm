package velho.controller;

import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.h2.jdbcx.JdbcConnectionPool;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	private static ObservableList<User> userViewList = FXCollections.observableArrayList();

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
	private static String sqlBuilder(final DatabaseQueryType type, final DatabaseTable tableName, final String[] columns,
			final Map<String, Object> columnValues, final Map<String, Object> where)
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
	 *
	 * @throws NoDatabaseLinkException
	 */
	private static Object runQuery(final DatabaseQueryType type, final DatabaseTable tableName, final String[] columns, final Map<String, Object> columnValues,
			final Map<String, Object> where) throws NoDatabaseLinkException
	{
		Connection connection = getConnection();
		Statement statement = null;

		// Update queries.
		int changed = 0;

		Set<Object> dataSet = new LinkedHashSet<Object>();

		try
		{
			// Initialize a statement.
			statement = connection.createStatement();

			statement.execute(sqlBuilder(type, tableName, columns, columnValues, where));

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

					switch (tableName)
					{
						case USERS:
							while (result.next())
								dataSet.add(new User(result.getInt("user_id"), result.getString("first_name"), result.getString("last_name"),
										getRoleFromID(result.getInt("role"))));
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
						case SHELVES:
							while (result.next())
								dataSet.add(new Shelf(result.getInt("shelf_id"), result.getInt("max_levels"), result.getInt("max_shelfslots_per_level"),
										result.getInt("max_productboxes_per_shelfslot")));
							break;
						default:
							throw new IllegalArgumentException();
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
					case SHELVES:
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
	 *
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
	 *
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
	 *
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
	 * Gets the database ID of the given user role name.
	 *
	 * @param roleName the name of the role
	 * @return the database ID of the given role (a value greater than 0) or <code>-1</code> if the role does not exist
	 * in the database
	 */
	public static int getRoleID(final String roleName) throws NoDatabaseLinkException
	{
		String[] columns = { "role_id" };
		Map<String, Object> where = new LinkedHashMap<String, Object>();
		where.put("name", roleName);

		@SuppressWarnings("unchecked")
		Set<Integer> result = (LinkedHashSet<Integer>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.ROLES, columns, null, where));

		if (result.size() == 0)
			return -1;

		// Only one result as IDs are unique.
		return result.iterator().next().intValue();
	}

	/**
	 * Gets a set of user role names in the database.
	 *
	 * @return a set of user role names
	 *
	 * @throws NoDatabaseLinkException
	 */
	public static Set<String> getUserRoleNames() throws NoDatabaseLinkException
	{
		String[] columns = { "name" };

		@SuppressWarnings("unchecked")
		Set<String> result = (LinkedHashSet<String>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.ROLES, columns, null, null));

		return result;
	}

	/**
	 * <p>Authenticates a user with the given badge ID string.</p>
	 * <p>Warnign: Assumes that the badge ID is techinically valid.</p>
	 *
	 * @param badgeID a badge id string
	 * @return a {@link User} object representing the authenticated user or <code>null</code> for invalid credentials
	 *
	 * @throws NoDatabaseLinkException
	 * @see {@link User#isValidBadgeID(String)}
	 */
	public static User authenticateBadgeID(final String badgeID) throws NoDatabaseLinkException
	{
		String[] columns = { "user_id", "first_name", "last_name", "role" };
		Map<String, Object> where = new LinkedHashMap<String, Object>();
		where.put("badge_id", badgeID);

		@SuppressWarnings("unchecked")
		Set<User> result = (LinkedHashSet<User>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.USERS, columns, null, where));

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
	 *
	 * @throws NoDatabaseLinkException
	 * @see {@link User#isValidPIN(String)}
	 */
	public static User authenticatePIN(final String firstName, final String lastName, final String pin) throws NoDatabaseLinkException
	{
		String[] columns = { "user_id", "first_name", "last_name", "role" };
		Map<String, Object> where = new LinkedHashMap<String, Object>();
		where.put("first_name", firstName);
		where.put("last_name", lastName);
		where.put("pin", pin);

		@SuppressWarnings("unchecked")
		Set<User> result = (LinkedHashSet<User>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.USERS, columns, null, where));

		if (result.size() == 0)
			return null;

		return result.iterator().next();
	}

	/**
	 * Gets the a {@link UserRole} object from the given role id.
	 *
	 * @param roleid role database ID
	 * @return the corresponding {@link UserRole} object
	 *
	 * @throws NoDatabaseLinkException
	 */
	private static UserRole getRoleFromID(final int roleid) throws NoDatabaseLinkException
	{
		String[] columns = { "name" };
		Map<String, Object> where = new LinkedHashMap<String, Object>();
		where.put("role_id", new Integer(roleid));

		@SuppressWarnings("unchecked")
		Set<String> result = (LinkedHashSet<String>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.ROLES, columns, null, where));

		if (result.size() == 0)
			return null;

		return UserController.stringToRole(result.iterator().next());
	}

	/**
	 * Gets a list product codes in the database.
	 *
	 * @return a list of integer product codes
	 */
	public static List<Integer> getProductCodeList()
	{
		// TODO Auto-generated method stub
		return new ArrayList<Integer>();
	}

	/**
	 * Gets an {@link ObservableList} of user names and roles.
	 *
	 * @return a list of users in the database
	 *
	 * @throws NoDatabaseLinkException
	 */
	public static ObservableList<User> getPublicUserDataList() throws NoDatabaseLinkException
	{
		String[] columns = { "user_id", "first_name", "last_name", "role" };

		@SuppressWarnings("unchecked")
		Set<User> result = (LinkedHashSet<User>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.USERS, columns, null, null));

		Iterator<User> it = result.iterator();

		userViewList.clear();
		while (it.hasNext())
			userViewList.add(it.next());

		System.out.println("User list updated.");
		return userViewList;
	}

	/**
	 * Gets user data by their database id.
	 *
	 * @param id database id of the user
	 * @return a {@link User} object or <code>null</code> if a user with that ID was not found
	 */
	public static User getUserByID(final int id) throws NoDatabaseLinkException
	{
		String[] columns = { "user_id", "first_name", "last_name", "role" };
		Map<String, Object> where = new LinkedHashMap<String, Object>();
		where.put("user_id", new Integer(id));

		@SuppressWarnings("unchecked")
		Set<User> result = (LinkedHashSet<User>) (runQuery(DatabaseQueryType.SELECT, DatabaseTable.USERS, columns, null, where));

		if (result.size() == 0)
			return null;

		// Only one result as IDs are unique.
		return result.iterator().next();
	}

	/*
	 * PUBLIC DATABASE SETTER METHODS
	 */

	/**
	 * Initializes the database.
	 *
	 * @return <code>true</code> if database changed as a result of this call
	 *
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
	 *
	 * @throws NoDatabaseLinkException when database link was lost
	 */
	public static boolean addUser(final String badgeID, final String pin, final String firstName, final String lastName, final int roleID)
			throws NoDatabaseLinkException
	{
		Map<String, Object> values = new LinkedHashMap<String, Object>();

		// If no pin is defined, add with badge id.
		if (pin == null || pin.isEmpty())
			values.put("badge_id", badgeID);
		else
			values.put("pin", pin);

		values.put("first_name", firstName);
		values.put("last_name", lastName);
		values.put("role", roleID);

		boolean changed = (0 != (Integer) runQuery(DatabaseQueryType.INSERT, DatabaseTable.USERS, null, values, null));

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
	 *
	 * @throws NoDatabaseLinkException
	 */
	public static boolean removeUser(final int databaseID) throws NoDatabaseLinkException
	{
		Map<String, Object> where = new LinkedHashMap<String, Object>();
		where.put("user_id", new Integer(databaseID));

		boolean changed = (0 != (Integer) (runQuery(DatabaseQueryType.DELETE, DatabaseTable.USERS, null, null, where)));

		// Update the user list displayed in the UI if database changed.
		if (changed)
			getPublicUserDataList();

		return changed;
	}

	/**
	 * Loads data from database.
	 */
	public static void loadData()
	{
		System.out.println("[DatabaseController] Loading data from database...");

		try
		{
			loadShelves();
		}
		catch (NoDatabaseLinkException e)
		{
			e.printStackTrace();
		}
	}

	private static void loadShelves() throws NoDatabaseLinkException
	{
		System.out.println("[DatabaseController] Loading shelves...");

		String[] columns = { "*" };
		@SuppressWarnings("unchecked")
		Set<Shelf> result = (Set<Shelf>) runQuery(DatabaseQueryType.SELECT, DatabaseTable.SHELVES, columns, null, null);

		Iterator<Shelf> it = result.iterator();

		while (it.hasNext())
			System.out.println(it.next().toString());

		System.out.println("[DatabaseController] Shelves loaded.");
	}
}
