package velho.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import velho.controller.DatabaseController;
import velho.controller.ListController;
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
	 * The root BorderPane for this view.
	 */
	private BorderPane bpane;

	/**
	 * The {@link ListController}.
	 */
	private ListController listController;

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

	public RemovalListCreationView(final RemovalListController removalListController, final ListController listController,
			final SearchController searchController)
	{
		this.removalListController = removalListController;
		this.listController = listController;
		this.searchController = searchController;
	}

	/**
	 * Gets the removal list management view.
	 *
	 * @return the removal list management BorderPane
	 */
	public BorderPane getView()
	{

		if (bpane == null)
		{
			bpane = new BorderPane();
			final GridPane searchView = (GridPane) searchController.getSearchView("removal-list");
			searchView.setPadding(new Insets(0, 10, 10, 10));
			bpane.setTop(searchView);

			final GridPane left = new GridPane();
			// TODO: Use CSS.
			left.setBackground(new Background(new BackgroundFill(Paint.valueOf("EEEEEE"), null, null)));

			final Label resultsLabel = new Label("Search Results");
			resultsLabel.setAlignment(Pos.CENTER);
			resultsLabel.setMaxWidth(Double.MAX_VALUE);
			resultsLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
			left.add(resultsLabel, 0, 0);

			resultList = removalListController.getSearchResultsListView();
			resultList.setPadding(new Insets(10, 0, 0, 0));
			resultList.setPrefWidth(MainWindow.WINDOW_WIDTH / 2);
			left.add(resultList, 0, 1);

			// Make the list always take up the full vertical space.
			GridPane.setVgrow(resultList, Priority.ALWAYS);

			final GridPane center = new GridPane();
			// TODO: Use CSS.
			center.setBackground(new Background(new BackgroundFill(Paint.valueOf("EEEEEE"), null, null)));

			final Label removalListLabel = new Label("New Removal List");
			removalListLabel.setAlignment(Pos.CENTER);
			removalListLabel.setMaxWidth(Double.MAX_VALUE);
			removalListLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

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

			removalListState.getItems().addAll();
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
		System.out.println("Refreshing removal list creation view.");
		resultList = removalListController.getSearchResultsListView();
		newList = removalListController.getNewRemovalListView();
	}
}
