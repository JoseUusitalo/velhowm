package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.controller.AllControllerTests;
import test.model.AllModelTests;

@RunWith(Suite.class)
@SuiteClasses({ AllControllerTests.class, AllModelTests.class })
public class AllTests
{
	// Class for running all tests in the project.
}
