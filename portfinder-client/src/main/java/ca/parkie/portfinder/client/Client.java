/***********************************************************
 * Portfinder
 * Copyright 2010-2014 Christian Parkinson
 * Licensed under the GNU GPL.  See COPYING for full terms.
 ***********************************************************/

package ca.parkie.portfinder.client;

import ca.parkie.portfinder.Constants;
import ca.parkie.portfinder.MacAddress;
import ca.parkie.portfinder.MacAddressCodec;
import ca.parkie.portfinder.Location;
import com.sun.jna.Native;

import javax.swing.*;
import java.util.*;

public class Client
{
	public final static String TITLE = "PortFinder v1.0";

	private MacAddressCodec codec;

	private String networkInterface;
	private Ethernet ether;

	private MainWindow window;
	private JFrame frame;

	public Client(MacAddressCodec codec)
	{
		ether = (Ethernet) Native.loadLibrary("ethernet", Ethernet.class);
		Native.loadLibrary(Ethernet.class);
		this.codec = codec;
	}

	public boolean generateFrame(MacAddress source) throws EthernetException
	{
		int r = ether.generateFrame(networkInterface, source.toByteArray(),
				Constants.DESTINATION_MAC_ADDRESS.toByteArray(),
				Constants.ETHER_TYPE);

		if (r == -1)
			throw new EthernetException();

		return r == 0;
	}

	public boolean poll() throws EthernetException
	{
		int r = ether.poll(networkInterface);
		if (r < 0)
		{
			switch (r)
			{
				case -2:
					throw new InterfaceNotFoundException();
				default:
					throw new EthernetException();
			}
		}

		return r == 0;
	}

	public MacAddress getMacAddress(Location loc)
	{
		return codec.encode(loc);
	}

	public Location getLocation(int room, int port) throws InvalidLocationException
	{
		if (room < Constants.ROOM_LOW || room >= Constants.ROOM_HIGH)
			throw new InvalidLocationException("Invalid room: " + room);

		if (port < 1 || port >= Constants.PORT_HIGH)
			throw new InvalidLocationException("Invalid port number: " + port);

		return new Location(room, port);
	}

	public String getNetworkInterface()
	{
		return networkInterface;
	}

	public String[] getNetworkInterfaceList() throws InterfaceListException
	{
		String[] networkInterfaceArray = ether.getInterfaceList();
		if (networkInterfaceArray == null)
			throw new InterfaceListException("Could not discover interface list.");

		List<String> list = Arrays.asList(networkInterfaceArray);
		Collections.sort(list);
		String[] array = new String[networkInterfaceArray.length];
		return list.toArray(array);
	}

	public boolean showNetworkInterfaceDialog()
	{
		try
		{
			String[] networkInterfaceList = getNetworkInterfaceList();
			JDialog dialog = new JDialog();
			final NetworkInterfaceDialog networkInterfaceDialog = new NetworkInterfaceDialog(dialog, networkInterfaceList);
			dialog.setContentPane(networkInterfaceDialog.getContentPane());
			dialog.setResizable(false);
			dialog.setModal(true);
			dialog.pack();
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);

			networkInterface = networkInterfaceDialog.getNetworkInterface();
			return networkInterface != null;
		}
		catch (InterfaceListException e)
		{
			JOptionPane.showMessageDialog(frame, "Could not discover network interface list.", "Network Interface Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	private void showMainWindow()
	{
		MainWindow window = new MainWindow(this);
		window.startInterfacePoll();

		frame.setContentPane(window.getContentPane());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public static void main(String[] args)
	{
		try
		{
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
			{
				if ("Nimbus".equals(info.getName()))
				{
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		}
		catch (Exception e)
		{
			// If Nimbus is not available, you can set the GUI to another look and feel.
		}

		MacAddressCodec codec = new MacAddressCodec(Constants.DEFAULT_ROOM_MASK, Constants.DEFAULT_INDEX_MASK);

		if (System.getProperty("jna.library.path") == null)
			System.setProperty("jna.library.path", ".");

		final Client c = new Client(codec);

		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				String user = System.getenv("USER");
				if (user == null)
					user = System.getenv("user");

				if (user == null || !user.equalsIgnoreCase("root"))
				{
					JOptionPane.showMessageDialog(null,
							"You don't appear to be root.  Please run the application as root.",
							"Insufficient Privileges",
							JOptionPane.ERROR_MESSAGE);
					System.exit(0);
				}

				JFrame frame = new JFrame(TITLE);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setResizable(false);
				c.frame = frame;

				if (!c.showNetworkInterfaceDialog())
					System.exit(0);

				c.showMainWindow();
			}
		});

	}
}
