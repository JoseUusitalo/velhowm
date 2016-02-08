package test.model;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * A class for running all model tests in the project.
 *
 * @author Jose Uusitalo
 */
@RunWith(Suite.class)
// @formatter:off
@SuiteClasses({ BarcodeScannerTest.class,
				FreezerTest.class,
				ProductBoxTest.class,
				ProductCategoryTest.class,
				ProductTest.class,
				ShelfTest.class,
				UserRoleTest.class,
				UserTest.class
				})
// @formatter:on
public class AllModelTests
{
	@BeforeClass
	public final static void before()
	{
		System.out.println("Starting All Model Tests...");
	}

	@AfterClass
	public final static void after()
	{
		System.out.println("All Model Tests Done.");
	}
}
