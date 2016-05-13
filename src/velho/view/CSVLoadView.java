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
import velho.controller.PopupController;
import velho.controller.UIController;
import velho.model.interfaces.DatabaseObject;
import velho.model.interfaces.GenericView;

public class CSVLoadView implements GenericView
{
	/**
	 * The root node.
	 */
	private GridPane root;

	/**
	 * The primary stage of the main window.
	 */
	private Stage primaryStage;

	/**
	 * A list of classes that can be saved and loaded from the database.
	 */
	private ObservableList<Class<? extends DatabaseObject>> validDatabaseClasses;

	/**
	 * @param validDatabaseClasses
	 */
	public CSVLoadView(final ObservableList<Class<? extends DatabaseObject>> validDatabaseClasses)
	{
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

			final Label loadLabel = new Label(LocalizationController.getInstance().getString("loadCSVFileLabel"));
			root.add(loadLabel, 0, 0);

			final TextField fileNameField = new TextField();
			root.add(fileNameField, 1, 0);

			final FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle(LocalizationController.getInstance().getString("loadCSVFileTitle"));
			fileChooser.setInitialDirectory(new File("./"));

			final ComboBox<Class<? extends DatabaseObject>> typeSelector = new ComboBox<Class<? extends DatabaseObject>>();
			typeSelector.setItems(validDatabaseClasses);
			root.add(typeSelector, 3, 0);

			final Button loadButton = new Button(LocalizationController.getInstance().getString("loadToDatabaseButton"));
			loadButton.setDisable(true);

			loadButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent e)
				{
					CSVController.getInstance().loadCSVFileToDatabase(fileNameField.getText(), typeSelector.getValue());
					fileNameField.clear();
					loadButton.setDisable(true);
				}
			});

			final Button selectButton = new Button(LocalizationController.getInstance().getString("selectCSVFileButton"));
			root.add(selectButton, 2, 0);
			root.add(loadButton, 4, 0);

			selectButton.setOnAction(new EventHandler<ActionEvent>()
			{
				@Override
				public void handle(final ActionEvent e)
				{
					final File csvFile = fileChooser.showOpenDialog(primaryStage);

					if (CSVController.getInstance().isValidCSVFile(csvFile))
					{
						fileNameField.setText(csvFile.getAbsolutePath());

						if (typeSelector.getValue() != null)
							loadButton.setDisable(false);
					}
					else
					{
						loadButton.setDisable(true);
						fileNameField.clear();
						PopupController.getInstance()
								.warning(LocalizationController.getInstance().getCompoundString("notACSVFileNotice", csvFile.getAbsolutePath()));
					}
				}
			});

			typeSelector.valueProperty().addListener(new ChangeListener<Class<? extends DatabaseObject>>()
			{
				@Override
				public void changed(final ObservableValue<? extends Class<? extends DatabaseObject>> observable, final Class<? extends DatabaseObject> oldValue,
						final Class<? extends DatabaseObject> newValue)
				{
					if (newValue == null || fileNameField.getText().isEmpty())
						loadButton.setDisable(true);
					else if (!fileNameField.getText().isEmpty())
						loadButton.setDisable(false);
				}
			});

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
