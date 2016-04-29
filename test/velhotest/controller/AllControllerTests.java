package velhotest.controller;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * A class for running all non-JavaFX controller tests in the project.
 *
 * @author Jose Uusitalo
 */
@RunWith(Suite.class)
// @formatter:off
@SuiteClasses({ DatabaseControllerTest.class,
				ExternalSystemsControllerTest.class,
				SearchControllerTest.class,
				UserControllerTest.class})
//@formatter:on
public class AllControllerTests
{
	@BeforeClass
	public final static void before()
	{
		System.out.println("\nStarting All Controller Tests...");
		System.out.println("--------------------------------");
	}

	@AfterClass
	public final static void after()
	{
		System.out.println("--------------------------");
		System.out.println("All Controller Tests Done.\n");
	}
}
