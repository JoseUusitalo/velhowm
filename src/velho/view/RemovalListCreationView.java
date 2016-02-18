package velho.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import velho.controller.ListController;
import velho.controller.RemovalListController;
import velho.controller.SearchController;

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
	 * The grid panel.
	 */
	private GridPane grid;

	public RemovalListCreationView(final RemovalListController removalListController, final ListController listController, final SearchController searchController)
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
			final ComboBox<String> removalListState = new ComboBox<String>();

			bpane = new BorderPane();
			GridPane searchView = (GridPane) searchController.getSearchView();
			searchView.setPadding(new Insets(0, 10, 10, 10));
			bpane.setTop(searchView);

			GridPane left = new GridPane();
			// TODO: Use CSS.
			left.setBackground(new Background(new BackgroundFill(Paint.valueOf("EEEEEE"), null, null)));

			Label resultsLabel = new Label("Search Results");
			resultsLabel.setAlignment(Pos.CENTER);
			resultsLabel.setMaxWidth(Double.MAX_VALUE);
			resultsLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
			left.add(resultsLabel, 0, 0);

			BorderPane resultList = removalListController.getSearchResults();
			resultList.setPrefWidth(MainWindow.WINDOW_WIDTH / 2);
			left.add(resultList, 0, 1);

			// Make the list always take up the full vertical space.
			GridPane.setVgrow(resultList, Priority.ALWAYS);

			GridPane center = new GridPane();
			// TODO: Use CSS.
			center.setBackground(new Background(new BackgroundFill(Paint.valueOf("EEEEEE"), null, null)));

			Label removalListLabel = new Label("New Removal List");
			removalListLabel.setAlignment(Pos.CENTER);
			removalListLabel.setMaxWidth(Double.MAX_VALUE);
			removalListLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

			GridPane.setHgrow(removalListLabel, Priority.ALWAYS);

			BorderPane newList = removalListController.getCurrentRemovalListView();
			newList.setPrefWidth(MainWindow.WINDOW_WIDTH / 2);
			center.add(removalListLabel, 0, 0);
			center.add(newList, 0, 1, 2, 1);
			center.add(removalListState, 1, 0);

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
}
