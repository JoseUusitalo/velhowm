package velho.controller;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import velho.view.AddUserView;

/**The class that controls popup notices.
 * 
 * @author Joona
 *
 */
public class PopupController
{
	/**
	 * Displays a popup dialog with the given message.
	 * 
	 * @param stage the message to show
	 */
	public static void info(Stage stage, final String string)
	{
		System.out.println(string);
		final Stage dialog = new Stage();
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(stage);
		VBox dialogVbox = new VBox(20);
		dialogVbox.getChildren().add(new Text("User created"));
		Scene dialogScene = new Scene(dialogVbox, 300, 200);
		dialog.setScene(dialogScene);
		dialog.show();
		
	}

	//public static void popupinfo ()
	/**
	 * Displays a popup dialog with the given message.
	 * 
	 * @param msg the message to show
	 */
	public static void warning(String msg)
	{
		System.out.println(msg);	
	}
}
