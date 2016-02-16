package velho.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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
			bpane.setTop(searchController.getView());

			VBox left = new VBox(10);
			// TODO: Use CSS.
			left.setBackground(new Background(new BackgroundFill(Paint.valueOf("EEEEEE"), null, null)));
			left.setPadding(new Insets(10, 0, 0, 0));

			Label resultsLabel = new Label("Search Results");
			resultsLabel.setAlignment(Pos.CENTER);
			resultsLabel.setMaxWidth(Double.MAX_VALUE);
			resultsLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

			BorderPane resultList = removalListController.getSearchResults();
			resultList.setPrefWidth(MainWindow.WINDOW_WIDTH / 2);
			left.getChildren().addAll(resultsLabel, resultList);

			// Make the list always take up the full vertical space.
			VBox.setVgrow(resultList, Priority.ALWAYS);

			VBox center = new VBox(10);
			// TODO: Use CSS.
			center.setBackground(new Background(new BackgroundFill(Paint.valueOf("EEEEEE"), null, null)));
			center.setPadding(new Insets(10, 0, 0, 0));

			Label removalListLabel = new Label("New Removal List");
			removalListLabel.setAlignment(Pos.CENTER);
			removalListLabel.setMaxWidth(Double.MAX_VALUE);
			removalListLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

			BorderPane newList = removalListController.getCurrentRemovalListView();
			newList.setPrefWidth(MainWindow.WINDOW_WIDTH / 2);
			center.getChildren().addAll(removalListLabel, newList);

			// Make the list always take up the full vertical space.
			VBox.setVgrow(newList, Priority.ALWAYS);

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
