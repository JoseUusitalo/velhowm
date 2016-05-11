package velho.view;

import java.io.File;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import velho.controller.CSVController;
import velho.controller.PopupController;
import velho.controller.UIController;
import velho.model.AbstractDatabaseObject;
import velho.model.interfaces.GenericView;

public class CSVLoadView implements GenericView
{
	/**
	 * The root node.
	 */
	private GridPane root;

	/**
	 * This view's controller.
	 */
	private CSVController csvController;

	/**
	 * The primary stage of the main window.
	 */
	private Stage primaryStage;

	/**
	 * A list of classes that can be saved and loaded from the database.
	 */
	private ObservableList<Class<? extends AbstractDatabaseObject>> validDatabaseClasses;

	/**
	 * @param csvController
	 * @param validDatabaseClasses
	 */
	public CSVLoadView(final CSVController csvController, final ObservableList<Class<? extends AbstractDatabaseObject>> validDatabaseClasses)
	{
		this.csvController = csvController;
		this.root = null;
		this.validDatabaseClasses = validDatabaseClasses;
	}

	public GridPane getView(final Stage mainStage)
	{
		if (root == null)
		{
			this.primaryStage = mainStage;

			root = new GridPane();
			root.setAlignment(Pos.CENTER);
			root.setHgap(10);
			root.setVgap(10);

			final Label loadLabel = new Label("Load CSV File:");
			root.add(loadLabel, 0, 0);

			final TextField fileNameField = new TextField();
			root.add(fileNameField, 1, 0);

			final FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Load CSV File");
			fileChooser.setInitialDirectory(new File("./"));

			final ComboBox<Class<? extends AbstractDatabaseObject>> typeSelector = new ComboBox<Class<? extends AbstractDatabaseObject>>();
			typeSelector.setItems(validDatabaseClasses);
			root.add(typeSelector, 3, 0);

			final Button loadButton = new Button("Load to Database");
			loadButton.setDisable(true);

			loadButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent e)
				{
					csvController.loadCSVFileToDatabase(fileNameField.getText(), typeSelector.getValue());
					fileNameField.clear();
					loadButton.setDisable(true);
				}
			});

			final Button selectButton = new Button("Select CSV File...");
			root.add(selectButton, 2, 0);
			root.add(loadButton, 4, 0);

			selectButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent e)
				{
					final File csvFile = fileChooser.showOpenDialog(primaryStage);

					if (CSVController.isValidCSVFile(csvFile))
					{
						fileNameField.setText(csvFile.getAbsolutePath());

						if (typeSelector.getValue() != null)
							loadButton.setDisable(false);
					}
					else
					{
						loadButton.setDisable(true);
						fileNameField.clear();
						PopupController.warning("Not a CSV file: " + csvFile.getAbsolutePath());
					}
				}
			});

			typeSelector.valueProperty().addListener(new ChangeListener<Class<? extends AbstractDatabaseObject>>()
			{
				@Override
				public void changed(final ObservableValue<? extends Class<? extends AbstractDatabaseObject>> observable,
						final Class<? extends AbstractDatabaseObject> oldValue, final Class<? extends AbstractDatabaseObject> newValue)
				{
					if (newValue == null || fileNameField.getText().isEmpty())
						loadButton.setDisable(true);
					else if (!fileNameField.getText().isEmpty())
						loadButton.setDisable(false);
				}
			});

			UIController.recordView(this);
		}

		return root;
	}

	@Override
	public void recreate()
	{
		getView(this.primaryStage);
	}

	@Override
	public void destroy()
	{
		root = null;
	}
}
