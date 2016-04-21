package velho;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import velho.controller.AllControllerTests;
import velho.model.AllModelTests;

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
	public final static void after()
	{
		System.out.println("---------------");
		System.out.println("All Tests Done.");
	}
}
