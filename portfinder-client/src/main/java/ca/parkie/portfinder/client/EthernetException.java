/***********************************************************
 * Portfinder
 * Copyright 2010-2014 Christian Parkinson
 * Licensed under the GNU GPL.  See COPYING for full terms.
 ***********************************************************/

package ca.parkie.portfinder.client;

public class EthernetException extends Exception
{
	public EthernetException()
	{
	}

	public EthernetException(String message)
	{
		super(message);
	}

	public EthernetException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public EthernetException(Throwable cause)
	{
		super(cause);
	}
}
