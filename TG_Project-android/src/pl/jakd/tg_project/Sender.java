package pl.jakd.tg_project;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class Sender
{
	public static final int PORT = 9999;
	public static final String host = "192.168.10.200";

	public static final byte TYPE_SENSORS = 0;
	public static final byte TYPE_OBJ = 1;

	private DatagramSocket udpSocket;

	public Sender ()
	{
		try
		{
			udpSocket = new DatagramSocket (PORT);
			udpSocket.setBroadcast (true);
		}
		catch (SocketException e)
		{
			e.printStackTrace ();
		}
	}

	public void sendData (final byte[] data)
	{
		Thread t = new Thread (new Runnable ()
		{
			@Override
			public void run ()
			{
				DatagramPacket dp = new DatagramPacket (data,
						data.length);
				try
				{
					if (udpSocket == null || udpSocket.isClosed ())
					{
						udpSocket = new DatagramSocket (PORT);
						udpSocket.setBroadcast (true);
					}
					dp.setSocketAddress (new InetSocketAddress (host, PORT));
					udpSocket.send (dp);
				}
				catch (SocketException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace ();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace ();
				}
			}
		});
		t.start ();

	}

	public void close ()
	{
		udpSocket.close ();
	}

}
