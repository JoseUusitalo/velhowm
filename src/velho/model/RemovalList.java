package velho.model;

import java.util.LinkedHashSet;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import velho.controller.DatabaseController;
import velho.model.exceptions.NoDatabaseLinkException;

/**
 * A list of product boxes to be thrown away.
 *
 * @author Jose Uusitalo
 */
public class RemovalList
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
	 * The database ID of this removal list.
	 */
	private int databaseID;

	/**
	 * The current state of this removal list.
	 */
	private RemovalListState state;

	/**
	 * Creates a new empty removal list in the active state.
	 */
	public RemovalList()
	{
		this.state = new RemovalListState(1, "Active");
		this.boxes = new LinkedHashSet<ProductBox>();
		this.observableBoxes = FXCollections.observableArrayList();
	}

	/**
	 * @param databaseID
	 * @param state
	 */
	public RemovalList(final int databaseID, final RemovalListState state)
	{
		this.databaseID = databaseID;
		this.state = state;
		this.boxes = new LinkedHashSet<ProductBox>();
		this.observableBoxes = FXCollections.observableArrayList();
	}

	@Override
	public String toString()
	{
		return "[" + databaseID + "] " + state + ": " + boxes.size() + " boxes";
	}

	/**
	 * The database ID of this removal list.
	 *
	 * @return the database ID of this list
	 */
	public int getDatabaseID()
	{
		return databaseID;
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
	 *
	 * @return the {@link ProductBoxSearchResultRow} objects on this list
	 */
	public ObservableList<Object> getObservableBoxes()
	{
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
	 * Updates the database table with the most recent data on this list.
	 *
	 * @return <code>true</code> if update was successfull
	 */
	public boolean saveToDatabase() throws NoDatabaseLinkException
	{
		return DatabaseController.updateRemovalList(this);
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
			return observableBoxes.add(new ProductBoxSearchResultRow(productBox));
		}
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
