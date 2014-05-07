/***********************************************************
 * Portfinder
 * Copyright 2010-2014 Christian Parkinson
 * Licensed under the GNU GPL.  See COPYING for full terms.
 ***********************************************************/

package ca.parkie.portfinder.client;

import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class Sizer implements ComponentListener
{
	public void componentResized(ComponentEvent e)
	{
		Dimension d = e.getComponent().getSize();
		System.out.println(d.width + "x" + d.height);
	}

	public void componentMoved(ComponentEvent e)
	{
	}

	public void componentShown(ComponentEvent e)
	{
	}

	public void componentHidden(ComponentEvent e)
	{
	}
}
