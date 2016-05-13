package velho.view;

import org.apache.log4j.Logger;

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
import velho.controller.ListController;
import velho.controller.LocalizationController;
import velho.controller.RemovalListController;
import velho.controller.UIController;
import velho.controller.database.DatabaseController;
import velho.model.RemovalList;
import velho.model.RemovalListState;
import velho.model.interfaces.GenericView;

/**
 * View for creating new removal lists
 *
 * @author Jose Uusitalo
 */
public class RemovalListView implements GenericView
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(RemovalListView.class.getName());

	/**
	 * The root BorderPane for this view.
	 */
	private BorderPane bpane;

	/**
	 * The new removal list view.
	 */
	private BorderPane thisList;

	/**
	 * The {@link RemovalList} to display.
	 */
	private final RemovalList removalList;

	/**
	 * @param removalList
	 */
	public RemovalListView(final RemovalList removalList)
	{
		this.removalList = removalList;
	}

	/**
	 * Gets the removal list viewing view.
	 *
	 * @return the removal list viewing BorderPane
	 * @throws NoDatabaseLinkException
	 */
	public BorderPane getView()
	{
		if (bpane == null)
		{
			bpane = new BorderPane();

			final GridPane top = new GridPane();
			top.getStyleClass().add("standard-padding");

			final Button browseListsButton = new Button(LocalizationController.getInstance().getString("browseRemovalListsButton"));
			browseListsButton.setAlignment(Pos.CENTER_LEFT);

			browseListsButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent event)
				{
					RemovalListController.getInstance().showBrowseRemovalListsView();
				}
			});

			final Label removalListLabel = new Label(LocalizationController.getInstance().getString("removalListLabel") + removalList.getDatabaseID());
			removalListLabel.getStyleClass().add("centered-title-small");
			removalListLabel.setAlignment(Pos.CENTER);
			removalListLabel.setMaxWidth(Double.MAX_VALUE);
			GridPane.setHgrow(removalListLabel, Priority.ALWAYS);

			final ComboBox<Object> removalListState = new ComboBox<Object>();
			removalListState.getItems().addAll(DatabaseController.getInstance().getAllRemovalListStates());
			removalListState.getSelectionModel().select(removalList.getState());

			removalListState.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>()
			{
				@Override
				public void changed(final ObservableValue<?> observableValue, final Object oldValue, final Object newValue)
				{
					RemovalListController.getInstance().updateRemovalListState(removalList, (RemovalListState) newValue);
				}
			});

			thisList = (BorderPane) ListController.getTableView(RemovalListController.getInstance(),
					DatabaseController.getInstance().getProductSearchDataColumns(false, false), removalList.getObservableBoxes());

			// Make the list always take up the full vertical space.
			GridPane.setVgrow(thisList, Priority.ALWAYS);

			top.add(browseListsButton, 0, 0);
			top.add(removalListLabel, 1, 0);
			top.add(removalListState, 2, 0);

			bpane.setTop(top);
			bpane.setCenter(thisList);
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
		thisList = null;
		getView();
	}

	/**
	 * Gets the search results list and the current new removal list views
	 * again.
	 */
	public void refresh()
	{
		SYSLOG.trace("Refreshing removal list viewing view.");
		thisList = RemovalListController.getInstance().getNewRemovalListView();
	}

	@Override
	public void destroy()
	{
		bpane = null;
	}
}
