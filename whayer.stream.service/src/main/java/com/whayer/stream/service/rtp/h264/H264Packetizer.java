package com.whayer.stream.service.rtp.h264;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;

import com.whayer.stream.service.rtp.AbstractPacketizer;
import com.whayer.stream.service.rtsp.Session;

import io.netty.channel.Channel;

public class H264Packetizer extends AbstractPacketizer {

	public final static String TAG = "H264Packetizer";

	private int naluLength = 0;
	private long delay = 0, oldtime = 0;
	private Statistics stats = new Statistics();
	private byte[] sps = null, pps = null, stapa = null;
	byte[] header = new byte[5];
	private int count = 0;
	private int streamType = 1;

	public H264Packetizer(ExecutorService executorService,Channel channel, Session session) {
		super(executorService, channel, session);
		this.rtpPacket.setClockFrequency(90000);
		this.rtpPacket.setSSRC(session.getSsrc());
		init();
	}

	// private void init() {
	// File file = new File("/home/whayer/1118.mp4");
	// FileInputStream is = null;
	// try {
	// is = new FileInputStream(file);
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// setInputStream(is);
	// }

	private void init() {
		FileChannel fc = null;
		try {
			fc = FileChannel.open(Paths.get(session.getUri()), StandardOpenOption.READ);
//			fc = FileChannel.open(Paths.get("D:/download/1118.mp4"), StandardOpenOption.READ);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setFileChannel(fc);
	}

	public void setStreamParameters(byte[] pps, byte[] sps) {
		this.pps = pps;
		this.sps = sps;

		// A STAP-A NAL (NAL type 24) containing the sps and pps of the stream
		if (pps != null && sps != null) {
			// STAP-A NAL header + NALU 1 (SPS) size + NALU 2 (PPS) size = 5
			// bytes
			stapa = new byte[sps.length + pps.length + 5];

			// STAP-A NAL header is 24
			stapa[0] = 24;

			// Write NALU 1 size into the array (NALU 1 is the SPS).
			stapa[1] = (byte) (sps.length >> 8);
			stapa[2] = (byte) (sps.length & 0xFF);

			// Write NALU 2 size into the array (NALU 2 is the PPS).
			stapa[sps.length + 3] = (byte) (pps.length >> 8);
			stapa[sps.length + 4] = (byte) (pps.length & 0xFF);

			// Write NALU 1 into the array, then write NALU 2 into the array.
			System.arraycopy(sps, 0, stapa, 3, sps.length);
			System.arraycopy(pps, 0, stapa, 5 + sps.length, pps.length);
		}
	}

	@Override
	public void run() {

		long duration = 0;
		stats.reset();
		count = 0;

		// if (is instanceof MediaCodecInputStream) {
		// streamType = 1;
		// this.rtpPacket.setCacheSize(0);
		// } else {
		streamType = 0;
		this.rtpPacket.setCacheSize(400);
		// }
		executorService.execute(rtpPacket);

		try {
			while (!Thread.interrupted()) {

				oldtime = System.nanoTime();
				// We read a NAL units from the input stream and we send them
				send();
				// We measure how long it took to receive NAL units from the
				// phone
				duration = System.nanoTime() - oldtime;

				stats.push(duration);
				// Computes the average duration of a NAL unit
				delay = stats.average();
				// Log.d(TAG,"duration: "+duration/1000000+" delay:
				// "+delay/1000000);

			}
		} catch (IOException e) {
		} catch (InterruptedException e) {
		}

	}

	/**
	 * Reads a NAL unit in the FIFO and sends it. If it is too big, we split it
	 * in FU-A units (RFC 3984).
	 */
	private void send() throws IOException, InterruptedException {
		int sum = 1, len = 0, type;

		if (streamType == 0) {
			// NAL units are preceeded by their length, we parse the length
			fill(header, 0, 5);
			ts += delay;

			naluLength = header[3] & 0xFF | (header[2] & 0xFF) << 8 | (header[1] & 0xFF) << 16
					| (header[0] & 0xFF) << 24;
			if (naluLength > 100000 || naluLength < 0)
				resync();
			// } else if (streamType == 1) {
			// // NAL units are preceeded with 0x00000001
			// fill(header,0,5);
			// ts =
			// ((MediaCodecInputStream)is).getLastBufferInfo().presentationTimeUs*1000L;
			// //ts += delay;
			// naluLength = is.available()+1;
			// if (!(header[0]==0 && header[1]==0 && header[2]==0)) {
			// // Turns out, the NAL units are not preceeded with 0x00000001
			// streamType = 2;
			// return;
			// }
			// } else {
			// // Nothing preceededs the NAL units
			// fill(header,0,1);
			// header[4] = header[0];
			// ts =
			// ((MediaCodecInputStream)is).getLastBufferInfo().presentationTimeUs*1000L;
			// //ts += delay;
			// naluLength = is.available()+1;
		}

		// Parses the NAL unit type
		type = header[4] & 0x1F;

		// The stream already contains NAL unit type 7 or 8, we don't need
		// to add them to the stream ourselves
		if (type == 7 || type == 8) {
			count++;
			if (count > 4) {
				sps = null;
				pps = null;
			}
		}

		// We send two packets containing NALU type 7 (SPS) and 8 (PPS)
		// Those should allow the H264 stream to be decoded even if no SDP was
		// sent to the decoder.
		if (type == 5 && sps != null && pps != null) {
			buffer = this.rtpPacket.requestBuffer();
			this.rtpPacket.markNextPacket();
			this.rtpPacket.updateTimestamp(ts);
			System.arraycopy(stapa, 0, buffer, rtphl, stapa.length);
			this.rtpPacket.commitBuffer(rtphl + stapa.length);
		}

		// Small NAL unit => Single NAL unit
		if (naluLength <= MAXPACKETSIZE - rtphl - 2) {
			buffer = this.rtpPacket.requestBuffer();
			buffer[rtphl] = header[4];
			len = fill(buffer, rtphl + 1, naluLength - 1);
			this.rtpPacket.updateTimestamp(ts);
			this.rtpPacket.markNextPacket();
			this.rtpPacket.commitBuffer(naluLength + rtphl);
		}
		// Large NAL unit => Split nal unit
		else {

			// Set FU-A header
			header[1] = (byte) (header[4] & 0x1F); // FU header type
			header[1] += 0x80; // Start bit
			// Set FU-A indicator
			header[0] = (byte) ((header[4] & 0x60) & 0xFF); // FU indicator NRI
			header[0] += 28;

			while (sum < naluLength) {
				buffer = this.rtpPacket.requestBuffer();
				buffer[rtphl] = header[0];
				buffer[rtphl + 1] = header[1];
				this.rtpPacket.updateTimestamp(ts);
				if ((len = fill(buffer, rtphl + 2, naluLength - sum > MAXPACKETSIZE - rtphl - 2
						? MAXPACKETSIZE - rtphl - 2 : naluLength - sum)) < 0)
					return;
				sum += len;
				// Last packet before next NAL
				if (sum >= naluLength) {
					// End bit on
					buffer[rtphl + 1] += 0x40;
					this.rtpPacket.markNextPacket();
				}
				this.rtpPacket.commitBuffer(len + rtphl + 2);
				// Switch start bit
				header[1] = (byte) (header[1] & 0x7F);
				// Log.d(TAG,"----- FU-A unit, sum:"+sum);
			}
		}
	}

	private int fill(byte[] buffer, int offset, int length) throws IOException {
		int sum = 0, len;
		ByteBuffer buf = ByteBuffer.allocate(length);
		while (sum < length) {

			len = fc.read(buf);
			buf.flip();
			buf.get(buffer, 0, buf.limit());
			buf.clear();

			// len = is.read(buffer, offset + sum, length - sum);
			if (len < 0) {
				throw new IOException("End of stream");
			} else
				sum += len;
		}
		return sum;
	}

	private void resync() throws IOException {
		int type;

		while (true) {

			header[0] = header[1];
			header[1] = header[2];
			header[2] = header[3];
			header[3] = header[4];

			ByteBuffer buf = ByteBuffer.allocate(1);
			fc.read(buf);
			buf.flip();
			header[4] = buf.get();
			buf.clear();
			// header[4] = (byte) is.read();

			type = header[4] & 0x1F;

			if (type == 5 || type == 1) {
				naluLength = header[3] & 0xFF | (header[2] & 0xFF) << 8 | (header[1] & 0xFF) << 16
						| (header[0] & 0xFF) << 24;
				if (naluLength > 0 && naluLength < 100000) {
					oldtime = System.nanoTime();
					break;
				}
				if (naluLength == 0) {
				} else if (header[3] == 0xFF && header[2] == 0xFF && header[1] == 0xFF && header[0] == 0xFF) {
				}
			}

		}
	}
}
