/***********************************************************
 * Portfinder
 * Copyright 2010-2014 Christian Parkinson
 * Licensed under the GNU GPL.  See COPYING for full terms.
 ***********************************************************/

package ca.parkie.portfinder.server;
import ca.parkie.portfinder.MacAddress;
import ca.parkie.portfinder.MacAddressCodec;

import java.nio.ByteBuffer;

abstract public class AbstractStorage implements Storage
{
	protected Filter filter;
	protected MacAddressCodec codec;

	protected AbstractStorage(MacAddressCodec codec, Filter filter)
	{
		this.codec = codec;
		this.filter = filter;
	}

	protected MacNotification extractRecord(ByteBuffer buffer)
	{
		int operation = buffer.get();

		// operation == 0 when at the end of the mib
		if (operation == 0)
			return null;

		int vlan = buffer.getShort();

		byte[] rawMac = new byte[6];
		buffer.get(rawMac);
		MacAddress mac = new MacAddress(rawMac);

		int port = buffer.getShort();
		return new MacNotification(operation, vlan, mac, port);
	}

	public boolean isValid(MacNotification notification)
	{
		return filter == null || filter.isInteresting(notification);
	}
}
