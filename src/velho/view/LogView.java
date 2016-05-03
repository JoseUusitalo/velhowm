package velho.view;

import org.apache.log4j.Logger;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import velho.controller.LogController;
import velho.controller.UIController;
import velho.model.interfaces.GenericView;

/**
 * View for reading logs.
 *
 * @author Jose Uusitalo
 */
public class LogView implements GenericView
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(LogView.class.getName());

	/**
	 * The root BorderPane for this view.
	 */
	private BorderPane bpane;

	/**
	 * The system log.
	 */
	private Text syslog;

	/**
	 * The user actions log.
	 */
	private Text usrlog;

	/**
	 * The {@link LogController}
	 */
	private final LogController logController;

	/**
	 * @param logController
	 */
	public LogView(final LogController logController)
	{
		this.logController = logController;
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
			ScrollPane leftlog = new ScrollPane();
			leftlog.setFitToHeight(true);
			leftlog.prefWidthProperty().bind(MainWindow.widthProperty.divide(2.0));
			syslog = new Text(logController.getSystemLog());
			leftlog.setFitToWidth(true);
			leftlog.setContent(syslog);

			ScrollPane rightlog = new ScrollPane();
			rightlog.setFitToHeight(true);
			usrlog = new Text(logController.getUserLog());
			rightlog.setFitToWidth(true);
			rightlog.setContent(usrlog);

			bpane.setLeft(leftlog);
			bpane.setCenter(rightlog);
			UIController.recordView(this);
		}

		return bpane;
	}

	@Override
	public void recreate()
	{
		bpane = null;
		getView();
	}

	/**
	 * Refreshes the log view.
	 */
	public void refresh()
	{
		SYSLOG.trace("Refreshing logs view.");
		syslog = new Text(logController.getSystemLog());
		usrlog = new Text(logController.getUserLog());
	}

	@Override
	public void destroy()
	{
		bpane = null;
	}
}
