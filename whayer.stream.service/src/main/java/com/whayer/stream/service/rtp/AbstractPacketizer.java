package com.whayer.stream.service.rtp;

import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.util.Random;
import java.util.concurrent.ExecutorService;

import com.whayer.stream.service.rtsp.Session;

import io.netty.channel.Channel;

abstract public class AbstractPacketizer implements Runnable {

	protected final static int rtphl = RtpPacket.RTP_HEADER_LENGTH;

	// Maximum size of RTP packets
	protected final static int MAXPACKETSIZE = RtpPacket.MTU - 28;

//	protected InputStream is = null;
	protected FileChannel fc = null;
	protected Session session;

	protected ExecutorService executorService;
	protected RtpPacket rtpPacket;
	protected byte[] buffer;
	protected long ts = 0;

	public AbstractPacketizer(ExecutorService executorService, Channel channel, Session session) {
		this.session = session;
		this.executorService = executorService;
		this.rtpPacket = new RtpPacket(channel, new InetSocketAddress(session.getLocalAddress(), session.getClientRtpPort()));
		this.ts = new Random().nextLong();
	}

//	public void setInputStream(InputStream is) {
//		this.is = is;
//	}

	public void setFileChannel(FileChannel fc) {
		this.fc = fc;
	}
	
	/** For debugging purposes. */
	protected static String printBuffer(byte[] buffer, int start, int end) {
		String str = "";
		for (int i = start; i < end; i++)
			str += "," + Integer.toHexString(buffer[i] & 0xFF);
		return str;
	}

	/**
	 * Used in packetizers to estimate timestamps in RTP packets.
	 */
	protected static class Statistics {

		public final static String TAG = "Statistics";

		private int count = 700, c = 0;
		private float m = 0, q = 0;
		private long elapsed = 0;
		private long start = 0;
		private long duration = 0;
		private long period = 10000000000L;
		private boolean initoffset = false;

		public Statistics() {
		}

		public Statistics(int count, int period) {
			this.count = count;
			this.period = period;
		}

		public void reset() {
			initoffset = false;
			q = 0;
			m = 0;
			c = 0;
			elapsed = 0;
			start = 0;
			duration = 0;
		}

		public void push(long value) {
			elapsed += value;
			if (elapsed > period) {
				elapsed = 0;
				long now = System.nanoTime();
				if (!initoffset || (now - start < 0)) {
					start = now;
					duration = 0;
					initoffset = true;
				}
				// Prevents drifting issues by comparing the real duration of
				// the
				// stream with the sum of all temporal lengths of RTP packets.
				value += (now - start) - duration;
				// Log.d(TAG, "sum1: "+duration/1000000+" sum2:
				// "+(now-start)/1000000+" drift:
				// "+((now-start)-duration)/1000000+" v: "+value/1000000);
			}
			if (c < 5) {
				// We ignore the first 20 measured values because they may not
				// be accurate
				c++;
				m = value;
			} else {
				m = (m * q + value) / (q + 1);
				if (q < count)
					q++;
			}
		}

		public long average() {
			long l = (long) m;
			duration += l;
			return l;
		}
	}
}
