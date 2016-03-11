package velho.controller.interfaces;

/**
 * Controllers that can handle actions performed in the UI that modify objects in memory or in the database.
 *
 * @author Jose Uusitalo
 */
public interface UIActionController
{
	/**
	 * An action performed in the UI in the context of creating a new object.
	 *
	 * @param data data to create
	 */
	public void createAction(final Object data);

	/**
	 * An action performed in the UI in the context of updating an existing object's data.
	 *
	 * @param data data to update to
	 */
	public void updateAction(final Object data);

	/**
	 * An action performed in the UI in the context of adding data to existing object.
	 *
	 * @param data data to add
	 */
	public void addAction(final Object data);

	/**
	 * An action performed in the UI in the context of remove data from an existing object.
	 *
	 * @param data data to remove
	 */
	public void removeAction(final Object data);

	/**
	 * An action performed in the UI in the context of deleting an existing object.
	 *
	 * @param data data to delete
	 */
	public void deleteAction(final Object data);

	/**
	 * An action performed in the UI in the context of viewing an existing object.
	 *
	 * @param data data to delete
	 */
	public void viewAction(final Object data);
}
