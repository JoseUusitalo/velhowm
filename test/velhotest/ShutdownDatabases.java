package velhotest;

import velho.controller.DatabaseController;
import velho.controller.LogDatabaseController;

/**
 * Forcibly closes the database connection to both databases.
 * For use in the build after running JUnit tests so the databases are not in use when the program is run.
 *
 * @author Jose Uusitalo
 */
public class ShutdownDatabases
{
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception
	{
		System.out.println("Closing Hibernate session factory.");
		DatabaseController.closeSessionFactory();

		System.out.println("Shutting down log database.");
		LogDatabaseController.shutdown();
	}
}
