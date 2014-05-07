/***********************************************************
 * Portfinder
 * Copyright 2010-2014 Christian Parkinson
 * Licensed under the GNU GPL.  See COPYING for full terms.
 ***********************************************************/

package ca.parkie.portfinder.server;

public class Location
{
	private int room;
	private int index;

	public Location(int room, int index)
	{
		this.room = room;
		this.index = index;
	}

	public int getRoom()
	{
		return room;
	}

	public int getIndex()
	{
		return index;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Location location = (Location) o;

		if (index != location.index) return false;
		if (room != location.room) return false;

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = room;
		result = 31 * result + index;
		return result;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder("Location: [ room=");
		sb.append(room).append(", index=").append(index).append(" ]");
		return sb.toString();
	}
}
