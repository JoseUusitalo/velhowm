package test.controller;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ DatabaseControllerTest.class, UserControllerTest.class })
public class AllControllerTests
{
	// Class for running all controller tests in the project.
}
