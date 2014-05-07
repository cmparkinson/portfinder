/***********************************************************
 * Portfinder
 * Copyright 2010-2014 Christian Parkinson
 * Licensed under the GNU GPL.  See COPYING for full terms.
 ***********************************************************/

package ca.parkie.portfinder.server;

import ca.parkie.portfinder.Constants;
import ca.parkie.portfinder.MacAddressCodec;
import org.snmp4j.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements CommandResponder
{
	private static final OID INTERESTED_OID = new OID("1.3.6.1.4.1.9.9.215.1.1.8.1.2");

	private Logger log = Logger.getLogger(getClass().getName());

	private Snmp snmp;
	private UdpAddress address;

	private Storage storage;

	public Server(Storage storage, InetAddress address, int port)
	{
		if (address == null)
			this.address = new UdpAddress(port);
		else
			this.address = new UdpAddress(address, port);

		this.storage = storage;
	}

	public Server(Storage storage, int port)
	{
		this(storage, null, port);
	}

	public Server(Storage storage)
	{
		this(storage, null, 161);
	}

	public void start() throws IOException
	{
		TransportMapping mapping = new DefaultUdpTransportMapping(address);
		snmp = new Snmp(mapping);
		snmp.addCommandResponder(this);
		snmp.listen();
	}

	public void stop() throws IOException
	{
		snmp.close();
	}

	public synchronized void processPdu(CommandResponderEvent commandResponderEvent)
	{
		PDU command = commandResponderEvent.getPDU();
		Address address = commandResponderEvent.getPeerAddress();
		InetAddress inetAddress;
		if (address instanceof IpAddress)
			inetAddress = ((IpAddress) address).getInetAddress();
		else
			throw new UnknownAddressException(address);

		if (command != null)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("Received SNMP packet: ").append(command.toString());
			log.finest(sb.toString());

			// The SNMP API has no generics support - this is a workaround that involves no casting
			for (int i = 0; i < command.getVariableBindings().size(); i++)
			{
				VariableBinding vb = command.get(i);
				if (vb.getOid().startsWith(INTERESTED_OID))
				{
					Variable v = vb.getVariable();

					try
					{
						if (v instanceof OctetString)
							storage.store(inetAddress, ((OctetString) v).getValue());
					}
					catch (StorageException e)
					{
						log.log(Level.WARNING, "Could not store trap.", e);
					}
				}
			}
		}
	}

	public static void main(String[] args)
	{
		if (args.length != 5)
		{
			System.out.println("Usage: <dbhost> <dbname> <dbuser> <dbpass> <udp port>");
			System.exit(1);
		}
		
		String host = args[0];
		String dbName = args[1];
		String dbUser = args[2];
		String dbPass = args[3];
		Integer port = Integer.valueOf(args[4]);
		
		try
		{
			MacAddressCodec codec = new MacAddressCodec(
					Constants.SOURCE_MAC_TEMPLATE,
					Constants.DEFAULT_ROOM_MASK,
					Constants.DEFAULT_INDEX_MASK);

			Filter filter = new SimpleFilter(Constants.SOURCE_MAC_TEMPLATE);

			String uri = "jdbc:mysql://" + host + "/" + dbName;
			
			AbstractStorage storage = StorageFactory.getDatabase(codec, filter, uri, dbUser, dbPass);
			Server server = new Server(storage, port);

			server.start();
			System.out.println("Running.  Press Q <enter> to exit.");
			int i;
			do
			{
				i = System.in.read();
			}
			while (i != (int) 'Q' && i != 'q');
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
