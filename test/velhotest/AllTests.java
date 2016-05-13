package velhotest;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import velho.controller.database.DatabaseController;
import velho.controller.database.LogDatabaseController;
import velhotest.controller.AllControllerTests;
import velhotest.model.AllModelTests;

/**
 * A class for running all tests in the project.
 *
 * @author Jose Uusitalo
 */
@RunWith(Suite.class)
// @formatter:off
@SuiteClasses({ AllControllerTests.class,
				AllModelTests.class,})
// @formatter:on
public class AllTests
{
	@BeforeClass
	public final static void before()
	{
		System.out.println("Starting All Tests...");
		System.out.println("---------------------");
	}

	@AfterClass
	public final static void after() throws Exception
	{
		System.out.println("---------------");
		System.out.println("All Tests Done.\n");

		System.out.println("Close session factory");
		DatabaseController.getInstance().closeSessionFactory();

		System.out.println("\n\nClose log database.");
		LogDatabaseController.getInstance().shutdown();
	}
}
