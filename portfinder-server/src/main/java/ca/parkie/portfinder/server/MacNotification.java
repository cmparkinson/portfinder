/***********************************************************
 * Portfinder
 * Copyright 2010-2014 Christian Parkinson
 * Licensed under the GNU GPL.  See COPYING for full terms.
 ***********************************************************/

package ca.parkie.portfinder.server;

import ca.parkie.portfinder.MacAddress;

public class MacNotification
{
	private int operation;
	private int vlan;
	private MacAddress mac;
	private int port;

	public MacNotification(int operation, int vlan, MacAddress mac, int port)
	{
		this.operation = operation;
		this.vlan = vlan;
		this.mac = mac;
		this.port = port;
	}

	public int getOperation()
	{
		return operation;
	}

	public MacAddress getMacAddress()
	{
		return mac;
	}

	public int getPort()
	{
		return port;
	}

	public int getVlan()
	{
		return vlan;
	}

	@Override
	public String toString()
	{
		return "MacNotification[" +
				"operation=" + operation +
				", vlan=" + vlan +
				", mac=" + mac +
				", port=" + port +
				']';
	}
}
