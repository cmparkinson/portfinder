/***********************************************************
 * Portfinder
 * Copyright 2010-2014 Christian Parkinson
 * Licensed under the GNU GPL.  See COPYING for full terms.
 ***********************************************************/

package ca.parkie.portfinder;

public class MacAddress
{
	private byte[] address;

	public MacAddress(byte[] address)
	{
		if (address.length != 6)
			throw new IllegalArgumentException("Invalid MAC address length: " + address.length);

		this.address = address;
	}

	public MacAddress(String mac)
	{
		String[] parts = mac.split(":");
		if (parts.length != 6)
			throw new IllegalArgumentException("Invalid MAC address: " + mac);

		address = new byte[6];
		for (int i = 0; i < parts.length; i++)
			address[i] = (byte) Integer.parseInt(parts[i], 16);
	}

	public byte[] toByteArray()
	{
		return address;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < address.length; i++)
		{
			String s = Integer.toHexString(address[i] & 0xFF);
			if (s.length() == 1)
				sb.append("0");

			sb.append(s.toUpperCase());

			if (i < address.length - 1)
				sb.append(":");
		}

		return sb.toString();
	}
}
