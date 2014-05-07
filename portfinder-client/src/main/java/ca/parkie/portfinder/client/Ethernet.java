/***********************************************************
 * Portfinder
 * Copyright 2010-2014 Christian Parkinson
 * Licensed under the GNU GPL.  See COPYING for full terms.
 ***********************************************************/

package ca.parkie.portfinder.client;

import com.sun.jna.Library;

public interface Ethernet extends Library
{
	int generateFrame(String ifaceName, byte[] sourceMac, byte[] destinationMac, int type);
	int poll(String ifaceName);
	String[] getInterfaceList();
}
