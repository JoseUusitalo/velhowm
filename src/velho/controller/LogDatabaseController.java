package velho.controller;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.h2.jdbcx.JdbcConnectionPool;

import velho.model.enums.DatabaseFileState;
import velho.model.enums.DatabaseQueryType;
import velho.model.enums.DatabaseTable;
import velho.model.exceptions.ExistingDatabaseLinkException;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.view.MainWindow;

/**
 * The H2 logs database controller.
 *
 * @author Jose Uusitalo
 */
public class LogDatabaseController
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(LogDatabaseController.class.getName());

	/**
	 * The database URI on the local machine.
	 */
	private final static String DB_URI = "jdbc:h2:./data/velholog;MV_STORE=FALSE;MVCC=FALSE;";

	/**
	 * User name for the system itself.
	 */
	private final static String USERNAME = "VELHOLOG";

	/**
	 * The path to the database file in the file system.
	 */
	private final static String DB_FILEPATH = "./data/velholog.h2.db";

	/**
	 * A pool where database connections are acquired from.
	 */
	private static JdbcConnectionPool connectionPool;

	/**
	 * Runs a database query with the given data.
	 *
	 * @param type query command
	 * @param tableName the {@link DatabaseTable}
	 * @param columns columns to select (can be <code>null</code>)
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
	private static List<Object> runQuery(final DatabaseQueryType type, final DatabaseTable tableName, final Map<DatabaseTable, String> joinOnValues,
			final String[] columns, final Map<String, Object> columnValues, final List<String> where) throws NoDatabaseLinkException
	{
		final Connection connection = getConnection();
		Statement statement = null;

		// Most other queries.
		final List<Object> datalist = new ArrayList<Object>();

		try
		{
			ResultSet result = null;

			// Initialize a statement.
			statement = connection.createStatement();

			statement.execute(DatabaseController.sqlBuilder(type, tableName, joinOnValues, columns, columnValues, where), Statement.RETURN_GENERATED_KEYS);

			switch (type)
			{
				case SELECT:
					result = statement.getResultSet();

					if (columns.length == 1 && columns[0] != "*")
					{
						while (result.next())
							datalist.add(result.getObject(columns[0]));
					}
					else
					{
						switch (tableName)
						{
							case DBLOGS:
							case SYSLOGS:
								while (result.next())
									datalist.add(result.getTimestamp("time") + " [" + result.getString("level") + "] " + result.getString("message"));
								break;

							case USRLOGS:
								while (result.next())
									datalist.add(result.getTimestamp("time") + " [" + result.getString("level") + "] "
											+ DatabaseController.getUserByID(result.getInt("user_id")).getFullDetails() + ": " + result.getString("message"));
								break;
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
			e.printStackTrace();
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

		return datalist;
	}

	/*
	 * -------------------------------- PUBLIC DATABASE METHODS --------------------------------
	 */

	/**
	 * Attempts to re-link the database.
	 */
	private static void relink()
	{
		if (MainWindow.DEBUG_MODE)
			System.out.println("Attempting to relink log database.");

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
				if (MainWindow.DEBUG_MODE)
					System.out.println("Log database is already in use.");
				PopupController.error("Log database is already in use. Please close the open application.");
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

		PopupController.warning("Log database connection was temporarily lost. Please try again or restart the application.");
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
	public static DatabaseFileState link() throws ClassNotFoundException, ExistingDatabaseLinkException
	{
		if (connectionPool != null)
			throw new ExistingDatabaseLinkException();

		// Load the driver.
		Class.forName("org.h2.Driver");

		String uri = DB_URI;
		DatabaseFileState state = null;

		/*
		 * If the database does not exists, it will be created with the connection pool.
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
			// We obviously can't use the logger before the log database exists.
			if (MainWindow.DEBUG_MODE)
				System.out.println("Log database does not exist, creating a new database.");
		}

		// Create a connection pool.
		connectionPool = JdbcConnectionPool.create(uri, USERNAME, "gottaKeepEmL0G5safe");

		// Try getting a connection. If the database does not exist, it is created.
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
			{
				if (MainWindow.DEBUG_MODE)
					System.out.println("Log database linked.");
			}
			else
			{
				if (MainWindow.DEBUG_MODE)
					System.out.println("Log database linking failed.");
			}
		}
		else
		{
			if (MainWindow.DEBUG_MODE)
				System.out.println("Log database creation failed.");
		}

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
	public static void unlink() throws NoDatabaseLinkException
	{
		if (connectionPool == null)
			throw new NoDatabaseLinkException();

		SYSLOG.debug("Unlinking log database.");
		connectionPool.dispose();
		connectionPool = null;
	}

	/**
	 * Links and initializes the database.
	 */
	public static boolean connectAndInitialize() throws ClassNotFoundException, ExistingDatabaseLinkException, NoDatabaseLinkException
	{
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

	/**
	 * Initializes the database.
	 *
	 * @return <code>true</code> if database changed as a result of this call
	 * @throws NoDatabaseConnectionException
	 */
	public static boolean initializeDatabase() throws NoDatabaseLinkException
	{
		if (MainWindow.DEBUG_MODE)
			System.out.println("Initializing log database...");

		final Connection connection = getConnection();
		Statement statement = null;

		boolean changed = false;

		try
		{
			// Initialize a statement.
			statement = connection.createStatement();
			statement.execute("RUNSCRIPT FROM './data/loginit.sql';");
			changed = (statement.getUpdateCount() > 0);
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
			e.printStackTrace();
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

		if (changed)
			SYSLOG.info("Log database initialized.");

		return changed;
	}

	/*
	 * -------------------------------- PUBLIC GETTER METHODS --------------------------------
	 */

	/**
	 * Gets the full system log.
	 *
	 * @return the system log
	 * @throws NoDatabaseLinkException
	 */
	public static ArrayList<Object> getSystemLog() throws NoDatabaseLinkException
	{
		final String[] columns = { "time", "level", "message" };
		final List<String> where = new ArrayList<String>();
		where.add("level = 'INFO'");

		// If not in debug mode return just the info.
		if (!MainWindow.DEBUG_MODE)
			return (ArrayList<Object>) runQuery(DatabaseQueryType.SELECT, DatabaseTable.SYSLOGS, null, columns, null, where);

		// Else get debug too.
		where.clear();
		where.add("level = 'INFO' OR level = 'DEBUG'");

		return (ArrayList<Object>) runQuery(DatabaseQueryType.SELECT, DatabaseTable.SYSLOGS, null, columns, null, where);
	}

	/**
	 * Gets the full user log with user names.
	 *
	 * @return the user log
	 * @throws NoDatabaseLinkException
	 */
	public static ArrayList<Object> getUserLog() throws NoDatabaseLinkException
	{
		final String[] columns = { "user_id", "time", "level", "message" };
		final List<String> where = new ArrayList<String>();
		where.add("level = 'INFO'");

		if (!MainWindow.DEBUG_MODE)
			return (ArrayList<Object>) runQuery(DatabaseQueryType.SELECT, DatabaseTable.USRLOGS, null, columns, null, where);

		where.clear();
		where.add("level = 'INFO' OR level = 'DEBUG'");

		return (ArrayList<Object>) runQuery(DatabaseQueryType.SELECT, DatabaseTable.USRLOGS, null, columns, null, where);
	}
}
