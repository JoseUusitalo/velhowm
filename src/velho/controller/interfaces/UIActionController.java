package velho.controller.interfaces;

import velho.view.ListView;

/**
 * Controllers that can handle actions performed in the UI that modify objects
 * in memory or in the database.
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
	void createAction(final Object data);

	/**
	 * An action performed in the UI in the context of updating an existing
	 * object's data.
	 *
	 * @param data data to update to
	 */
	void updateAction(final Object data);

	/**
	 * An action performed in the UI in the context of adding data to existing
	 * object.
	 *
	 * @param data data to add
	 */
	void addAction(final Object data);

	/**
	 * An action performed in the UI in the context of remove data from an
	 * existing object.
	 *
	 * @param data data to remove
	 */
	void removeAction(final Object data);

	/**
	 * An action performed in the UI in the context of deleting an existing
	 * object.
	 *
	 * @param data data to delete
	 */
	void deleteAction(final Object data);

	/**
	 * An action performed in the UI in the context of viewing an existing
	 * object.
	 *
	 * @param data data to delete
	 */
	void viewAction(final Object data);

	/**
	 * Receates the views created by this UI action controller.
	 *
	 * @param listView the {@link ListView} that called for recreation (optional)
	 */
	void recreateViews(final ListView listView);
}
