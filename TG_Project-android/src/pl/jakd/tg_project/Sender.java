package pl.jakd.tg_project;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;

public class Sender
{
	public static final int PORT = 9999;
	public static final String host = "192.168.1.2";

	public static final byte TYPE_SENSORS = 0;
	public static final byte TYPE_PLAYER = 1;
	public static final byte TYPE_STABILIZING = 2;
	public static final byte TYPE_ENEMY = 3;
	public static final byte TYPE_FOOD = 4;
	public static final byte TYPE_WALL = 5;
	
	private DatagramSocket udpSocket = null;
	private LinkedBlockingQueue<byte[]> queue = new LinkedBlockingQueue<byte[]> ();
	private Thread t;

	public Sender ()
	{
		t = new Thread (new SendWorker ());
		t.start ();
		try
		{
			udpSocket = new DatagramSocket (PORT);
			udpSocket.setBroadcast (true);
		}
		catch (SocketException e)
		{
			e.printStackTrace ();
		}//*/
	}

	public void sendData (final byte[] data)
	{
		queue.add (data);
	}

	public void close ()
	{
		if (udpSocket != null)
		{
			udpSocket.disconnect ();
			udpSocket.close ();
			udpSocket = null;
		}
	}

	private class SendWorker implements Runnable
	{
		@Override
		public void run ()
		{
			while (true)
			{
				try
				{
					byte[] data = queue.poll (1, TimeUnit.SECONDS);

					if (data == null)
						continue;

					int len = 700;
					int size;

					Deflater deflater = new Deflater (Deflater.BEST_COMPRESSION);
					byte[] buff = null;

					do
					{
						deflater.reset ();
						deflater.setInput (data);
						deflater.finish ();
						len *= 2;
						buff = new byte[len + 4];
						size = deflater.deflate (buff, 4, buff.length - 4);

					} while (size == len);

					buff[0] = (byte)(size >> 24);
					buff[1] = (byte)(size >> 16);
					buff[2] = (byte)(size >> 8);
					buff[3] = (byte)(size >> 0);

					DatagramPacket dp = new DatagramPacket (buff,
							size + 4);

					if (udpSocket == null)
					{
						udpSocket = new DatagramSocket (PORT);
						udpSocket.setBroadcast (true);
					}
					dp.setSocketAddress (new InetSocketAddress (host, PORT));

					udpSocket.send (dp);
				}
				catch (Exception e)
				{
					e.printStackTrace ();
				}
			}
		}
	}
}
