/***********************************************************
 * Portfinder
 * Copyright 2010-2014 Christian Parkinson
 * Licensed under the GNU GPL.  See COPYING for full terms.
 ***********************************************************/

package ca.parkie.portfinder.server;

import ca.parkie.portfinder.MacAddress;

import java.math.BigInteger;

public class SimpleFilter implements Filter
{
	private BigInteger template;

	public SimpleFilter(MacAddress template)
	{
		this.template = new BigInteger(template.toByteArray());
	}

	@Override
	public boolean isInteresting(MacNotification notification)
	{
		BigInteger bi = new BigInteger(notification.getMacAddress().toByteArray());
		BigInteger masked = template.and(bi);
		return template.equals(masked);
	}
}
