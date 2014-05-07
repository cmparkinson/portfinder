/***********************************************************
 * Portfinder
 * Copyright 2010-2014 Christian Parkinson
 * Licensed under the GNU GPL.  See COPYING for full terms.
 ***********************************************************/

package ca.parkie.portfinder;

public class Constants
{
	public static long DEFAULT_ROOM_MASK = 0x3FF00;

	public static long DEFAULT_INDEX_MASK = 0xFF;

	public static MacAddress DESTINATION_MAC_ADDRESS = new MacAddress("FF:FF:FF:FF:FF:FF");

	public static MacAddress SOURCE_MAC_TEMPLATE = new MacAddress(new byte[] { 0x02, 0x55, 0x55, 0x00, 0x00, 0x00 });

	public static int ETHER_TYPE = 0x01FF;

	public static int ROOM_LOW = 100;

	public static int ROOM_HIGH = 1500;

	public static int PORT_HIGH = 20;
}
