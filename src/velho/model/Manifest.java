package velho.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import velho.controller.database.DatabaseController;

/**
 * A list of {@link ProductBox} objects that have been delivered to the warehouse from somewhere else.
 *
 * @author Jose Uusitalo
 */
public class Manifest extends AbstractDatabaseObject
{
	// TODO: Generalize products lists into an abstract class. Removal lists and Manifests are basically identical.

	/**
	 * The set of {@link ProductBox} objects.
	 */
	private Set<ProductBox> boxes;

	/**
	 * An {@link ObservableList} of {@link ProductBox} objects for display in the user interface.
	 */
	private ObservableList<Object> observableBoxes;

	/**
	 * The current state of this removal list.
	 */
	private ManifestState state;

	/**
	 * The ID of the driver who delivered the shipment.
	 */
	private int driverID;

	/**
	 * The data the shipment was ordered.
	 */
	private Date orderedDate;

	/**
	 * The data the shipment was received.
	 */
	private Date receivedDate;

	/**
	 * @param databaseID
	 * @param uuid
	 * @param driverID
	 * @param state
	 * @param ordered
	 * @param received
	 */
	public Manifest(final int databaseID, final UUID uuid, final int driverID, final ManifestState state, final Date ordered, final Date received)
	{
		setDatabaseID(databaseID);
		setUuid(uuid);
		this.state = state;
		this.driverID = driverID;
		this.orderedDate = ordered;
		this.receivedDate = received;
		this.boxes = new LinkedHashSet<ProductBox>();
		this.observableBoxes = FXCollections.observableArrayList();
	}

	/**
	 * @param databaseID
	 * @param state
	 * @param driverID
	 * @param ordered
	 * @param received
	 */
	public Manifest(final int databaseID, final int driverID, final ManifestState state, final Date ordered, final Date received)
	{
		this(databaseID, UUID.randomUUID(), driverID, state, ordered, received);
	}

	/**
	 * @param state
	 * @param driverID
	 * @param ordered
	 * @param received
	 */
	public Manifest(final int driverID, final ManifestState state, final Date ordered, final Date received)
	{
		this(0, UUID.randomUUID(), driverID, state, ordered, received);
	}

	/**
	 */
	public Manifest()
	{
		// For Hibernate.
		setUuid(UUID.randomUUID());
		this.boxes = new LinkedHashSet<ProductBox>();
		this.observableBoxes = FXCollections.observableArrayList();
	}

	@Override
	public String toString()
	{
		/*
		 * Note: Using getter on purpose because of Hibernate. Without it apparently there may be issues where the ID is
		 * 0 when it should not be.
		 */
		return "[" + getDatabaseID() + "] State: " + state + " Driver: " + driverID + " Ordered/Received: " + orderedDate.toString() + "/"
				+ receivedDate.toString() + " (" + boxes.size() + ")";
	}

	/**
	 * Gets the number of product boxes on this manifest.
	 *
	 * @return the number of boxes on this manifest
	 */
	public int getSize()
	{
		return boxes.size();
	}

	/**
	 * Gets the current state of this manifest.
	 *
	 * @return the state of this manifest
	 */
	public ManifestState getState()
	{
		return state;
	}

	/**
	 * Assigns a new state this manifest.
	 *
	 * @param state the new state of this manifest
	 */
	public void setState(final ManifestState state)
	{
		this.state = state;
	}

	/**
	 * Gets the ID of the driver who delivered the contents of this manifest.
	 *
	 * @return the driver ID attached to this manifest
	 */
	public int getDriverID()
	{
		return driverID;
	}

	public void setDriverID(final int driverID)
	{
		this.driverID = driverID;
	}

	/**
	 * Gets the date the contents of this manifest were ordered.
	 *
	 * @return the ordered date
	 */
	public Date getOrderedDate()
	{
		return orderedDate;
	}

	/**
	 * Sets the date the contents of this manifest were ordered.
	 *
	 * @param ordered the new ordered date
	 */
	public void setOrderedDate(final Date ordered)
	{
		this.orderedDate = ordered;
	}

	/**
	 * Gets the date the contents of this manifest were received at the warehouse.
	 *
	 * @return the shipment received date
	 */
	public Date getReceivedDate()
	{
		return receivedDate;
	}

	/**
	 * Sets the date the contents of this manifest were received at the warehouse.
	 *
	 * @param received the new date the shipment was received
	 */
	public void setReceivedDate(final Date received)
	{
		this.receivedDate = received;
	}

	/**
	 * Gets the set of product boxes on this manifest.
	 *
	 * @return the {@link ProductBox} objects on this manifest
	 */
	public Set<ProductBox> getBoxes()
	{
		return boxes;
	}

	/**
	 * Adds the specified {@link ProductBox} objects to this manifest.
	 *
	 * @param productBoxes set of boxes to add to this list, <code>null</code> to clear
	 * @return <code>true</code> if all boxes were added to this manifest
	 */
	public boolean setBoxes(final Set<ProductBox> productBoxes)
	{
		if (productBoxes == null)
		{
			for (final ProductBox box : this.boxes)
				box.setManifest(null);

			this.boxes.clear();
			this.observableBoxes.clear();
		}
		else
			this.boxes = productBoxes;

		boolean bswitch = true;

		for (final ProductBox box : this.boxes)
		{
			if (box != null)
			{
				box.setManifest(this);
				bswitch = bswitch && observableBoxes.add(new ProductBoxSearchResultRow(box));
			}
		}

		return bswitch;
	}

	/**
	 * Gets the contents of this manifest.
	 * Rebuilds the list on every method call by looping through the set of boxes.
	 *
	 * @return the {@link ProductBoxSearchResultRow} objects on this manifest
	 */
	public ObservableList<Object> getObservableBoxes()
	{
		observableBoxes.clear();
		boxes.forEach((final ProductBox box) -> observableBoxes.add(new ProductBoxSearchResultRow(box)));

		return observableBoxes;
	}

	/**
	 * Sets a new manifest state for this manifest by the database ID of the manifest state.
	 * Intended for use with loading data from CSV files.
	 *
	 * @param manifestStateID the database ID of the new manifest state of this manifest
	 * @see DatabaseController#getManifestStateByID(int)
	 */
	public void setManifestStateID(final int manifestStateID)
	{
		if (manifestStateID < 1)
			throw new IllegalArgumentException("Manifest State ID must be greater than 0, was '" + manifestStateID + "'.");

		this.state = DatabaseController.getManifestStateByID(manifestStateID);
	}

	/**
	 * Sets the date the contents of this manifest were received at the warehouse by the specified string.
	 * <blockquote>
	 * The string must be formatted as follows: <code>yyyy-MM-dd</code>
	 * </blockquote>
	 *
	 * @param dateString the new date the shipment was received as a string
	 */
	public void setReceivedDateString(final String dateString) throws ParseException
	{
		this.receivedDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
	}

	/**
	 * Sets the date the contents of this manifest were ordered by the specified string.
	 * <blockquote>
	 * The string must be formatted as follows: <code>yyyy-MM-dd</code>
	 * </blockquote>
	 *
	 * @param dateString the new date the shipment was ordered as a string
	 */
	public void setOrderedDateString(final String dateString) throws ParseException
	{
		this.orderedDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
	}
}
