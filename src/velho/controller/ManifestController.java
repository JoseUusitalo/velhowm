package velho.controller;

import java.time.Instant;
import java.util.Date;
import java.util.Set;

import org.apache.log4j.Logger;

import javafx.scene.Node;
import velho.controller.interfaces.UIActionController;
import velho.model.Manifest;
import velho.model.ManifestState;
import velho.model.ProductBox;
import velho.model.enums.UserRole;
import velho.view.GenericTabView;
import velho.view.MainWindow;
import velho.view.ManifestManagementView;
import velho.view.ManifestView;

/**
 * A controller for handling {@link Manifest} objects.
 *
 * @author Jose Uusitalo
 */
public class ManifestController implements UIActionController
{
	/**
	 * Apache log4j logger: User.
	 */
	private static final Logger USRLOG = Logger.getLogger("userLogger");

	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(ManifestController.class.getName());

	/**
	 * The manifests tab.
	 */
	private GenericTabView tabView;

	/**
	 * The panel in the manifest tab.
	 */
	private ManifestManagementView managementView;

	/**
	 * The current manifest being shown.
	 */
	private Manifest currentManifest;

	/**
	 * The {@link MainWindow}.
	 */
	private MainWindow mainWindow;

	/**
	 * @param mainWindow
	 */
	public ManifestController(final MainWindow mainWindow)
	{
		this.mainWindow = mainWindow;
	}

	/**
	 * Gets the manifest list view.
	 *
	 * @return a tabular view of all manifests
	 */
	public Node getBrowseManifestsView()
	{
		return ListController.getTableView(this, DatabaseController.getManifestDataColumns(), DatabaseController.getAllManifests());
	}

	/**
	 * Shows a list of all manifests in the manifest tab.
	 */
	public void showBrowseManifestsView()
	{
		USRLOG.info("Browsing manifests.");

		managementView.setContent(getBrowseManifestsView());
		showStateSelector(null);
	}

	/**
	 * Shows the data of a single manifest in the manifest tab.
	 *
	 * @param manifest {@link Manifest} to show
	 */
	public void showManifestView(final Manifest manifest)
	{
		USRLOG.info("Viewing manifest: " + manifest);
		currentManifest = manifest;
		managementView.setContent(new ManifestView(manifest, this).getView());
		// The method showing the combo box state selector is called in the
		// view.
	}

	/**
	 * Gets the manifest tab view.
	 *
	 * @return the tab view for manifests
	 */
	public Node getView()
	{
		if (tabView == null)
		{
			tabView = new GenericTabView();
			managementView = new ManifestManagementView(this);
			tabView.setView(managementView.getView());

			// Manifest list is shown by default.
			showBrowseManifestsView();
		}

		return tabView.getView();
	}

	/**
	 * Changes the state of the manifest currently being viewed.
	 *
	 * @param newState the new state of the manifest
	 */
	public void setCurrentManifestState(final ManifestState newState)
	{
		USRLOG.info(currentManifest + " state changed to " + newState + ".");
		currentManifest.setState(newState);

		if (DatabaseController.save(currentManifest) > 0)
			SYSLOG.info("Updated database: " + currentManifest);
		else
			SYSLOG.info("Database update failed: " + currentManifest);
	}

	/**
	 * Adds the manifest state selector to the management panel.
	 * Use <code>null</code> to remove it.
	 *
	 * @param stateBox the manifest state combo box
	 */
	public void showStateSelector(final Node stateBox)
	{
		managementView.setRightNode(stateBox);
	}

	/**
	 * Processes the set of boxes that barcode scanner built from the barcode of
	 * the newly arrived shipment manifest.
	 *
	 * @param boxSet set of {@link ProductBox} objects on the physical manifest
	 */
	public void receiveShipment(final Set<ProductBox> boxSet, final Date orderDate, final int driverID)
	{
		Manifest manifest;

		manifest = new Manifest(DatabaseController.getManifestStateByID(3), driverID, orderDate, Date.from(Instant.now()));
		manifest.setBoxes(boxSet);

		if (DatabaseController.save(manifest) > 0)
		{
			// If the user is a Manager (but not an Administrator!) show a
			// popup.
			if (LoginController.userRoleIs(UserRole.MANAGER))
			{
				if (PopupController.confirmation(LocalizationController.getString("manifestShipmentArrivalPopUp")))
				{
					showManifestView(manifest);
					mainWindow.selectTab(LocalizationController.getString("addManifestsTab"));
				}
			}
		}

	}

	@Override
	public void createAction(final Object data)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void updateAction(final Object data)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void addAction(final Object data)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void removeAction(final Object data)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void deleteAction(final Object data)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void viewAction(final Object data)
	{
		showManifestView((Manifest) data);
	}
}
