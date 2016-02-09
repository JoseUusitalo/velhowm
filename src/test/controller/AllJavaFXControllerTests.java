package test.controller;

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
@SuiteClasses({ UserControllerTest.class,
				DatabaseControllerFXTest.class})
//@formatter:on
public class AllJavaFXControllerTests
{
	@BeforeClass
	public final static void before()
	{
		System.out.println("Starting All JavaFX Controller Tests...");
	}

	@AfterClass
	public final static void after()
	{
		System.out.println("All JavaFX Controller Tests Done.");
	}
}
