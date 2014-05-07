/***********************************************************
 * Portfinder
 * Copyright 2010-2014 Christian Parkinson
 * Licensed under the GNU GPL.  See COPYING for full terms.
 ***********************************************************/

package ca.parkie.portfinder.server;

import org.snmp4j.smi.Address;

public class UnknownAddressException extends RuntimeException
{
	public UnknownAddressException(Address address)
	{
		super("Unexpected address type received: " + address.getClass().getName());
	}
}
