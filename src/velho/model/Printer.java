package velho.model;

import org.apache.log4j.Logger;

/**
 * @author Edward
 */
public class Printer
{
	/**
	 * Apache log4j logger: System.
	 */
	private static final Logger SYSLOG = Logger.getLogger(Printer.class.getName());

	public static void print(final Object data)
	{
		SYSLOG.debug("Printer received data: " + data.toString());
	}
}
