package velho.model;

import java.util.LinkedHashSet;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * A list of product boxes to be thrown away.
 *
 * @author Jose Uusitalo
 */
public class RemovalList extends AbstractDatabaseObject implements Comparable<RemovalList>
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
	 * The current state of this removal list.
	 */
	private RemovalListState state;

	/**
	 * @param databaseID
	 * @param state
	 */
	public RemovalList(final int databaseID, final RemovalListState state)
	{
		setDatabaseID(databaseID);
		this.state = state;
		this.boxes = new LinkedHashSet<ProductBox>();
		this.observableBoxes = FXCollections.observableArrayList();
	}

	/**
	 * Creates a new empty removal list in the active state.
	 */
	public RemovalList()
	{
		/*
		 * I have to create a new object here because attempting to get one from the database causes a weird null
		 * pointer exception.
		 */
		this.state = new RemovalListState(1, "Active");
		this.boxes = new LinkedHashSet<ProductBox>();
		this.observableBoxes = FXCollections.observableArrayList();
	}

	@Override
	public String toString()
	{
		return "[" + getDatabaseID() + "] " + state + ": " + boxes.size() + " boxes";
	}

	@Override
	public boolean equals(final Object o)
	{
		if (!(o instanceof RemovalList))
			return false;

		final RemovalList pt = (RemovalList) o;

		if (this.getDatabaseID() <= 0)
			return this == pt;

		return this.getDatabaseID() == pt.getDatabaseID();
	}

	@Override
	public int compareTo(final RemovalList removallist)
	{
		return this.getDatabaseID() - removallist.getDatabaseID();
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
	 * @param boxes the set of boxes on this removal list
	 */
	public boolean setBoxes(final Set<ProductBox> boxes)
	{
		this.boxes = boxes;
		boolean bswitch = true;

		for (final ProductBox box : boxes)
			bswitch = bswitch && (observableBoxes.add(new ProductBoxSearchResultRow(box)));

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
			return observableBoxes.add(new ProductBoxSearchResultRow(productBox));

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
			for (final Object row : observableBoxes)
			{
				if (((ProductBoxSearchResultRow) row).getBox().equals(productBox))
				{
					return observableBoxes.remove(row);
				}
			}
		}

		return false;
	}

	/**
	 * Resets this removal lists state to Active and clears all boxes from it.
	 */
	public void reset()
	{
		state = new RemovalListState(1, "Active");
		boxes.clear();
		observableBoxes.clear();
	}
}
