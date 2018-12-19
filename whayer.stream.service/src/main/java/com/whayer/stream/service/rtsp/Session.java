package com.whayer.stream.service.rtsp;

import java.util.Random;
import java.util.UUID;

public class Session extends SdpBody {
	private String id;
	private String uri;
	private String localAddress;
	private String contentType;

	private String unicast = "unicast";
	private String mode = "play";
	private Integer clientRtpPort = -1;
	private Integer clientRtcpPort = -1;
	private Integer serverRtpPort = -1;
	private Integer serverRtcpPort = -1;
	private Integer ssrc = new Random().nextInt();

	public Session() {
		super();
		this.id = UUID.randomUUID().toString();
		this.transportType = "RTP/AVP";
		this.contentType = "application/sdp";
	}

	public Session(String uri, String localAddress, String remoteAddress) {
		super(remoteAddress);
		this.id = UUID.randomUUID().toString();
		this.uri = uri;
		this.localAddress = localAddress;
		this.contentType = "application/sdp";
	}

	public Session(String uri, String localAddress, String remoteAddress, String transportType, String contentType,
			Body sdp) {
		super(remoteAddress, transportType);
		this.id = UUID.randomUUID().toString();
		this.uri = uri;
		this.localAddress = localAddress;
		this.contentType = contentType;
	}

	public String transport() {
		StringBuilder builder = new StringBuilder();

		builder.append(transportType).append(";").append(this.unicast)
				//.append(";mode=").append(this.mode).append(";destination=").append(this.localAddress)
				.append(";source=").append(this.remoteAddress)
				.append(";client_port=").append(this.clientRtpPort).append("-").append(this.clientRtcpPort)
				.append(";server_port=").append(this.serverRtpPort).append("-").append(this.serverRtcpPort)
				.append(";ssrc=").append("0x").append(Integer.toHexString(this.ssrc));

		return builder.toString();
	}

	public boolean initTransport(String transport) {
		if (null != transport && !transport.isEmpty()) {
			String[] ss = transport.split(";", 0);
			if (ss.length > 2) {
				this.transportType = ss[0];
				for (int i = 2; i < ss.length; i++) {
					if (ss[i].indexOf("client_port") == 0) {
						String[] ports = ss[i].substring(12).split("-");
						if (ports.length == 2) {
							this.clientRtpPort = Integer.parseInt(ports[0]);
							this.clientRtcpPort = Integer.parseInt(ports[1]);
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getLocalAddress() {
		return localAddress;
	}

	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}

	public String getRemoteAddress() {
		return remoteAddress;
	}

	public void setRemoteAddress(String remoteAddress) {
		this.remoteAddress = remoteAddress;
	}

	public String getTransportType() {
		return transportType;
	}

	public void setTransportType(String transportType) {
		this.transportType = transportType;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getUnicast() {
		return unicast;
	}

	public void setUnicast(String unicast) {
		this.unicast = unicast;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Integer getClientRtpPort() {
		return clientRtpPort;
	}

	public void setClientRtpPort(Integer clientRtpPort) {
		this.clientRtpPort = clientRtpPort;
	}

	public Integer getClientRtcpPort() {
		return clientRtcpPort;
	}

	public void setClientRtcpPort(Integer clientRtcpPort) {
		this.clientRtcpPort = clientRtcpPort;
	}

	public Integer getServerRtpPort() {
		return serverRtpPort;
	}

	public void setServerRtpPort(Integer serverRtpPort) {
		this.serverRtpPort = serverRtpPort;
	}

	public Integer getServerRtcpPort() {
		return serverRtcpPort;
	}

	public void setServerRtcpPort(Integer serverRtcpPort) {
		this.serverRtcpPort = serverRtcpPort;
	}

	public Integer getSsrc() {
		return ssrc;
	}

	public void setSsrc(Integer ssrc) {
		this.ssrc = ssrc;
	}

	public Sdp getSdp() {
		return sdp;
	}

	public void setSdp(Sdp sdp) {
		this.sdp = sdp;
	}
}
