package velho.controller;

import java.io.File;
import java.sql.*;
import java.util.List;
<<<<<<< HEAD
=======
import java.util.LinkedHashSet;
import java.util.Set;
>>>>>>> branch 'develop' of https://gitlab.com/joseu/velho.git

import org.h2.jdbcx.JdbcConnectionPool;

import velho.model.enums.DatabaseTable;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseLinkException;

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

	/*
	 * PUBLIC DATABASE MANIPULATION METHODS
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
			statement.execute("RUNSCRIPT FROM 'C:/git/velho/data/init.sql'");

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
	 * @param userID
	 * @param userFirstName
	 * @param userLastName
	 * @param userRole
	 * @throws SQLException when given data was invalid
	 * @throws NoDatabaseLinkException when database link was lost
	 *
	 */
	@SuppressWarnings("resource")
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
	}

	/**
	 * Checks if the given role name is valid.
	 *
	 * @param roleName the name of the role
	 * @return the database ID of the given role (a value greater than 0) or <code>-1</code> if the role does not exist
	 * in the database
	 */
	public static int isValidRole(final String roleName) throws NoDatabaseLinkException
	{
		Connection connection = getConnection();
		Statement statement = null;
		int id = -1;

		try
		{
			// Initialize a statement.
			statement = connection.createStatement();

			// Run the initialization script.
			statement.execute("SELECT id FROM " + DatabaseTable.ROLES + " WHERE name = '" + roleName + "'");

			ResultSet result = statement.getResultSet();
			result.next();

			// Will only return one row because the name value is UNIQUE.
			id = result.getInt("id");

			// Close all resources.
			result.close();
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

	public static Set<String> getUserRoleNames()
	{
		return new LinkedHashSet<String>();
	}

	public static Object authenticate(String authenticationString)
	{
		// TODO Auto-generated method stub
		return null;
	}


	public static List<Integer> getProductCodeList(int i){
		// TODO Auto-generated method stub
		return null;
	}
	public static List<String> getUserRoles()

	{
		// TODO Auto-generated method stub
		return null;
	}
}
