package velho.controller;

import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import org.h2.jdbcx.JdbcConnectionPool;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import velho.model.User;
import velho.model.enums.DatabaseTable;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.model.interfaces.UserRole;

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
			e.printStackTrace();
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
	public static void connectAndInitialize() throws ClassNotFoundException, ExistingDatabaseLinkException, NoDatabaseLinkException
	{
		DatabaseController.link();
		DatabaseController.initializeDatabase();
	}

	/*
	 * PUBLIC DATABASE GETTER METHODS
	 */

	/**
	 * Gets the database ID of the given user role name.
	 *
	 * @param roleName the name of the role
	 * @return the database ID of the given role (a value greater than 0) or <code>-1</code> if the role does not exist
	 * in the database
	 */
	public static int getRoleID(final String roleName) throws NoDatabaseLinkException
	{
		Connection connection = getConnection();
		Statement statement = null;
		int id = -1;

		try
		{
			// Initialize a statement.
			statement = connection.createStatement();

			statement.execute("SELECT role_id FROM " + DatabaseTable.ROLES + " WHERE name = '" + roleName + "';");

			ResultSet result = statement.getResultSet();

			// Will only return one row because the name value is UNIQUE.
			if (result.next())
				id = result.getRow();

			// Close all resources.
			statement.close();
			connection.close();

			// Found something.
			if (id != 0)
				return id;

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (IllegalStateException e)
		{
			try
			{
				connection.close();
			}
			catch (SQLException e1)
			{
				e1.printStackTrace();
			}

			// Connection pool has been disposed = no database connection.
			throw new NoDatabaseLinkException();
		}

		try
		{
			connection.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		// Didn't find anything.
		return -1;
	}

	/**
	 * Gets a set of user role names in the database.
	 *
	 * @return a set of user role names
	 * @throws NoDatabaseLinkException
	 */
	public static Set<String> getUserRoleNames() throws NoDatabaseLinkException
	{
		Connection connection = getConnection();
		Statement statement = null;
		LinkedHashSet<String> names = new LinkedHashSet<String>();

		try
		{
			// Initialize a statement.
			statement = connection.createStatement();

			statement.execute("SELECT name FROM " + DatabaseTable.ROLES);

			ResultSet result = statement.getResultSet();

			while (result.next())
			{
				names.add(result.getString("name"));
			}

			// Close all resources.
			statement.close();
			connection.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (IllegalStateException e)
		{
			try
			{
				connection.close();
			}
			catch (SQLException e1)
			{
				e1.printStackTrace();
			}

			// Connection pool has been disposed = no database connection.
			throw new NoDatabaseLinkException();
		}

		try
		{
			connection.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return names;
	}

	/**
	 * Authenticates a user with the given authentication string.
	 *
	 * @param authenticationString a PIN or a badge id
	 * @return a {@link User} object representing the authenticated user or null for invalid credentials
	 * @throws NoDatabaseLinkException
	 */
	public static User authenticate(final String authenticationString) throws NoDatabaseLinkException
	{
		Connection connection = getConnection();
		Statement statement = null;
		User loggedInUser = null;

		try
		{
			// Initialize a statement.
			statement = connection.createStatement();

			int authInt = Integer.parseInt(authenticationString);

			if (User.isValidPIN(authInt))
				statement.execute("SELECT * FROM " + DatabaseTable.USERS + " WHERE pin = " + authInt + ";");
			else
				statement.execute("SELECT * FROM " + DatabaseTable.USERS + " WHERE badge_id = " + authInt + ";");

			ResultSet result = statement.getResultSet();

			if (result.next())
				loggedInUser = new User(result.getInt("user_id"), result.getString("first_name"), result.getString("last_name"),
						getRoleFromID(result.getInt("role")));

			// Close all resources.
			statement.close();
			connection.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (IllegalStateException e)
		{
			try
			{
				connection.close();
			}
			catch (SQLException e1)
			{
				e1.printStackTrace();
			}

			// Connection pool has been disposed = no database connection.
			throw new NoDatabaseLinkException();
		}
		catch (NumberFormatException e)
		{
			// Given auth string not a number.
			return null;
		}

		try
		{
			connection.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return loggedInUser;
	}

	/**
	 * Gets the a Role object from the given role id.
	 *
	 * @param roleid role database ID
	 * @return the corresponding {@link UserRole} object
	 * @throws NoDatabaseLinkException
	 */
	private static UserRole getRoleFromID(final int roleid) throws NoDatabaseLinkException
	{
		Connection connection = getConnection();
		Statement statement = null;
		UserRole role = null;
		String name = null;
		try
		{
			// Initialize a statement.
			statement = connection.createStatement();

			statement.execute("SELECT name FROM " + DatabaseTable.ROLES + " WHERE role_id = " + roleid + ";");

			ResultSet result = statement.getResultSet();

			// Only one result.
			if (result.next())
				name = result.getString("name");
			role = UserController.stringToRole(name);

			// Close all resources.
			statement.close();
			connection.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (IllegalStateException e)
		{
			try
			{
				connection.close();
			}
			catch (SQLException e1)
			{
				e1.printStackTrace();
			}

			// Connection pool has been disposed = no database connection.
			throw new NoDatabaseLinkException();
		}

		try
		{
			connection.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return role;
	}

	/**
	 * Gets a list unique product codes in the database.
	 *
	 * @param count how many product codes to get
	 * @return a list of integer product codes
	 */
	public static List<Integer> getProductCodeList(final int count)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets an {@link ObservableList} of user names and roles.
	 *
	 * @return a list of usersr
	 * @throws NoDatabaseLinkException
	 */
	public static ObservableList<User> getPublicUserDataList() throws NoDatabaseLinkException
	{
		Connection connection = getConnection();
		Statement statement = null;

		try
		{
			// Initialize a statement.
			statement = connection.createStatement();

			statement.execute("SELECT first_name, last_name, role FROM " + DatabaseTable.USERS + ";");

			ResultSet result = statement.getResultSet();

			userViewList.clear();

			while (result.next())
			{
				userViewList
						.add(new User(result.getRow(), result.getString("first_name"), result.getString("last_name"), getRoleFromID(result.getInt("role"))));
			}

			// Close all resources.
			result.close();
			statement.close();
			connection.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (IllegalStateException e)
		{
			try
			{
				connection.close();
			}
			catch (SQLException e1)
			{
				e1.printStackTrace();
			}

			// Connection pool has been disposed = no database connection.
			throw new NoDatabaseLinkException();
		}

		try
		{
			connection.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		System.out.println("User list updated.");
		return userViewList;
	}

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
	 * Gets user data by their database id.
	 *
	 * @param id database id of the user
	 * @return a {@link User} object
	 */
	public static User getUserByID(final int id) throws NoDatabaseLinkException
	{
		Connection connection = getConnection();
		Statement statement = null;
		User user = null;

		try
		{
			// Initialize a statement.
			statement = connection.createStatement();

			statement.execute("SELECT first_name, last_name, role FROM " + DatabaseTable.USERS + " WHERE user_id = " + id + ";");

			ResultSet result = statement.getResultSet();

			// Only one result.
			if (result.next())
				user = new User(result.getRow(), result.getString("first_name"), result.getString("last_name"), getRoleFromID(result.getInt("role")));

			// Close all resources.
			statement.close();
			connection.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (IllegalStateException e)
		{
			try
			{
				connection.close();
			}
			catch (SQLException e1)
			{
				e1.printStackTrace();
			}

			// Connection pool has been disposed = no database connection.
			throw new NoDatabaseLinkException();
		}

		try
		{
			connection.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return user;
	}

	/*
	 * PUBLIC DATABASE SETTER METHODS
	 */

	/**
	 * Initializes the database.
	 *
	 * @throws NoDatabaseConnectionException
	 */
	@SuppressWarnings("resource")
	public static void initializeDatabase() throws NoDatabaseLinkException
	{
		System.out.println("Initializing database...");

		Connection connection = getConnection();
		Statement statement = null;

		try
		{
			// Initialize a statement.
			statement = connection.createStatement();

			// Run the initialization script.
			statement.execute("RUNSCRIPT FROM './data/init.sql';");

			// Close all resources.
			statement.close();
			connection.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		catch (IllegalStateException e)
		{
			// Connection pool has been disposed = no database connection.
			throw new NoDatabaseLinkException();
		}

		System.out.println("Database initialized.");
	}

	/**
	 * <p>Adds a new user to the database.</p>
	 * <p>Warning: Assumes that given data is valid.</p>
	 *
	 * @param badgeID badge ID of the user
	 * @param pin PIN of the user
	 * @param firstName first name of the user
	 * @param lastName last name of the user
	 * @param roleID the ID of the role of the user
	 * @throws SQLException when given data was technically invalid
	 * @throws NoDatabaseLinkException when database link was lost
	 */
	public static void addUser(final String badgeID, final String pin, final String firstName, final String lastName, final int roleID)
			throws NoDatabaseLinkException, SQLException
	{
		Connection connection = getConnection();
		Statement statement = null;

		try
		{
			// Initialize a statement.
			statement = connection.createStatement();

			// If no pin is defined, add badge id.
			if (pin == null || pin.isEmpty())
				statement.execute("INSERT INTO `" + DatabaseTable.USERS + "`(`badge_id`, `first_name`, `last_name`, `role`)" + "VALUES(" + badgeID + ",'"
						+ firstName + "','" + lastName + "'," + roleID + ");");
			else
				statement.execute("INSERT INTO `" + DatabaseTable.USERS + "`(`pin`, `first_name`, `last_name`, `role`)" + "VALUES(" + pin + ",'" + firstName
						+ "','" + lastName + "'," + roleID + ");");

			// Close all resources.
			statement.close();
			connection.close();
		}
		catch (IllegalStateException e)
		{
			// Connection pool has been disposed = no database connection.
			throw new NoDatabaseLinkException();
		}

		System.out.println("User '" + firstName + " " + lastName + "' added.");

		// Update the user list displayed in the UI after adding a new user.
		getPublicUserDataList();
	}

	/**
	 * Removes a user with the specified database row ID.
	 *
	 * @param databaseID the database ID of the user to delete
	 * @throws NoDatabaseLinkException
	 * @throws SQLException
	 */
	public static void removeUser(final int databaseID) throws NoDatabaseLinkException, SQLException
	{
		Connection connection = getConnection();
		Statement statement = null;

		try
		{
			// Initialize a statement.
			statement = connection.createStatement();

			// If no pin is defined, add badge id.
			statement.execute("DELETE FROM `" + DatabaseTable.USERS + "` WHERE user_id = " + databaseID + ";");

			// Close all resources.
			statement.close();
			connection.close();
		}
		catch (IllegalStateException e)
		{
			// Connection pool has been disposed = no database connection.
			throw new NoDatabaseLinkException();
		}

		System.out.println("User ID " + databaseID + " deleted.");

		// Update the user list displayed in the UI after removing a user.
		getPublicUserDataList();
	}
}
