package velho.controller;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import javafx.scene.Node;
import velho.controller.database.LogDatabaseController;
import velho.view.LogView;

/**
 * The singleton controller for handling the viewing of logs.
 *
 * @author Jose Uusitalo
 */
public class LogController
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(LogController.class.getName());

	/**
	 * The {@link LogView}.
	 */
	private final LogView logView;

	/**
	 * A private inner class holding the class instance.
	 *
	 * @author Jose Uusitalo
	 */
	private static class Holder
	{
		/**
		 * The only instance of {@link LogController}.
		 */
		private static final LogController INSTANCE = new LogController();
	}

	/**
	 */
	private LogController()
	{
		logView = new LogView();
	}

	/**
	 * Gets the instance of the {@link LogController}.
	 *
	 * @return the log controller
	 */
	public static synchronized LogController getInstance()
	{
		return Holder.INSTANCE;
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
		final StringBuilder strbuilder = new StringBuilder();
		ArrayList<Object> log = new ArrayList<Object>();

		SYSLOG.info("Loading the full system log.");

		try
		{
			log = LogDatabaseController.getInstance().getSystemLog();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		for (Object line : log)
			strbuilder.append(line.toString()).append('\n');

		return strbuilder.toString();
	}

	/**
	 * Gets the full user log from the database.
	 *
	 * @return the user log
	 */
	@SuppressWarnings("static-method")
	public String getUserLog()
	{
		final StringBuilder stringBuilder = new StringBuilder();
		ArrayList<Object> log = new ArrayList<Object>();

		SYSLOG.info("Loading the full user log.");

		try
		{
			log = LogDatabaseController.getInstance().getUserLog();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		for (Object line : log)
			stringBuilder.append(line.toString()).append('\n');

		return stringBuilder.toString();
	}

	/**
	 * Refreshes the displayed logs.
	 */
	public void refresh()
	{
		logView.refresh();
	}
}
