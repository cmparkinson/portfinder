/***********************************************************
 * Portfinder
 * Copyright 2010-2014 Christian Parkinson
 * Licensed under the GNU GPL.  See COPYING for full terms.
 ***********************************************************/

package ca.parkie.portfinder.server;

import ca.parkie.portfinder.MacAddressCodec;

import java.sql.SQLException;

public class StorageFactory
{
	public static AbstractStorage getDatabase(MacAddressCodec codec, Filter filter, String uri) throws StorageException
	{
		return getDatabase(codec, filter, uri, null, null);
	}

	public static AbstractStorage getDatabase(MacAddressCodec codec, Filter filter, String uri, String username, String password)
			throws StorageException
	{
		try
		{
			return new MySQLDatabase(codec, filter, uri, username, password);
		}
		catch (Exception e)
		{
			throw new StorageException(e);
		}
	}
}
