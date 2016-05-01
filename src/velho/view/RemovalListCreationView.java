package velho.view;

import org.apache.log4j.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import velho.controller.DatabaseController;
import velho.controller.RemovalListController;
import velho.controller.SearchController;
import velho.model.RemovalListState;

/**
 * View for creating new removal lists
 *
 * @author Jose Uusitalo
 */
public class RemovalListCreationView
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(RemovalListCreationView.class.getName());

	/**
	 * The root BorderPane for this view.
	 */
	private BorderPane bpane;

	/**
	 * The {@link RemovalListController}.
	 */
	private RemovalListController removalListController;

	/**
	 * The {@link SearchController}.
	 */
	private SearchController searchController;

	/**
	 * The new removal list view.
	 */
	private BorderPane newList;

	/**
	 * The search results list.
	 */
	private BorderPane resultList;

	/**
	 * @param removalListController
	 * @param searchController
	 */
	public RemovalListCreationView(final RemovalListController removalListController, final SearchController searchController)
	{
		this.removalListController = removalListController;
		this.searchController = searchController;
	}

	/**
	 * Gets the removal list management view.
	 *
	 * @return the removal list management BorderPane
	 * @throws NoDatabaseLinkException
	 */
	public BorderPane getView()
	{

		if (bpane == null)
		{
			bpane = new BorderPane();
			final GridPane searchView = (GridPane) searchController.getSearchView(false);
			searchView.setPadding(new Insets(0, 10, 10, 10));
			bpane.setTop(searchView);

			final GridPane left = new GridPane();

			final Label resultsLabel = new Label("Search Results");
			resultsLabel.getStyleClass().add("centered-title-medium");
			resultsLabel.setPadding(new Insets(7, 0, 0, 0));
			resultsLabel.setAlignment(Pos.CENTER);
			resultsLabel.setMaxWidth(Double.MAX_VALUE);
			left.add(resultsLabel, 0, 0);

			resultList = removalListController.getSearchResultsListView();
			resultList.setPadding(new Insets(10, 0, 0, 0));
			resultList.setPrefWidth(MainWindow.WINDOW_WIDTH / 2);
			left.add(resultList, 0, 1);

			// Make the list always take up the full vertical space.
			GridPane.setVgrow(resultList, Priority.ALWAYS);

			final GridPane center = new GridPane();

			final Label removalListLabel = new Label("New Removal List");
			removalListLabel.getStyleClass().add("centered-title-medium");
			removalListLabel.setAlignment(Pos.CENTER);
			removalListLabel.setMaxWidth(Double.MAX_VALUE);

			GridPane.setHgrow(removalListLabel, Priority.ALWAYS);

			newList = removalListController.getNewRemovalListView();
			newList.setPadding(new Insets(10, 0, 0, 0));
			newList.setPrefWidth(MainWindow.WINDOW_WIDTH / 2);
			center.add(removalListLabel, 1, 0);
			center.add(newList, 0, 1, 3, 1);

			final ComboBox<Object> removalListState = new ComboBox<Object>();

			removalListState.getItems().addAll(DatabaseController.getAllRemovalListStates());

			center.add(removalListState, 0, 0);

			removalListState.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>()
			{
				@Override
				public void changed(final ObservableValue<?> observableValue, final Object oldValue, final Object newValue)
				{
					removalListController.setNewRemovalListState((RemovalListState) newValue);
				}
			});

			final Button saveButton = new Button("Save");
			center.add(saveButton, 2, 0);

			saveButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					removalListController.saveNewRemovalList();
				}
			});

			// Make the list always take up the full vertical space.
			GridPane.setVgrow(newList, Priority.ALWAYS);

			removalListState.getSelectionModel().selectFirst();

			bpane.setLeft(left);
			bpane.setCenter(center);
		}

		return bpane;
	}

	/**
	 * Destroys the view.
	 */
	public void destroy()
	{
		bpane = null;
	}

	/**
	 * Gets the search results list and the current new removal list views again.
	 */
	public void refresh()
	{
		SYSLOG.trace("Refreshing removal list creation view.");
		resultList = removalListController.getSearchResultsListView();
		newList = removalListController.getNewRemovalListView();
	}
}
