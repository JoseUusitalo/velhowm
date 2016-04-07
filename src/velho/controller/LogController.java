package velho.controller;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import javafx.scene.Node;
import velho.model.exceptions.NoDatabaseLinkException;
import velho.view.LogView;
import velho.view.MainWindow;

/**
 * A class for controlling viwing logs.
 *
 * @author Jose Uusitalo
 */
public class LogController
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(MainWindow.class.getName());

	/**
	 * The {@link LogView}.
	 */
	private LogView logView;

	/**
	 */
	public LogController()
	{
		logView = new LogView(this);
	}

	/**
	 * Gets the view for reading system and user logs.
	 *
	 * @return the log reading view
	 */
	public Node getView()
	{
		return logView.getView();
	}

	/**
	 * Gets the full system log from the database.
	 *
	 * @return the system log
	 */
	@SuppressWarnings("static-method")
	public String getSystemLog()
	{
		StringBuilder sb = new StringBuilder();
		ArrayList<Object> log = new ArrayList<Object>();

		SYSLOG.info(LocalizationController.getString("systemLogLoadingNotification"));
		try
		{
			log = LogDatabaseController.getSystemLog();
		}
		catch (NoDatabaseLinkException e)
		{
			LogDatabaseController.tryReLink();
		}

		for (Object line : log)
		{
			sb.append(line.toString());
			sb.append("\n");
		}

		return sb.toString();
	}

	/**
	 * Gets the full user log from the database.
	 *
	 * @return the user log
	 */
	@SuppressWarnings("static-method")
	public String getUserLog()
	{
		StringBuilder sb = new StringBuilder();
		ArrayList<Object> log = new ArrayList<Object>();

		SYSLOG.info(LocalizationController.getString("userLogLoadingNotification"));
		try
		{
			log = LogDatabaseController.getUserLog();
		}
		catch (NoDatabaseLinkException e)
		{
			LogDatabaseController.tryReLink();
		}

		for (Object line : log)
		{
			sb.append(line.toString());
			sb.append("\n");
		}

		return sb.toString();
	}

	/**
	 * Refreshes the displayed logs.
	 */
	public void refresh()
	{
		logView.refresh();
	}
}
