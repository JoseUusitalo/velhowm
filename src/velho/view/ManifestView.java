package velho.view;

import javafx.scene.layout.BorderPane;
import velho.controller.DatabaseController;
import velho.controller.ListController;
import velho.controller.ManifestController;
import velho.model.Manifest;

/**
 * A view showing the data of a single {@link Manifest} object.
 *
 * @author Jose Uusitalo
 */
public class ManifestView
{
	/**
	 * The root border pane for this view.
	 */
	private BorderPane bpane;

	/**
	 * The {@link ManifestController}.
	 */
	private ManifestController manifestController;

	/**
	 * The {@link Manifest} to display in this view.
	 */
	private Manifest manifest;

	/**
	 * @param manifestController
	 * @param manifest
	 */
	public ManifestView(final ManifestController manifestController, final Manifest manifest)
	{
		this.manifestController = manifestController;
		this.manifest = manifest;
	}

	/**
	 * Gets the manifest list view.
	 *
	 * @return the manifest list root border pane
	 */
	public BorderPane getView()
	{
		if (bpane == null)
		{
			bpane = new BorderPane();

			BorderPane boxlist = (BorderPane) ListController.getTableView(manifestController, DatabaseController.getProductSearchDataColumns(false, false),
					manifest.getObservableBoxes());

			bpane.setCenter(boxlist);
		}

		return bpane;
	}
}
