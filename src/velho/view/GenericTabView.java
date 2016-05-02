package velho.view;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import velho.controller.UIController;
import velho.model.interfaces.GenericView;

/**
 * A view for handling the contents of certain tabs in the main menu.
 *
 * @author Jose Uusitalo
 */
public class GenericTabView implements GenericView
{
	/**
	 * The root BorderPane for this view.
	 */
	private BorderPane bPane;

	/**
	 * Defines the bPane border panel for this class.
	 */
	public GenericTabView()
	{
		bPane = new BorderPane();
	}

	/**
	 * Gets the root border pane.
	 *
	 * @return the root border pane
	 */
	public BorderPane getView()
	{
		if (bPane == null)
		{
			bPane = new BorderPane();
			UIController.recordView(this);
		}
		return bPane;

	}

	/**
	 * Sets the view in this tab.
	 *
	 * @param view new view
	 */
	public void setView(final Node view)
	{
		bPane.setCenter(view);
	}

	@Override
	public void recreate()
	{
		bPane = null;
		getView();
	}

	@Override
	public void destroy()
	{
		bPane = null;
	}
}
