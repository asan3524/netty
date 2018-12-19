package com.whayer.stream.service.rtsp;

import com.whayer.stream.service.rtsp.Sdp.M.E;

public class SdpBody extends Body {

	public SdpBody() {
		this.sdp = new Sdp();
	}

	public SdpBody(String remoteAddress) {
		this.sdp = new Sdp();
		this.remoteAddress = remoteAddress;
		this.transportType = "RTP/AVP";
	}

	public SdpBody(String remoteAddress, String transportType) {
		this.sdp = new Sdp();
		this.remoteAddress = remoteAddress;
		this.transportType = transportType;
	}

	protected Sdp sdp;
	protected String remoteAddress;
	protected String transportType;

	@Override
	protected boolean serialize() {

		StringBuilder builder = new StringBuilder();
		// session
		if (null != this.sdp.getS()) {
			// v
			builder.append("v=").append(this.sdp.getS().getVersion()).append("\r\n");
			// o
			builder.append("o=").append(this.sdp.getS().getCreateId()).append(" ")
					.append(this.sdp.getS().getSessionId()).append(" ").append(this.sdp.getS().getVersion()).append(" ")
					.append(this.sdp.getS().getNetType()).append(" ").append(this.sdp.getS().getIpType()).append(" ")
					.append(this.remoteAddress).append("\r\n");
			// s
			builder.append("s=").append(this.sdp.getS().getSessionName()).append("\r\n");
			// u
			if (null != this.sdp.getS().getFileUri())
				builder.append("u=").append(this.sdp.getS().getFileUri()).append("\r\n");
			String addr = null == this.sdp.getS().getcIp() ? this.remoteAddress : this.sdp.getS().getcIp();
			// c
			builder.append("c=").append(this.sdp.getS().getNetType()).append(" ").append(this.sdp.getS().getIpType())
					.append(" ").append(addr).append("\r\n");
		}
		// time
		if (null != this.sdp.getT()) {
			builder.append("t=").append(this.sdp.getT().getBeginTime()).append(" ").append(this.sdp.getT().getEndTime())
					.append("\r\n");
		}

		// media
		if (null != this.sdp.getM()) {
			// 解码标签必须有值
			if (this.sdp.getM().getPayloads().isEmpty())
				return false;

			// m
			builder.append("m=").append(this.sdp.getM().getMedia()).append(" ").append(this.sdp.getM().getPort())
					.append(" ").append(this.transportType);

			for (Integer v : this.sdp.getM().getPayloads()) {
				builder.append(" ").append(v);
			}

			builder.append("\r\n");

			// a
			if (null != this.sdp.getM().getSendOrRecv()) {
				builder.append("a=").append(this.sdp.getM().getSendOrRecv()).append("\r\n");
			}

			if (null != this.sdp.getM().getEncodeInfos() && !this.sdp.getM().getEncodeInfos().isEmpty()) {
				for (E info : this.sdp.getM().getEncodeInfos()) {
					builder.append("a=").append("rtpmap:").append(info.payload).append(" ").append(info.encodeName)
							.append('/').append(info.clockrate).append("\r\n");
				}
			}

			if (null != this.sdp.getM().getDlSpeed()) {
				builder.append("a=downloadspeed:").append(this.sdp.getM().getDlSpeed()).append("\r\n");
			}

			if (null != this.sdp.getM().getFileSize()) {
				builder.append("a=filesize:").append(this.sdp.getM().getFileSize()).append("\r\n");
			}

			if (null != this.sdp.getM().getSetup()) {
				builder.append("a=setup:").append(this.sdp.getM().getSetup()).append("\r\n");
			}

			if (null != this.sdp.getM().getConnection()) {
				builder.append("a=connection:").append(this.sdp.getM().getConnection()).append("\r\n");
			}

			if (null != this.sdp.getM().getSvcspace()) {
				builder.append("a=svcspace:").append(this.sdp.getM().getSvcspace()).append("\r\n");
			}

			if (null != this.sdp.getM().getSvctime()) {
				builder.append("a=svctime:").append(this.sdp.getM().getSvctime()).append("\r\n");
			}

			// y
			if (null != this.sdp.getM().getY() && !this.sdp.getM().getY().isEmpty()) {
				builder.append("y=").append(this.sdp.getM().getY()).append("\r\n");
			}

			// f
			if (null != this.sdp.getM().getMd()) {
				String v1 = "";
				String v2 = "";
				String v3 = "";
				String v4 = "";
				String v5 = "";
				String a1 = "";
				String a2 = "";
				String a3 = "";

				if (null != this.sdp.getM().getMd().getvEncode()) {
					v1 = String.valueOf(this.sdp.getM().getMd().getvCodeRate());
				}

				if (null != this.sdp.getM().getMd().getvResolution()) {
					v2 = String.valueOf(this.sdp.getM().getMd().getvResolution());
				}

				if (null != this.sdp.getM().getMd().getvFrameRate()) {
					v3 = String.valueOf(this.sdp.getM().getMd().getvFrameRate());
				}

				if (null != this.sdp.getM().getMd().getvEncode()) {
					v1 = String.valueOf(this.sdp.getM().getMd().getvCodeRate());
				}

				if (null != this.sdp.getM().getMd().getvCodeRateType()) {
					v4 = String.valueOf(this.sdp.getM().getMd().getvCodeRateType());
				}

				if (null != this.sdp.getM().getMd().getvCodeRate()) {
					v5 = String.valueOf(this.sdp.getM().getMd().getvCodeRate());
				}

				if (null != this.sdp.getM().getMd().getaEncode()) {
					a1 = String.valueOf(this.sdp.getM().getMd().getaEncode());
				}

				if (null != this.sdp.getM().getMd().getaCodeRate()) {
					a2 = String.valueOf(this.sdp.getM().getMd().getaCodeRate());
				}

				if (null != this.sdp.getM().getMd().getaSampling()) {
					a3 = String.valueOf(this.sdp.getM().getMd().getaSampling());
				}

				// 格式f=v/v1/v2/v3/v4/v5/
				builder.append("f=v/").append(v1).append("/").append(v2).append("/").append(v3).append("/").append(v4)
						.append("/").append(v5).append("a/").append(a1).append("/").append(a2).append("/").append(a3)
						.append("\r\n");
			}
		}

		this.body = builder.toString();

		return true;
	}

