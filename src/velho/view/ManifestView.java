package velho.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import velho.controller.ListController;
import velho.controller.LocalizationController;
import velho.controller.ManifestController;
import velho.controller.UIController;
import velho.controller.database.DatabaseController;
import velho.model.Manifest;
import velho.model.ManifestState;
import velho.model.interfaces.GenericView;

/**
 * A view showing the data of a single {@link Manifest} object.
 *
 * @author Jose Uusitalo
 */
public class ManifestView implements GenericView
{
	/**
	 * The root border pane for this view.
	 */
	private BorderPane bpane;

	/**
	 * The {@link Manifest} to display in this view.
	 */
	private final Manifest manifest;

	/**
	 * @param manifest
	 * @param manifestController
	 */
	public ManifestView(final Manifest manifest)
	{
		this.manifest = manifest;
	}

	/**
	 * Gets the manifest list view.
	 *
	 * @return the manifest list root border pane
	 * @throws NoDatabaseLinkException
	 */
	public BorderPane getView()
	{
		if (bpane == null)
		{
			bpane = new BorderPane();

			final BorderPane boxlist = (BorderPane) ListController.getTableView(ManifestController.getInstance(),
					DatabaseController.getInstance().getProductSearchDataColumns(false, false), manifest.getObservableBoxes());

			final HBox stateBox = new HBox(10);
			final Label stateLabel = new Label(LocalizationController.getInstance().getString("manifestStateLabel"));
			final ComboBox<Object> manifestState = new ComboBox<Object>();

			manifestState.getItems().addAll(DatabaseController.getInstance().getManifestStateChangeList(manifest.getState()));
			manifestState.getSelectionModel().select(manifest.getState());

			stateBox.getChildren().addAll(stateLabel, manifestState);
			stateBox.setAlignment(Pos.CENTER_RIGHT);

			manifestState.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>()
			{
				@Override
				public void changed(final ObservableValue<?> observableValue, final Object oldValue, final Object newValue)
				{
					ManifestController.getInstance().setCurrentManifestState((ManifestState) newValue);
				}
			});

			ManifestController.getInstance().showStateSelector(stateBox);
			bpane.setCenter(boxlist);
			UIController.getInstance().recordView(this);
		}

		return bpane;
	}

	@Override
	public void recreate()
	{
		bpane = null;
		getView();
	}

	@Override
	public void destroy()
	{
		bpane = null;
	}
}
