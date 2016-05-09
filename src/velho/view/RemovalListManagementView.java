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
import velho.controller.LocalizationController;
import velho.controller.RemovalListController;
import velho.controller.UIController;
import velho.model.interfaces.GenericView;

/**
 * View for managing removal lists
 *
 * @author Jose Uusitalo
 */
public class RemovalListManagementView implements GenericView
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

			browseListsButton = new Button(LocalizationController.getString("browseRemovalListsButton"));
			browseListsButton.setAlignment(Pos.CENTER_LEFT);
			managementPanel.add(browseListsButton, 0, 0);

			final Label removalListManagementLabel = new Label(LocalizationController.getString("removalListManagementLabel"));
			removalListManagementLabel.getStyleClass().add("standard-padding");
			removalListManagementLabel.getStyleClass().add("centered-title");
			removalListManagementLabel.setMaxWidth(Double.MAX_VALUE);
			managementPanel.add(removalListManagementLabel, 1, 0);
			GridPane.setHgrow(removalListManagementLabel, Priority.ALWAYS);

			newListButton = new Button(LocalizationController.getString("createRemovalListButton"));
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
			UIController.recordView(this);
		}

		return bpane;
	}

	/**
	 * Sets the view below the management pane.
	 *
	 * @param view the view to set to the center
	 */
	public void setContent(final Node view)
	{
		bpane.setCenter(view);
	}

	/**
	 * Gets the view below the management pane.
	 * @return the center view
	 */
	public Node getContent()
	{
		return bpane.getCenter();
	}

	/**
	 * Destroys the view.
	 */
	@Override
	public void recreate()
	{
		bpane = null;
		getView();
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

	@Override
	public void destroy()
	{
		bpane = null;
	}
}
