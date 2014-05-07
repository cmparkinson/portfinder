/***********************************************************
 * Portfinder
 * Copyright 2010-2014 Christian Parkinson
 * Licensed under the GNU GPL.  See COPYING for full terms.
 ***********************************************************/

package ca.parkie.portfinder.server;

import java.net.InetAddress;

public interface Storage
{
	void store(InetAddress address, byte[] data) throws StorageException;
}
