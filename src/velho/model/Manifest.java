package velho.model;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A list of {@link ProductBox} objects that have been delivered to the warehouse from somewhere else.
 *
 * @author Jose Uusitalo
 */
public class Manifest extends AbstractDatabaseObject implements Comparable<Manifest>
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
	 * @param state
	 * @param driverID
	 * @param ordered
	 * @param received
	 */
	public Manifest(final int databaseID, final ManifestState state, final int driverID, final Date ordered, final Date received)
	{
		setDatabaseID(databaseID);
		this.state = state;
		this.driverID = driverID;
		this.orderedDate = ordered;
		this.receivedDate = received;
		this.boxes = new LinkedHashSet<ProductBox>();
		this.observableBoxes = FXCollections.observableArrayList();
	}

	/**
	 * Creates a new manifest with the given data that does not exist in the database.
	 *
	 * @param state
	 * @param driverID
	 * @param ordered
	 * @param received
	 */
	public Manifest(final ManifestState state, final int driverID, final Date ordered, final Date received)
	{
		this(0, state, driverID, ordered, received);
	}

	/**
	 */
	public Manifest()
	{
		// For Hibernate.
		this.boxes = new LinkedHashSet<ProductBox>();
		this.observableBoxes = FXCollections.observableArrayList();
	}

	@Override
	public String toString()
	{
		return "[" + getDatabaseID() + "] State: " + state + " Driver: " + driverID + " Ordered/Received: " + orderedDate.toString() + "/"
				+ receivedDate.toString() + " (" + boxes.size() + ")";
	}

	@Override
	public boolean equals(final Object o)
	{
		if (!(o instanceof Manifest))
			return false;

		final Manifest m = (Manifest) o;

		if (this.getDatabaseID() <= 0)
			return this == m;

		return this.getDatabaseID() == m.getDatabaseID();
	}

	@Override
	public int compareTo(final Manifest manifest)
	{
		return this.getDatabaseID() - manifest.getDatabaseID();
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
	 * @param productBoxes set of boxes to add to this list
	 * @return <code>true</code> if all boxes were added to this manifest
	 */
	public boolean setBoxes(final Set<ProductBox> productBoxes)
	{
		this.boxes = productBoxes;
		boolean bswitch = true;

		for (final ProductBox box : productBoxes)
			bswitch = bswitch && (observableBoxes.add(new ProductBoxSearchResultRow(box)));

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
}
