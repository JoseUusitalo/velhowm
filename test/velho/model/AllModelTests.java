package velho.model;

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
@SuiteClasses({ FreezerTest.class,
				ManifestTest.class,
				ProductBoxTest.class,
				ProductBoxSearchResultRowTest.class,
				ProductCategoryTest.class,
				ProductTest.class,
				RemovalListTest.class,
				RemovalPlatformTest.class,
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
		System.out.println("\nStarting All Model Tests...");
		System.out.println("---------------------------");
	}

	@AfterClass
	public final static void after()
	{
		System.out.println("---------------------");
		System.out.println("All Model Tests Done.\n");
	}
}
