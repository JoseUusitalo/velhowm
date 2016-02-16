package velho.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import velho.controller.ListController;
import velho.controller.RemovalListController;

/**
 * View for managing removal lists
 *
 * @author Jose Uusitalo
 */
public class RemovalListManagementView
{
	/**
	 * The root BorderPane for this view.
	 */
	private BorderPane bpane;

	/**
	 * The {@link ListController}.
	 */
	private ListController listController;

	/**
	 * The {@link RemovalListController}.
	 */
	private RemovalListController removalListController;

	/**
	 * Button for showing the removal list creation view.
	 */
	private Button newListButton;

	/**
	 * Button for showing all removal lists.
	 */
	private Button browseListsButton;

	public RemovalListManagementView(final RemovalListController removalListController, final ListController listController)
	{
		this.removalListController = removalListController;
		this.listController = listController;
	}

	/**
	 * Gets the removal list management view.
	 *
	 * @return the removal list management BorderPane
	 */
	public BorderPane getView()
	{
		if (bpane == null)
		{
			bpane = new BorderPane();

			VBox managementPanel = new VBox(10);
			// TODO: Use CSS.
			managementPanel.setBackground(new Background(new BackgroundFill(Paint.valueOf("EEEEEE"), null, null)));
			managementPanel.setPadding(new Insets(10));

			Label removalListManagementLabel = new Label("Removal List\nManagement");
			removalListManagementLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
			removalListManagementLabel.setPadding(new Insets(10));
			removalListManagementLabel.setMaxWidth(Double.MAX_VALUE);
			removalListManagementLabel.setAlignment(Pos.CENTER);

			newListButton = new Button("Create New Removal List");
			newListButton.setMaxWidth(Double.MAX_VALUE);

			browseListsButton = new Button("Browse Removal Lists");
			browseListsButton.setMaxWidth(Double.MAX_VALUE);

			newListButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					removalListController.showNewRemovalListView();
				}
			});

			browseListsButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					removalListController.showBrowseRemovalListsView();
				}
			});

			managementPanel.getChildren().addAll(removalListManagementLabel, newListButton, browseListsButton);
			bpane.setLeft(managementPanel);
		}

		return bpane;
	}

	/**
	 * Destroys the view.
	 */
	public void destroy()
	{
		bpane = null;
	}

	/**
	 * Toggles the visibility of the browse removal lists button.
	 */
	public void toggleBrowseListsButton()
	{
		browseListsButton.setVisible(!browseListsButton.isVisible());
	}
}
