/***********************************************************
 * Portfinder
 * Copyright 2010-2014 Christian Parkinson
 * Licensed under the GNU GPL.  See COPYING for full terms.
 ***********************************************************/

package ca.parkie.portfinder;

import ca.parkie.portfinder.server.Location;

import java.math.BigInteger;

public class MacAddressCodec
{
	private BigInteger roomMask;
	private BigInteger indexMask;

	private MacAddress template;

	public MacAddressCodec(MacAddress template, long roomMask, long indexMask)
	{
		this.template = template;
		this.roomMask = longToBigInteger(roomMask);
		this.indexMask = longToBigInteger(indexMask);
	}

	public MacAddressCodec(long roomMask, long indexMask)
	{
		this(Constants.SOURCE_MAC_TEMPLATE, roomMask, indexMask);
	}

	public Location decode(MacAddress mac)
	{
		byte[] raw = mac.toByteArray();
		int room = decode(raw, roomMask);
		int index = decode(raw, indexMask);
		return new Location(room, index);
	}

	private int decode(byte[] rawMac, BigInteger mask)
	{
		BigInteger mac = new BigInteger(rawMac);
		return maskAndShift(mac, mask);
	}

	private int maskAndShift(BigInteger mac, BigInteger mask)
	{
		BigInteger bi = mac.and(mask);
		int shift = mask.getLowestSetBit();
		return bi.shiftRight(shift).intValue();
	}

	private static BigInteger intToBigInteger(int i)
	{
		return new BigInteger(String.valueOf(i));
	}

	private static BigInteger longToBigInteger(long l)
	{
		return new BigInteger(String.valueOf(l));
	}

	public MacAddress encode(Location location)
	{
		BigInteger bia = new BigInteger(template.toByteArray());
		BigInteger room = intToBigInteger(location.getRoom());
		BigInteger index = intToBigInteger(location.getIndex());
		int roomShift = roomMask.getLowestSetBit();
		int indexShift = indexMask.getLowestSetBit();
		bia = bia.or(room.shiftLeft(roomShift));
		bia = bia.or(index.shiftLeft(indexShift));
		byte[] bytes = bia.toByteArray();
		return new MacAddress(bytes);
	}
}
