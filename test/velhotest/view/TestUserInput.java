package velhotest.view;

import org.junit.Before;
import org.junit.Test;
import org.loadui.testfx.GuiTest;

import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import velho.view.MainWindow;

public class TestUserInput extends GuiTest
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

	@Before
	public void init()
	{
		authenticationStringField = find("#authenticationStringField");

		authenticationStringField.setText("");
	}

	@Test(timeout = 10000)
	public void should_increment_countValue()
	{
		click(authenticationStringField);
		type("jeejee");
	}
}