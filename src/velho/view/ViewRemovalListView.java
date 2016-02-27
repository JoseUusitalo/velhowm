package velho.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import velho.controller.DatabaseController;
import velho.controller.ListController;
import velho.controller.RemovalListController;
import velho.model.RemovalList;
import velho.model.RemovalListState;

/**
 * View for creating new removal lists
 *
 * @author Jose Uusitalo
 */
public class ViewRemovalListView
{
	/**
	 * The root BorderPane for this view.
	 */
	private BorderPane bpane;

	/**
	 * The {@link RemovalListController}.
	 */
	private RemovalListController removalListController;

	/**
	 * The new removal list view.
	 */
	private BorderPane thisList;

	private RemovalList removalList;

	public ViewRemovalListView(final RemovalList removalList, final RemovalListController removalListController)
	{
		this.removalList = removalList;
		this.removalListController = removalListController;
	}

	/**
	 * Gets the removal list viewing view.
	 *
	 * @return the removal list viewing BorderPane
	 */
	public BorderPane getView()
	{

		if (bpane == null)
		{
			bpane = new BorderPane();

			final GridPane top = new GridPane();
			top.getStyleClass().add("standard-padding");

			final Button browseListsButton = new Button("Browse Removal Lists");
			browseListsButton.setAlignment(Pos.CENTER_LEFT);

			browseListsButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					removalListController.showBrowseRemovalListsView();
				}
			});

			final Label removalListLabel = new Label("Removal List #" + removalList.getDatabaseID());
			removalListLabel.getStyleClass().add("centered-title-small");
			removalListLabel.setAlignment(Pos.CENTER);
			removalListLabel.setMaxWidth(Double.MAX_VALUE);
			GridPane.setHgrow(removalListLabel, Priority.ALWAYS);

			final ComboBox<Object> removalListState = new ComboBox<Object>();
			removalListState.getItems().addAll(DatabaseController.getAllRemovalListStates());
			removalListState.getSelectionModel().select(removalList.getState());

			removalListState.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>()
			{
				@Override
				public void changed(final ObservableValue<?> observableValue, final Object oldValue, final Object newValue)
				{
					removalListController.updateRemovalListState(removalList, (RemovalListState) newValue);
				}
			});

			thisList = (BorderPane) ListController.getTableView(removalListController, DatabaseController.getProductSearchDataColumns(false, false),
					removalList.getObservableBoxes());

			// Make the list always take up the full vertical space.
			GridPane.setVgrow(thisList, Priority.ALWAYS);

			top.add(browseListsButton, 0, 0);
			top.add(removalListLabel, 1, 0);
			top.add(removalListState, 2, 0);

			bpane.setTop(top);
			bpane.setCenter(thisList);
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
		System.out.println("Refreshing removal list viewing view.");
		thisList = removalListController.getNewRemovalListView();
	}
}
