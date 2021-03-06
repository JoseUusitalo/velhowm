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
import velho.controller.LocalizationController;
import velho.controller.RemovalListController;
import velho.controller.SearchController;
import velho.controller.UIController;
import velho.controller.database.DatabaseController;
import velho.model.RemovalListState;
import velho.model.interfaces.GenericView;

/**
 * View for creating new removal lists
 *
 * @author Jose Uusitalo
 */
public class RemovalListCreationView implements GenericView
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
	 * The new removal list view.
	 */
	private BorderPane newList;

	/**
	 * The search results list.
	 */
	private BorderPane resultList;

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
			final GridPane searchView = (GridPane) SearchController.getInstance().getSearchView(false);
			searchView.setPadding(new Insets(0, 10, 10, 10));
			bpane.setTop(searchView);

			final GridPane left = new GridPane();

			final Label resultsLabel = new Label(LocalizationController.getInstance().getString("searchResultsLabel"));
			resultsLabel.getStyleClass().add("centered-title-medium");
			resultsLabel.setPadding(new Insets(7, 0, 0, 0));
			resultsLabel.setAlignment(Pos.CENTER);
			resultsLabel.setMaxWidth(Double.MAX_VALUE);
			left.add(resultsLabel, 0, 0);

			resultList = RemovalListController.getInstance().getSearchResultsListView();
			resultList.setPadding(new Insets(10, 0, 0, 0));
			resultList.setPrefWidth(MainWindow.WINDOW_WIDTH / 2);
			left.add(resultList, 0, 1);

			// Make the list always take up the full vertical space.
			GridPane.setVgrow(resultList, Priority.ALWAYS);

			final GridPane center = new GridPane();

			final Label removalListLabel = new Label(LocalizationController.getInstance().getString("newRemovalListLabel"));
			removalListLabel.getStyleClass().add("centered-title-medium");
			removalListLabel.setAlignment(Pos.CENTER);
			removalListLabel.setMaxWidth(Double.MAX_VALUE);

			GridPane.setHgrow(removalListLabel, Priority.ALWAYS);

			newList = RemovalListController.getInstance().getNewRemovalListView();
			newList.setPadding(new Insets(10, 0, 0, 0));
			newList.setPrefWidth(MainWindow.WINDOW_WIDTH / 2);
			center.add(removalListLabel, 1, 0);
			center.add(newList, 0, 1, 3, 1);

			final ComboBox<Object> removalListState = new ComboBox<Object>();

			removalListState.getItems().addAll(DatabaseController.getInstance().getAllRemovalListStates());

			center.add(removalListState, 0, 0);

			removalListState.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>()
			{
				@Override
				public void changed(final ObservableValue<?> observableValue, final Object oldValue, final Object newValue)
				{
					RemovalListController.getInstance().setNewRemovalListState((RemovalListState) newValue);
				}
			});

			final Button saveButton = new Button(LocalizationController.getInstance().getString("saveButton"));
			center.add(saveButton, 2, 0);

			saveButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					RemovalListController.getInstance().saveNewRemovalList();
				}
			});

			// Make the list always take up the full vertical space.
			GridPane.setVgrow(newList, Priority.ALWAYS);

			removalListState.getSelectionModel().selectFirst();

			bpane.setLeft(left);
			bpane.setCenter(center);
			UIController.getInstance().recordView(this);
		}

		return bpane;
	}

	/**
	 * Destroys the view.
	 */
	@Override
	public void recreate()
	{
		bpane = null;
		resultList = null;
		newList = null;
		getView();
	}

	/**
	 * Gets the search results list and the current new removal list views
	 * again.
	 */
	public void refresh()
	{
		SYSLOG.trace(LocalizationController.getInstance().getString("refreshRemovalListNotice"));
		resultList = RemovalListController.getInstance().getSearchResultsListView();
		newList = RemovalListController.getInstance().getNewRemovalListView();
	}

	@Override
	public void destroy()
	{
		bpane = null;
	}
}
