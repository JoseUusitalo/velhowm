package velho.view;

import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class ProductManagementView
{
	private VBox vbox;

	public ProductManagementView()
	{

		vbox = new VBox(10);

	}

	public VBox getView()
	{

		return vbox;
	}

	public void setContents(final Node... views)
	{
		vbox.getChildren().addAll(views);
	}

}
