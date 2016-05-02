package velho.view;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import velho.model.interfaces.GenericView;

public class ProductManagementView implements GenericView
{
	private VBox vbox;

	public ProductManagementView()
	{
	}

	public VBox getView()
	{
		if (vbox == null)
		{
			vbox = new VBox(10);
		}

		return vbox;
	}

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
