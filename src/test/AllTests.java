package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.controller.AllControllerTests;
import test.model.AllModelTests;
import test.model.BarcodeScannerTest;
import test.model.UserTest;

@RunWith(Suite.class)
@SuiteClasses({ AllControllerTests.class, AllModelTests.class })
public class AllTests
{

}
