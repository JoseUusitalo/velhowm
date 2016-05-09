package velhotest.view;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import velho.controller.DatabaseController;
import velho.controller.LogDatabaseController;
import velho.view.MainWindow;

public class TestLogin extends GuiTest
{
	MainWindow app;
	private TextField authenticationStringField;

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
	public void initComponents()
	{
		authenticationStringField = find("#authenticationStringField");

		authenticationStringField.setText("");
	}

	@Test(timeout = 10000)
	public void should_increment_countValue()
	{
		click(authenticationStringField);
		type("jeejee");
		assertEquals("jeejee", (authenticationStringField.getText()));
	}
}