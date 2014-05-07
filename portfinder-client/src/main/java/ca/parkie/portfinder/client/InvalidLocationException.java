/***********************************************************
 * Portfinder
 * Copyright 2010-2014 Christian Parkinson
 * Licensed under the GNU GPL.  See COPYING for full terms.
 ***********************************************************/

package ca.parkie.portfinder.client;

public class InvalidLocationException extends Exception
{
	public InvalidLocationException()
	{
	}

	public InvalidLocationException(String message)
	{
		super(message);
	}
}
