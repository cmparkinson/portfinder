/***********************************************************
 * Portfinder
 * Copyright 2010-2014 Christian Parkinson
 * Licensed under the GNU GPL.  See COPYING for full terms.
 ***********************************************************/

package ca.parkie.portfinder.server;

public class StorageException extends Exception
{
	public StorageException()
	{
	}

	public StorageException(String message)
	{
		super(message);
	}

	public StorageException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public StorageException(Throwable cause)
	{
		super(cause);
	}
}
