package velho.view;

import java.io.File;

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

public class CSVView implements GenericView
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
	public CSVView(final CSVController csvController, final ObservableList<Class<? extends AbstractDatabaseObject>> validDatabaseClasses)
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

			final ComboBox<Class<? extends AbstractDatabaseObject>> typeSelector = new ComboBox<Class<? extends AbstractDatabaseObject>>();
			typeSelector.setItems(validDatabaseClasses);

			final FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Load CSV File");
			fileChooser.setInitialDirectory(new File("./"));

			final Button loadButton = new Button("Load to Database");
			loadButton.setDisable(true);

			loadButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent e)
				{
					csvController.loadCSVFileToDatabase(fileNameField.getText(), typeSelector.getValue());
				}
			});

			final Button selectButton = new Button("Select CSV File...");
			root.add(selectButton, 2, 0);
			root.add(loadButton, 3, 0);

			selectButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent e)
				{
					final File csvFile = fileChooser.showOpenDialog(primaryStage);

					if (CSVController.isValidCSVFile(csvFile))
					{
						loadButton.setDisable(false);
						fileNameField.setText(csvFile.getAbsolutePath());
					}
					else
					{
						loadButton.setDisable(true);
						fileNameField.setText("");
						PopupController.warning("Not a CSV file: " + csvFile.getAbsolutePath());
					}
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
