package velho.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RemovalList
{
	private ObservableList<Object> list;

	/**
	 * @param list
	 */
	public RemovalList()
	{
		this.list = FXCollections.observableArrayList();
	}

	public ObservableList<Object> getObservableList()
	{
		return list;
	}

}
