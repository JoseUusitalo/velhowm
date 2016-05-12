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
import velho.controller.LocalizationController;
import velho.controller.UIController;
import velho.model.interfaces.DatabaseObject;
import velho.model.interfaces.GenericView;

public class CSVWriteView implements GenericView
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
	private ObservableList<Class<? extends DatabaseObject>> validDatabaseClasses;

	/**
	 * @param csvController
	 * @param validDatabaseClasses
	 */
	public CSVWriteView(final CSVController csvController, final ObservableList<Class<? extends DatabaseObject>> validDatabaseClasses)
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

			final Label saveLabel = new Label(LocalizationController.getInstance().getString("writeDatabaseContentsLabel"));

			final TextField fileNameField = new TextField();

			final FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle(LocalizationController.getInstance().getString("writeCSVFileTitle"));
			fileChooser.setInitialDirectory(new File("./"));
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File (*.csv)", "*.csv"));

			final Label typeLabel = new Label(LocalizationController.getInstance().getString("dataBaseContentsLabel"));

			final ComboBox<Class<? extends DatabaseObject>> typeSelector = new ComboBox<Class<? extends DatabaseObject>>();
			typeSelector.setItems(validDatabaseClasses);

			final Button writeButton = new Button(LocalizationController.getInstance().getString("writeButton"));
			writeButton.setDisable(true);

			writeButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent e)
				{
					csvController.writeDatabaseTableToCSVFile(fileNameField.getText(), typeSelector.getValue());
					fileNameField.clear();
					writeButton.setDisable(true);
				}
			});

			final Button selectButton = new Button(LocalizationController.getInstance().getString("selectCSVFileButton"));

			selectButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent e)
				{
					final File csvFile = fileChooser.showSaveDialog(primaryStage);
					fileNameField.setText(csvFile.getAbsolutePath());

					if (typeSelector.getValue() != null)
						writeButton.setDisable(false);
				}
			});

			typeSelector.valueProperty().addListener(new ChangeListener<Class<? extends DatabaseObject>>()
			{
				@Override
				public void changed(final ObservableValue<? extends Class<? extends DatabaseObject>> observable, final Class<? extends DatabaseObject> oldValue,
						final Class<? extends DatabaseObject> newValue)
				{
					if (newValue == null || fileNameField.getText().isEmpty())
						writeButton.setDisable(true);
					else if (!fileNameField.getText().isEmpty())
						writeButton.setDisable(false);
				}
			});

			root.add(saveLabel, 0, 0);
			root.add(fileNameField, 1, 0);
			root.add(selectButton, 2, 0);
			root.add(typeLabel, 0, 1);
			root.add(typeSelector, 1, 1);
			root.add(writeButton, 2, 1);

			UIController.getInstance().recordView(this);
		}

		return root;
	}

	@Override
	public void recreate()
	{
		root = null;
		getView(this.primaryStage);
	}

	@Override
	public void destroy()
	{
		root = null;
	}
}
