package velho.controller;

import org.apache.log4j.Logger;

import javafx.scene.Node;
import velho.model.Manifest;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.model.interfaces.UIActionController;
import velho.view.GenericTabView;
import velho.view.ManifestView;

/**
 * A controller for handling {@link Manifest} objects.
 *
 * @author Jose Uusitalo
 */
public class ManifestController implements UIActionController
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(ManifestController.class.getName());

	/**
	 * The manifests tab.
	 */
	private GenericTabView tabView;

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
	public Node getView()
	{
		if (tabView == null)
		{
			tabView = new GenericTabView();
			try
			{
				tabView.setView(ListController.getTableView(this, DatabaseController.getManifestDataColumns(), DatabaseController.getAllManifests()));
			}
			catch (NoDatabaseLinkException e)
			{
				DatabaseController.tryReLink();
				tabView.setView(null);
			}
		}

		return tabView.getView();
	}

	@Override
	public void createAction(final Object data)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void updateAction(final Object data)
	{
		// TODO Auto-generated method stub
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
		tabView.setView(new ManifestView(this, (Manifest) data).getView());
	}
}
