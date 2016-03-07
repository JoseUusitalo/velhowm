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
public class Manifest
{
	/**
	 * The set of {@link ProductBox} objects.
	 */
	private Set<ProductBox> boxes;

	/**
	 * An {@link ObservableList} of {@link ProductBox} objects for display in the user interface.
	 */
	private ObservableList<Object> observableBoxes;

	/**
	 * The database ID of this manifest.
	 */
	private int databaseID;

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
	private Date ordered;

	/**
	 * The data the shipment was received.
	 */
	private Date received;

	/**
	 * Creates a new empty manifest in the "Received" state.
	 */
	public Manifest()
	{
		this.databaseID = -1;
		this.state = new ManifestState(3, "Received");
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
	public Manifest(final int databaseID, final ManifestState state, final int driverID, final Date ordered, final Date received)
	{
		this.databaseID = databaseID;
		this.state = state;
		this.driverID = driverID;
		this.ordered = ordered;
		this.received = received;
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
		this.databaseID = -1;
		this.state = state;
		this.driverID = driverID;
		this.ordered = ordered;
		this.received = received;
		this.boxes = new LinkedHashSet<ProductBox>();
		this.observableBoxes = FXCollections.observableArrayList();
	}

	@Override
	public String toString()
	{
		return "[" + databaseID + "] State: " + state + " Driver: " + driverID + " Ordered/Received: " + ordered.toString() + "/" + received.toString() + " ("
				+ boxes.size() + ")";
	}

	/**
	 * The database ID of this manifest.
	 *
	 * @return the database ID of this manifest
	 */
	public int getDatabaseID()
	{
		return databaseID;
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
	 * Gets the ID of the driver who delivered the contents of this manifest.
	 *
	 * @return the driver ID attached to this manifest
	 */
	public int getDriverID()
	{
		return driverID;
	}

	/**
	 * Gets the date the contents of this manifest were ordered.
	 *
	 * @return the ordered date
	 */
	public Date getOrderedDate()
	{
		return ordered;
	}

	/**
	 * Gets the date the contents of this manifest were received at the warehouse.
	 *
	 * @return the shipment received date
	 */
	public Date getReceivedDate()
	{
		return received;
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
	 * Gets the contents of this removal list.
	 *
	 * @return the {@link ProductBoxSearchResultRow} objects on this list
	 */
	public ObservableList<Object> getObservableBoxes()
	{
		return observableBoxes;
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
	 * Adds the specified {@link ProductBox} objects to this manifest.
	 *
	 * @param productBoxes set of boxes to add to this list
	 * @return <code>true</code> if all boxes were added to this manifest
	 */
	public boolean setProductBoxes(final Set<ProductBox> productBoxes)
	{
		this.boxes = productBoxes;
		boolean bswitch = true;

		for (final ProductBox box : productBoxes)
			bswitch = bswitch && (observableBoxes.add(new ProductBoxSearchResultRow(box)));

		return bswitch;
	}
}