	@Override
	protected boolean deserialize() {
		// TODO Auto-generated method stub

		if (null == this.body || this.body.isEmpty())
			return false;

		String lines[] = body.split("[\r\n]+", -1);

		if (null == lines || lines.length < 1) {
			return false;
		}
		// 解析时，首先是会话域
		// step
		// 0: session field
		// 1: time field
		// 2: media field
		int step = 0;

		for (String line : lines) {
			String s[] = line.split("[=]+", -1);
			if (null == s || s.length < 1)
				continue;

			char[] symbols = s[0].toCharArray();

			if (symbols.length < 1)
				continue;

			char flag = symbols[0];

			if (flag == 't') {
				step = 1;
			} else if (flag == 'm') {
				step = 2;
			}

			switch (step) {
			case 0: {
				parseSession(flag, s);
			}
				break;

			case 1: {
				parseTime(flag, s);
			}
				break;

			case 2: {
				parseMedia(flag, s);
			}
				break;

			default:
				break;
			}
		}
		return true;
	}

	private boolean parseSession(char flag, String[] lines) {
		switch (flag) {
		case 'v': {
			if (lines.length > 1) {
				String v = lines[1].trim();
				if (null != v && !v.isEmpty())
					this.getSdp().getS().setVersion(new String(v));
			}
		}
			break;

		case 'o': {
			if (lines.length == 1)
				break;

			String tmp = lines[1].trim();

			if (null != tmp && !tmp.isEmpty()) {
				String newLines[] = tmp.split("\\s+", -1);

				if (newLines.length > 0) {
					String str = newLines[0].trim();
					if (null != str && !str.isEmpty())
						this.getSdp().getS().setCreateId(str);
				}

				if (newLines.length > 1) {
					String str = newLines[1].trim();
					if (null != str && !str.isEmpty())
						this.getSdp().getS().setSessionId(str);
				}

				if (newLines.length > 3) {
					String str = newLines[3].trim();
					if (null != str && !str.isEmpty())
						this.getSdp().getS().setNetType(str);
				}

				if (newLines.length > 4) {
					String str = newLines[4].trim();
					if (null != str && !str.isEmpty())
						this.getSdp().getS().setIpType(str);
				}

				if (newLines.length > 5) {
					String str = newLines[5].trim();
					if (null != str && !str.isEmpty())
						this.setRemoteAddress(str);
				}
			}

		}
			break;

		case 's': {
			if (lines.length > 1) {
				String v = lines[1].trim();
				if (null != v && !v.isEmpty())
					this.getSdp().getS().setSessionName(new String(v));
			}
		}
			break;

		case 'u': {
			if (lines.length > 1) {
				String v = lines[1].trim();
				if (null != v && !v.isEmpty())
					this.getSdp().getS().setFileUri(new String(v));
			}
		}
			break;

		case 'c': {
			if (lines.length > 1) {
				String tmp = lines[1].trim();

				if (null != tmp && !tmp.isEmpty()) {
					String newLines[] = tmp.split("\\s+", -1);

					if (newLines.length > 2) {
						String str = newLines[2].trim();
						if (null != str && !str.isEmpty())
							this.getSdp().getS().setcIp(str);
					}

				}
			}
		}
			break;

		default:
			break;
		}
		return true;
	}

