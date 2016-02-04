package test.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BarcodeScannerTest.class, ProductBoxTest.class, ProductTest.class, ShelfTest.class, UserRoleTest.class, UserTest.class })
public class AllModelTests
{
	// Class for running all model tests in the project.
}
