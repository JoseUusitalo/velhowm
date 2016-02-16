package velho.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import velho.controller.ListController;
import velho.controller.RemovalListController;

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

	public RemovalListCreationView(final RemovalListController removalListController, final ListController listController)
	{
		this.removalListController = removalListController;
		this.listController = listController;
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

			Label temp = new Label("<Insert Product Search Here>");
			temp.setAlignment(Pos.CENTER);
			temp.setPadding(new Insets(20));

			bpane.setTop(temp);
			VBox left = new VBox(10);

			Label resultsLabel = new Label("Search Results");
			resultsLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

			BorderPane resultList = (BorderPane) listController.getProductSearchResultsView();
			resultList.setPrefWidth(MainWindow.WINDOW_WIDTH / 2);
			left.getChildren().addAll(resultsLabel, resultList);

			// Make the list always take up the full vertical space.
			VBox.setVgrow(resultList, Priority.ALWAYS);

			VBox center = new VBox(10);
			Label removalListLabel = new Label("New Removal List");
			removalListLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));

			BorderPane newList = (BorderPane) removalListController.getCurrentRemovalListView();
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
