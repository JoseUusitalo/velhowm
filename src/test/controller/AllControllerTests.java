package test.controller;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * A class for running all non-JavaFX controller tests in the project.
 * 
 * @author Jose Uusitalo
 */
@RunWith(Suite.class)
@SuiteClasses({ DatabaseControllerTest.class })
public class AllControllerTests
{
}
