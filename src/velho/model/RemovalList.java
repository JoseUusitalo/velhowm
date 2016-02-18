package velho.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import velho.controller.DatabaseController;

public class RemovalList
{
	private ObservableSet<Object> boxes;
	private int databaseID;
	private RemovalListState state;

	public RemovalList()
	{
		this.state = new RemovalListState(1, "Active");
		this.boxes = FXCollections.observableSet();
	}

	public RemovalList(final int databaseID, final RemovalListState state)
	{
		this.databaseID = databaseID;
		this.state = state;
		this.boxes = FXCollections.observableSet();
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
	 * @return the {@link ProductBox} objects on this list
	 */
	public ObservableSet<Object> getBoxes()
	{
		return boxes;
	}

	/**
	 * Updates the database table with the most recent data on this list.
	 *
	 * @return <code>true</code> if update was successfull
	 */
	public boolean updateDatabase()
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
		return boxes.add(productBox);
	}
}
