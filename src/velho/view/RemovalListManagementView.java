package velho.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
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

	/**
	 * @param removalListController
	 * @param listController
	 */
	public RemovalListManagementView(final RemovalListController removalListController)
	{
		this.removalListController = removalListController;
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

			final GridPane managementPanel = new GridPane();
			managementPanel.getStyleClass().add("standard-padding");

			browseListsButton = new Button("Browse Removal Lists");
			browseListsButton.setAlignment(Pos.CENTER_LEFT);
			managementPanel.add(browseListsButton, 0, 0);

			final Label removalListManagementLabel = new Label("Removal List Management");
			removalListManagementLabel.getStyleClass().add("standard-padding");
			removalListManagementLabel.getStyleClass().add("centered-title");
			removalListManagementLabel.setMaxWidth(Double.MAX_VALUE);
			managementPanel.add(removalListManagementLabel, 1, 0);
			GridPane.setHgrow(removalListManagementLabel, Priority.ALWAYS);

			newListButton = new Button("Create New Removal List");
			newListButton.setAlignment(Pos.CENTER_RIGHT);
			managementPanel.add(newListButton, 2, 0);

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

			bpane.setTop(managementPanel);
		}

		return bpane;
	}

	/**
	 * Sets the view below the management pane.
	 */
	public void setContent(final Node view)
	{
		bpane.setCenter(view);
	}

	/**
	 * Gets the view below the management pane.
	 */
	public Node getContent()
	{
		return bpane.getCenter();
	}

	/**
	 * Destroys the view.
	 */
	public void destroy()
	{
		bpane = null;
	}

	/**
	 * Sets the visibility of the browse removal lists button.
	 *
	 * @param visible <code>true</code> to show the button
	 */
	public void setBrowseListsButtonVisiblity(final boolean visible)
	{
		browseListsButton.setVisible(visible);
	}
}
