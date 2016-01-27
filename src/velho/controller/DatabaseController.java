package velho.controller;

import java.io.File;
import java.sql.*;

import org.h2.jdbcx.JdbcConnectionPool;

import velho.model.exceptions.NoDatabaseConnectionException;

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
	 * Creates the connection to the database.
	 * Use {@link #shutdown()} to close the connection.
	 *
	 * @return <code>true</code> if connection was created successfully
	 */
	public static boolean connect() throws ClassNotFoundException
	{
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
			if (isConnected())
			{
				System.out.println("Database connection created.");
				return true;
			}

			System.out.println("Database connection failed.");
			return false;
		}

		System.out.println("Database creation failed.");
		return false;
	}

	/**
	 * Shuts down the connection to the database.
	 * Use {@link #connect()} to connec to the database again.
	 */
	public static void shutdown()
	{
		connectionPool.dispose();
		System.out.println("Database shut down.");
	}

	/**
	 * Initializes the database.
	 *
	 * @throws NoDatabaseConnectionException
	 */
	@SuppressWarnings("resource")
	public static void initializeDatabase() throws NoDatabaseConnectionException
	{
		System.out.println("Initializing database...");

		// Check for connection.
		if (!isConnected())
		{
			throw new NoDatabaseConnectionException();
		}

		Connection connection = null;
		Statement statement = null;

		try
		{
			// Get a new connection.
			connection = connectionPool.getConnection();

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
			throw new NoDatabaseConnectionException();
		}

		System.out.println("Database initialized.");
	}

	/**
	 * Checks if a database connection exists.
	 *
	 * @return <code>true</code> if a database connection exists
	 */
	public static boolean isConnected()
	{
		return connectionPool != null;
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
}
