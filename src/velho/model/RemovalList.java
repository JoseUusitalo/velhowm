package velho.model;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import velho.controller.DatabaseController;

/**
 * A list of product boxes to be thrown away.
 *
 * @author Jose Uusitalo
 */
public class RemovalList extends AbstractDatabaseObject
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(RemovalList.class.getName());

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
	private RemovalListState state;

	/**
	 * @param databaseID
	 * @param uuid
	 * @param state
	 */
	public RemovalList(final int databaseID, final UUID uuid, final RemovalListState state)
	{
		setDatabaseID(databaseID);
		setUuid(uuid);
		this.state = state;
		this.boxes = new LinkedHashSet<ProductBox>();
		this.observableBoxes = FXCollections.observableArrayList();
	}

	/**
	 * @param databaseID
	 * @param state
	 */
	public RemovalList(final int databaseID, final RemovalListState state)
	{
		this(databaseID, UUID.randomUUID(), state);
	}

	/**
	 */
	public RemovalList()
	{
		/*
		 * I have to create a new object here because attempting to get one from the database causes a weird null
		 * pointer exception.
		 */
		this.state = new RemovalListState(1, "Active");
		setUuid(UUID.randomUUID());
		this.boxes = new LinkedHashSet<ProductBox>();
		this.observableBoxes = FXCollections.observableArrayList();
	}

	@Override
	public String toString()
	{
		return "[" + getDatabaseID() + "] " + state + ": " + boxes.size() + " boxes";
	}

	/**
	 * Gets the number of product boxes on this removal list.
	 *
	 * @return the number of boxes on this list
	 */
	public int getSize()
	{
		return boxes.size();
	}

	/**
	 * Gets the current state of this removal list.
	 *
	 * @return the state of this removal list
	 */
	public RemovalListState getState()
	{
		return state;
	}

	/**
	 * Assigns a new state this removal list.
	 *
	 * @param state the new state of this removal list
	 */
	public void setState(final RemovalListState state)
	{
		this.state = state;
	}

	/**
	 * Gets the contents of this removal list.
	 * Rebuilds the list on every method call by looping through the set of boxes.
	 *
	 * @return the {@link ProductBoxSearchResultRow} objects on this list
	 */
	public ObservableList<Object> getObservableBoxes()
	{
		observableBoxes.clear();
		boxes.forEach((final ProductBox box) -> observableBoxes.add(new ProductBoxSearchResultRow(box)));

		return observableBoxes;
	}

	/**
	 * Gets the set of actual product boxes in this removal list.
	 *
	 * @return the {@link ProductBox} objects in this removal list
	 */
	public Set<ProductBox> getBoxes()
	{
		return boxes;
	}

	/**
	 * Sets the set of actual product boxes in this removal list.
	 *
	 * @param boxes the set of boxes on this removal list, <code>null</code> to clear
	 * @return <code>true</code> if all boxes were added to this manifest
	 */
	public boolean setBoxes(final Set<ProductBox> productBoxes)
	{
		if (productBoxes == null)
			this.boxes.clear();
		else
			this.boxes = productBoxes;

		boolean bswitch = true;

		for (final ProductBox box : this.boxes)
		{
			if (box != null)
				bswitch = bswitch && (observableBoxes.add(new ProductBoxSearchResultRow(box)));
		}

		return bswitch;
	}

	/**
	 * Adds a {@link ProductBox} on this removal list.
	 *
	 * @param productBox box to add to this list
	 * @return <code>true</code> if the box was not already on this list and was added to it
	 */
	public boolean addProductBox(final ProductBox productBox)
	{
		if (boxes.add(productBox))
		{
			productBox.setRemovalList(this);
			SYSLOG.trace(productBox + " added to removal list " + this);

			return observableBoxes.add(new ProductBoxSearchResultRow(productBox));
		}

		SYSLOG.error("Failed to add " + productBox + " to removal list " + this);

		return false;
	}

	/**
	 * Removes a {@link ProductBox} from this removal list.
	 *
	 * @param productBox box to remove from this list
	 * @return <code>true</code> if the box was present on the list and was removed
	 */
	public boolean removeProductBox(final ProductBox productBox)
	{
		if (boxes.remove(productBox))
		{
			SYSLOG.trace(productBox + " removed from removal list " + this);

			productBox.setRemovalList(null);

			for (final Object row : observableBoxes)
			{
				if (((ProductBoxSearchResultRow) row).getBox().equals(productBox))
				{
					SYSLOG.trace("Observable list updated");
					return observableBoxes.remove(row);
				}
			}

			SYSLOG.warn(productBox + " was not in the observable list!");
		}

		SYSLOG.error("Failed to remove " + productBox + " from removal list " + this);

		return false;
	}

	/**
	 * Resets this removal lists state to Active and clears all boxes from it.
	 */
	public void reset()
	{
		state = DatabaseController.getRemovalListStateByID(1);
		clear();
	}

	/**
	 * Deletes all items from this removal list and also removes the reference from the {@link ProductBox} children to
	 * this list.
	 */
	public void clear()
	{
		SYSLOG.debug("Clearing removal list " + this + " of boxes: " + boxes);

		observableBoxes.clear();

		for (final ProductBox box : boxes)
		{
			box.setRemovalList(null);

			/*
			 * HIBERNATE NOTICE
			 *
			 * There is no better way of doing this at the moment.
			 * Saving collections only works when saving the children and not the parents.
			 */

			DatabaseController.saveOrUpdate(box);
		}

		boxes.clear();
	}
}
