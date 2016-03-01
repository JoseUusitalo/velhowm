package velho.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import velho.controller.DatabaseController;
import velho.controller.ListController;
import velho.controller.ManifestController;
import velho.model.Manifest;
import velho.model.ManifestState;
import velho.model.exceptions.NoDatabaseLinkException;

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
	 * @param manifest
	 * @param manifestController
	 */
	public ManifestView(final Manifest manifest, final ManifestController manifestController)
	{
		this.manifestController = manifestController;
		this.manifest = manifest;
	}

	/**
	 * Gets the manifest list view.
	 *
	 * @return the manifest list root border pane
	 */
	public BorderPane getView() throws NoDatabaseLinkException
	{
		if (bpane == null)
		{
			bpane = new BorderPane();

			BorderPane boxlist = (BorderPane) ListController.getTableView(manifestController, DatabaseController.getProductSearchDataColumns(false, false),
					manifest.getObservableBoxes());

			HBox stateBox = new HBox(10);
			Label stateLabel = new Label("State:");
			ComboBox<Object> manifestState = new ComboBox<Object>();

			manifestState.getItems().addAll(DatabaseController.getAllManifestStates(manifest.getState()));
			manifestState.getSelectionModel().select(manifest.getState());

			stateBox.getChildren().addAll(stateLabel, manifestState);
			stateBox.setAlignment(Pos.CENTER_RIGHT);

			manifestState.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>()
			{
				@Override
				public void changed(final ObservableValue<?> observableValue, final Object oldValue, final Object newValue)
				{
					manifestController.setCurrentManifestState((ManifestState) newValue);
				}
			});

			manifestController.showStateSelector(stateBox);
			bpane.setCenter(boxlist);
		}

		return bpane;
	}
}