	private boolean parseTime(char flag, String[] lines) {
		switch (flag) {
		case 't': {
			if (lines.length > 1) {
				String tmp = lines[1].trim();

				if (null != tmp && !tmp.isEmpty()) {
					String newLines[] = tmp.split("\\s+", -1);

					if (newLines.length > 1) {
						String bt = newLines[0].trim();
						if (null != bt && !bt.isEmpty()) {
							try {
								this.getSdp().getT().setBeginTime(Long.parseLong(bt));
							} catch (NumberFormatException e) {
							}

						}

						String et = newLines[1].trim();
						if (null != et && !et.isEmpty()) {
							try {
								this.getSdp().getT().setEndTime(Long.parseLong(et));
							} catch (NumberFormatException e) {
							}

						}
					}

				}
			}
		}
			break;

		default:
			break;
		}

		return true;
	}

	private boolean parseMedia(char flag, String[] lines) {
		switch (flag) {
		case 'm': {
			if (lines.length > 1) {
				String tmp = lines[1].trim();

				if (null != tmp && !tmp.isEmpty()) {
					String newLines[] = tmp.split("\\s+", -1);

					if (newLines.length > 0) {
						String str = newLines[0].trim();
						if (null != str && !str.isEmpty())
							this.getSdp().getM().setMedia(str);
					}

					if (newLines.length > 1) {
						String str = newLines[1].trim();
						if (null != str && !str.isEmpty()) {
							try {
								this.getSdp().getM().setPort(Integer.parseInt(str));
							} catch (NumberFormatException e) {
								return false;
							}
						}
					}

					if (newLines.length > 2) {
						String str = newLines[2].trim();
						if (null != str && !str.isEmpty())
							this.setTransportType(str);
					}

					if (newLines.length > 3) {
						for (int i = 3; i < newLines.length; i++) {
							String str = newLines[i].trim();
							if (null != str && !str.isEmpty()) {
								int v = -1;
								try {
									v = Integer.parseInt(str);
								} catch (NumberFormatException e) {
									break;
								}

								this.getSdp().getM().getPayloads().add(v);
							}
						}
					}

				}

			}
		}
			break;

		case 'c': {
			// 保留
		}
			break;

		case 'b': {
			// 保留
		}
			break;

		case 'a': {
			if (lines.length > 1) {
				String str = lines[1].trim();

				if (null != str && !str.isEmpty()) {
					str = str.toLowerCase();

					if (str.contains("rtpmap:")) {
						String newLines[] = str.split("\\s+");

						if (newLines.length > 1) {
							String v = newLines[0].trim();
							if (null != v && !v.isEmpty()) {
								v = v.substring("rtpmap:".length());
								int payload = 96;
								try {
									payload = Integer.parseInt(v);
								} catch (Exception e) {
									break;
								}
								Sdp.M.E info = this.sdp.getM().new E(payload);

								String tmp = newLines[1].trim();
								if (null != tmp && !tmp.isEmpty()) {
									String tmpLines[] = tmp.split("/");
									if (tmpLines.length > 1) {
										String n = tmpLines[0].trim();
										if (null != n && !n.isEmpty()) {
											info.encodeName = new String(n);
										}

										String c = tmpLines[1].trim();

										if (null != c && !c.isEmpty()) {
											try {
												info.clockrate = Integer.parseInt(c);
											} catch (Exception e) {
												break;
											}

											// 解析成功，加入info
											this.getSdp().getM().getEncodeInfos().add(info);
										}
									}
								}
							}
						}

					} else if (str.contains("downloadspeed:")) {
						String v = str.substring("downloadspeed:".length());
						if (null != v && !v.isEmpty()) {
							try {
								this.getSdp().getM().setDlSpeed(Integer.parseInt(v));
							} catch (Exception e) {
								break;
							}
						}
					} else if (str.contains("filesize:")) {
						String v = str.substring("filesize:".length());
						if (null != v && !v.isEmpty()) {
							try {
								this.getSdp().getM().setFileSize(Long.parseLong(v));
							} catch (Exception e) {
								break;
							}
						}
					} else if (str.contains("setup:")) {
						String v = str.substring("setup:".length());
						if (null != v && !v.isEmpty()) {
							this.getSdp().getM().setSetup(v);
						}
					} else if (str.contains("connection:")) {
						String v = str.substring("connection:".length());
						if (null != v && !v.isEmpty()) {
							this.getSdp().getM().setConnection(v);
						}
					} else if (str.contains("svctime:")) {
						String v = str.substring("svctime:".length());
						if (null != v && !v.isEmpty()) {
							try {
								this.getSdp().getM().setSvctime(Integer.parseInt(v));
							} catch (Exception e) {
								break;
							}
						}
					} else if (str.contains("svcspace:")) {
						String v = str.substring("svcspace:".length());
						if (null != v && !v.isEmpty()) {
							try {
								this.getSdp().getM().setSvcspace(Integer.parseInt(v));
							} catch (Exception e) {
								break;
							}
						}
					} else if (str.contains("recvonly")) {
						this.getSdp().getM().setSendOrRecv(new String("recvonly"));
					} else if (str.contains("sendrecv")) {
						this.getSdp().getM().setSendOrRecv(new String("sendrecv"));
					} else if (str.contains("sendonly")) {
						this.getSdp().getM().setSendOrRecv(new String("sendonly"));
					}
				}
			}
		}
			break;

		case 'y': {
			if (lines.length > 1) {
				String str = lines[1].trim();
				if (null != str && !str.isEmpty())
					this.getSdp().getM().setY(str);
			}
		}
			break;

		case 'f': {
			if (lines.length > 1) {
				this.getSdp().getM().setMd(this.getSdp().getM().new MD());

				String str = lines[1].trim();
				if (null != str && !str.isEmpty()) {

					String v = str.substring(0, str.indexOf('a'));
					String a = str.substring(str.indexOf('a'));

					if (null != v && !v.isEmpty()) {
						char ch = str.charAt(0);
						if (ch != 'v') {
							break;
						}

						// must not ignore whitespace!
						String newLines[] = v.split("[/]", -1);

						if (newLines.length > 5) {
							this.getSdp().getM().getMd().setvEncode(GetInteger(newLines[1]));
							this.getSdp().getM().getMd().setvResolution(GetInteger(newLines[2]));
							this.getSdp().getM().getMd().setvFrameRate(GetInteger(newLines[3]));
							this.getSdp().getM().getMd().setvCodeRateType(GetInteger(newLines[4]));
							this.getSdp().getM().getMd().setvCodeRate(GetInteger(newLines[5]));
						}
					}

					if (null != a && !a.isEmpty()) {
						char ch = a.charAt(0);
						if (ch != 'a') {
							break;
						}

						// must not ignore whitespace!
						String newLines[] = a.split("[/]", -1);

						if (newLines.length > 3) {
							this.getSdp().getM().getMd().setaEncode(GetInteger(newLines[1]));
							this.getSdp().getM().getMd().setaCodeRate(GetInteger(newLines[2]));
							this.getSdp().getM().getMd().setaSampling(GetInteger(newLines[3]));
						}
					}
				}
			}
		}
			break;

		default:
			break;
		}
		return true;
	}

	private Integer GetInteger(String str) {
		Integer ret = null;

		if (null != str && !str.isEmpty()) {
			try {
				int tmp = Integer.parseInt(str.trim());
				ret = new Integer(tmp);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		return ret;
	}

	public Sdp getSdp() {
		return sdp;
	}

	public void setSdp(Sdp sdp) {
		this.sdp = sdp;
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
}
