package velho.view;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import velho.model.interfaces.GenericView;

/**
 * A view for displaying other views vertically in a {@link VBox}.
 *
 * @author Jose Uusitalo
 */
public class VerticalViewGroup implements GenericView
{
	/**
	 * The root node.
	 */
	private VBox vbox;

	/**
	 */
	public VerticalViewGroup()
	{
		vbox = new VBox(10);
	}

	/**
	 * Gets the root node.
	 *
	 * @return the root node
	 */
	public VBox getView()
	{
		return vbox;
	}

	/**
	 * Sets the contents of this view group.
	 *
	 * @param views views to be added
	 */
	public void setContents(final Node... views)
	{
		vbox.getChildren().addAll(views);
	}

	@Override
	public void recreate()
	{
		vbox = null;
	}

	@Override
	public void destroy()
	{
		vbox = null;
	}
}
