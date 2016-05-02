package velho.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import velho.controller.LocalizationController;
import velho.controller.ManifestController;
import velho.controller.UIController;
import velho.model.interfaces.GenericView;

/**
 * View for managing manifests.
 *
 * @author Jose Uusitalo
 */
public class ManifestManagementView implements GenericView
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
	 * The header of the view in the tab.
	 */
	private HBox managementPanel;

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

			managementPanel = new HBox(10);
			managementPanel.getStyleClass().add("standard-padding");

			Button browseButton = new Button(LocalizationController.getString("browseManifestsButton"));
			GridPane.setConstraints(browseButton, 0, 0, 1, 1, HPos.LEFT, VPos.CENTER);

			/*
			 * After nearly an hour of trying to align the two nodes (button and
			 * state selector) on separate sides of
			 * the HBox this was the best and only working solution.
			 */
			final HBox spacer = new HBox();
			HBox.setHgrow(spacer, Priority.ALWAYS);

			managementPanel.getChildren().addAll(browseButton, spacer);

			browseButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					manifestController.showBrowseManifestsView();
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
	 * @param view the new center view
	 */
	public void setContent(final Node view)
	{
		bpane.setCenter(view);
	}

	/**
	 * Gets the view below the management pane.
	 *
	 * @return the center node
	 */
	public Node getContent()
	{
		return bpane.getCenter();
	}

	/**
	 * Adds the given node to the right end of the management panel.
	 * Use <code>null</code> to remove the previously placed node.
	 *
	 * @param node node to add
	 */
	public void setRightNode(final Node node)
	{
		if (node == null)
		{
			try
			{
				// NOTE: Assumes that the panel only has the browse button and
				// spacer in it permanently.
				managementPanel.getChildren().remove(2);
			}
			catch (IndexOutOfBoundsException e)
			{
				// It's fine.
			}
		}
		else
		{
			managementPanel.getChildren().add(node);
		}
	}

	@Override
	public void recreate()
	{
		bpane = null;
		managementPanel = null;
		getView();
	}

	@Override
	public void destroy()
	{
		bpane = null;
		managementPanel = null;
	}
}
