package velho.view;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

/**
 * A view for handling the contents of the Removal Lists tab in the main menu.
 *
 * @author Jose Uusitalo
 */
public class RemovalListTabView
{
	/**
	 * The root BorderPane for this view.
	 */
	private BorderPane bPane;

	public RemovalListTabView()
	{
	}

	/**
	 * Gets the login view.
	 *
	 * @return the login view BorderPane
	 */
	public BorderPane getView()
	{
		if (bPane == null)
		{
			bPane = new BorderPane();
		}
		return bPane;
	}

	/**
	 * Destroys the view.
	 */
	public void destroy()
	{
		bPane = null;
	}

	/**
	 * Sets the view in the tab.
	 *
	 * @param view new view
	 */
	public void setView(final Node view)
	{
		bPane.setCenter(view);
	}
}
