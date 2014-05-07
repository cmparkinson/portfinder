/***********************************************************
 * Portfinder
 * Copyright 2010-2014 Christian Parkinson
 * Licensed under the GNU GPL.  See COPYING for full terms.
 ***********************************************************/

package ca.parkie.portfinder.server;

import ca.parkie.portfinder.MacAddressCodec;

import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQLDatabase extends AbstractStorage
{
	private Logger log = Logger.getLogger(getClass().getName());

	protected String connectionUri;
	protected String username;
	protected String password;

	protected Connection con;
	protected PreparedStatement ps;

	public MySQLDatabase(MacAddressCodec codec, Filter filter, String connectionUri)
			throws ClassNotFoundException, SQLException
	{
		this(codec, filter, connectionUri, null, null);
	}

	public MySQLDatabase(MacAddressCodec codec, Filter filter, String connectionUri, String username, String password)
			throws ClassNotFoundException, SQLException
	{
		super(codec, filter);
		this.connectionUri = connectionUri;
		this.username = username;
		this.password = password;

		Class.forName("com.mysql.jdbc.Driver");

		if (username != null)
			con = DriverManager.getConnection(connectionUri, username, password);
		else
			con = DriverManager.getConnection(connectionUri);

		ps = con.prepareStatement("insert into notification (ip, operation, vlan, mac, port, room, room_port_num) values (?, ?, ?, ?, ?, ?, ?)");
	}

	public synchronized void store(InetAddress address, byte[] data) throws StorageException
	{
		ByteBuffer buffer = ByteBuffer.allocate(data.length);
		buffer.put(data);
		buffer.rewind();

		BigInteger bi = new BigInteger(address.getAddress());
		long rawAddress = bi.longValue();

		try
		{
			if (con.isClosed())
			{
				log.info("SQL connection closed.  Reconnecting...");
				con = DriverManager.getConnection(connectionUri, username, password);
			}
		}
		catch (SQLException e)
		{
			throw new StorageException(e);
		}

		MacNotification notification;
		while ((notification = extractRecord(buffer)) != null)
		{
			if (!isValid(notification))
				continue;

			Location loc = codec.decode(notification.getMacAddress());

			try
			{
				ps.setLong(1, rawAddress);
				ps.setByte(2, (byte) notification.getOperation());
				ps.setInt(3, notification.getVlan());
				BigInteger macBi = new BigInteger(notification.getMacAddress().toByteArray());
				ps.setLong(4, macBi.longValue());
				ps.setShort(5, (short) notification.getPort());
				ps.setShort(6, (short) loc.getRoom());
				ps.setByte(7, (byte) loc.getIndex());
				ps.execute();
			}
			catch (SQLException e)
			{
				throw new StorageException(e);
			}

			if (log.isLoggable(Level.INFO))
			{
				StringBuilder sb = new StringBuilder("Received trap from ");
				sb.append(address.getHostAddress()).append(": ").append(notification.toString());
				log.info(sb.toString());
			}
		}
	}
}
