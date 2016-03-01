package velho.controller;

import org.apache.log4j.Logger;

import javafx.scene.Node;
import velho.model.Manifest;
import velho.model.ManifestState;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.model.interfaces.UIActionController;
import velho.view.GenericTabView;
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

	private Manifest currentManifest;

	/**
	 */
	public ManifestController()
	{
	}

	/**
	 * Gets the manifest list view.
	 *
	 * @return a tabular view of all manifests
	 */
	public Node getBrowseManifestsView() throws NoDatabaseLinkException
	{
		return ListController.getTableView(this, DatabaseController.getManifestDataColumns(), DatabaseController.getAllManifests());
	}

	@Override
	public void createAction(final Object data)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void updateAction(final Object data)
	{
		System.out.println("UPDATE: " + ((Manifest) data).toString());
	}

	@Override
	public void addAction(final Object data)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void removeAction(final Object data)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void deleteAction(final Object data)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void viewAction(final Object data)
	{
		showManifestView((Manifest) data);
	}

	/**
	 * Shows a list of all manifests in the manifest tab.
	 */
	public void showBrowseManifestsView()
	{
		USRLOG.info("Browsing manifests.");

		try
		{
			managementView.setContent(getBrowseManifestsView());
			showStateSelector(null);
		}
		catch (NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}
	}

	/**
	 * Shows the data of a single manifest in the manifest tab.
	 *
	 * @param manifest {@link Manifest} to show
	 */
	public void showManifestView(final Manifest manifest)
	{
		try
		{
			USRLOG.info("Viewing manifest: " + manifest);
			currentManifest = manifest;
			managementView.setContent(new ManifestView(manifest, this).getView());
			// The method showing the combo box state selector is called in the view.
		}
		catch (NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}
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

		try
		{
			if (currentManifest.saveToDatabase())
				SYSLOG.info("Updated database: " + currentManifest);
			else
				SYSLOG.info("Database update failed: " + currentManifest);
		}
		catch (NoDatabaseLinkException e)
		{
			DatabaseController.tryReLink();
		}
	}

	/**
	 * Adds the manifest state selector to the management panel.
	 * Use <code>null</code> to remove it.
	 *
	 * @param manifestState the manifest state combo box
	 */
	public void showStateSelector(final Node stateBox)
	{
		managementView.setRightNode(stateBox);
	}
}
