package velho.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import velho.controller.ManifestController;

/**
 * View for managing manifests.
 *
 * @author Jose Uusitalo
 */
public class ManifestManagementView
{
	/**
	 * The root BorderPane for this view.
	 */
	private BorderPane bpane;

	/**
	 * The {@link ManifestController}.
	 */
	private ManifestController manifestController;

	/**
	 * Button for showing all removal lists.
	 */
	private Button browseButton;

	/**
	 * @param manifestController
	 */
	public ManifestManagementView(final ManifestController manifestController)
	{
		this.manifestController = manifestController;
	}

	/**
	 * Gets the manifest management view.
	 *
	 * @return the manifest management border pane
	 */
	public BorderPane getView()
	{
		if (bpane == null)
		{
			bpane = new BorderPane();

			final GridPane managementPanel = new GridPane();
			managementPanel.getStyleClass().add("standard-padding");

			browseButton = new Button("Browse Manifests");
			managementPanel.add(browseButton, 0, 0);

			browseButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					manifestController.showBrowseManifestsView();
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
		browseButton.setVisible(visible);
	}
}
