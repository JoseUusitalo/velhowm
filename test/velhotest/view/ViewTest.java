package velhotest.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import velho.controller.DatabaseController;
import velho.controller.LogDatabaseController;
import velho.view.MainWindow;

/**
 * Attempts at testing the UI.
 *
 * @author Jose Uusitalo &amp; Edward Puustinen
 */
@SuppressWarnings("static-method")
public class ViewTest extends GuiTest
{
	private MainWindow app;
	private TextField firstNameField;
	private TextField lastNameField;
	private TextField authenticationStringField;
	private Button logInButton;
	private TextField pinField;
	private TextField userFnameField;
	private TextField userLNameField;
	private Button createButton;

	@Override
	protected Parent getRootNode()
	{
		try
		{
			if (app == null)
			{
				stage.centerOnScreen();
				app = new MainWindow();
				app.start(stage);
			}

			final Parent root = app.getStage().getScene().getRoot();
			app.getStage().getScene().setRoot(new Region());

			return root;
		}
		catch (Exception e)
		{
			e.printStackTrace();

			return null;
		}
	}

	@BeforeClass
	public static final void init() throws Exception
	{
		LogDatabaseController.connectAndInitialize();
		DatabaseController.link();
		DatabaseController.loadSampleData();
	}

	/**
	 * Unlinks from both databases.
	 */
	@AfterClass
	public static final void unlinkDatabases() throws Exception
	{
		DatabaseController.unlink();
		LogDatabaseController.unlink();
	}

	@Before
	public void waitForUI()
	{
		try
		{
			Thread.sleep(3000L);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	@Test(timeout = 30000)
	public void loginAdmin()
	{
		firstNameField = find("#firstNameField");
		lastNameField = find("#lastNameField");
		authenticationStringField = find("#authenticationStringField");
		logInButton = find("#logInButton");

		click(firstNameField);
		type("Admin");
		assertEquals("Admin", (firstNameField.getText()));

		click(lastNameField);
		type("Test");
		assertEquals("Test", (lastNameField.getText()));

		click(authenticationStringField);
		type("111111");
		assertEquals("111111", (authenticationStringField.getText()));

		click(logInButton);

		assertTrue(((Label) find("#userName")).getText().contains("Admin Test"));
	}

	@Test(timeout = 30000)
	public void createUser()
	{
		userFnameField = find("#userFnameField");
		userLNameField = find("#userLNameField");
		pinField = find("#pinField");
		createButton = find("#createButton");

		click(pinField);
		type("8888");
		assertEquals("8888", (pinField.getText()));

		click(userFnameField);
		type("Victor");
		assertEquals("Victor", (userFnameField.getText()));

		click(userLNameField);
		type("MidPlzOrFeed");
		assertEquals("MidPlzOrFeed", (userLNameField.getText()));

		click(createButton);
	}
}