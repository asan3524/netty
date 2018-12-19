package com.whayer.stream.service.rtsp;

import java.util.ArrayList;
import java.util.List;

public class Sdp {

	public Sdp() {
		this.s = new S();
		this.t = new T();
		this.m = new M(96);
	}

	private S s;
	private T t;
	private M m;

	public S getS() {
		return s;
	}

	public void setS(S s) {
		this.s = s;
	}

	public T getT() {
		return t;
	}

	public void setT(T t) {
		this.t = t;
	}

	public M getM() {
		return m;
	}

	public void setM(M m) {
		this.m = m;
	}

	public class S {
		// --- v line
		// 版本默认为0
		private String version = "0";
		// --- o line createId sessionId Version IN IP4 address
		private String createId = "-";
		// 会话ID，默认为0
		private String sessionId = "0";
		// 网络类型。默认为IN
		private String netType = "IN";
		// IP类型，取值IP4，IP6
		private String ipType = "IP4";
		// --- s line 会话名称(可选)
		// 在向Sip服务器和媒体流发送者或接受者之间的SIP消息中，取值.
		// Play: 实时点播
		// Playback:历史回放
		// Download:文件下载
		// Talk:语言对讲
		private String sessionName = "-";

		// --- u line 会话名称(可选)
		// 填写音视频文件的URI，一般在历史回放或文件下载使用
		// 取值分为简捷或普通方式，2选1.
		// 简单方式：deviceId:下载类型
		// 普通方式：http://uri
		private String fileUri = null;

		// --- c line 连接信息
		// 如果为空，填入o行中的IP地址
		// 否则格式为：IN ${ipType} ${cIp}
		private String cIp = null;

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getCreateId() {
			return createId;
		}

		public void setCreateId(String createId) {
			this.createId = createId;
		}

		public String getSessionId() {
			return sessionId;
		}

		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}

		public String getNetType() {
			return netType;
		}

		public void setNetType(String netType) {
			this.netType = netType;
		}

		public String getIpType() {
			return ipType;
		}

		public void setIpType(String ipType) {
			this.ipType = ipType;
		}

		public String getSessionName() {
			return sessionName;
		}

		public void setSessionName(String sessionName) {
			this.sessionName = sessionName;
		}

		public String getFileUri() {
			return fileUri;
		}

		public void setFileUri(String fileUri) {
			this.fileUri = fileUri;
		}

		public String getcIp() {
			return cIp;
		}

		public void setcIp(String cIp) {
			this.cIp = cIp;
		}
	}

	public class T {
		// --- t line
		// 在录像回放或音视频文件录制的时候起作用。
		private Long beginTime = 0L;
		private Long endTime = 0L;

		public Long getBeginTime() {
			return beginTime;
		}

		public void setBeginTime(Long beginTime) {
			this.beginTime = beginTime;
		}

		public Long getEndTime() {
			return endTime;
		}

		public void setEndTime(Long endTime) {
			this.endTime = endTime;
		}
	}

	public class M {

		public M(Integer payload) {
			// TODO Auto-generated constructor stub
			payloads.add(payload);
			encodeInfos.add(new E(payload));
		}

		// --- m line
		// 媒体类型，video表示视频，audio表示音频。
		private String media = "video";
		// 媒体端口
		private int port = 6000;
		// 传输层协议
		// "RTP/AVP" -- > UDP;"TCP/RTP/AVP" -- > TCP
		private String transportType = "RTP/AVP";
		// 负载
		// 0-95 IETF规定
		// 98 标准MPEG-4
		// 99 国家标准AVS-PS2
		// 100 标准H.264
		// 110 厂商自定义
		private List<Integer> payloads = new ArrayList<Integer>();

		// --- a line
		// 收发模式:recvonly/sendrecv/sendonly
		private String sendOrRecv = "sendonly";

		// a=rtpmap: payload encodeName/clockrate
		class E {
			public int payload = 0;
			public int clockrate = 90000;
			public String encodeName = "MP2P";

			public E(int payload, int clockrate, String encodeName) {
				super();
				this.payload = payload;
				this.clockrate = clockrate;
				this.encodeName = encodeName;
			}

			public E(int payload) {
				super();
				this.payload = payload;
			}

		}

		private List<E> encodeInfos = new ArrayList<E>();

		// a-->下载速度
		private Integer dlSpeed = null;
		// a-->文件大小
		private Long fileSize = null;
		// a-->TCP协商参数。
		// 例: a=setup:active或a=connection:new
		private String setup = null;
		private String connection = null;
		// a-->SVC参数 svcspace
		private Integer svcspace = null;
		// a-->SVC参数 svctime
		private Integer svctime = null;

		// --- y line
		// ssrc值，长度为10的数字字符串,格式:dddddddddd
		// 第一位,历史或实时标识，0为实时，1位历史
		// 第2到第6位，取值为20位SIP监控域ID的第4到第8位。
		// 第7到第10位，一个在当前域中存在的媒体流SSRC不重复的4位10进制随机数。
		// 注：当是平台内点播设备时，由平台产生SSRC值，传给设备。
		// 当是平台间点播媒体时，由被点播平台回复INVITE 200 OK产生SSRC值。
		private String y = "";

		private MD md = null;

		public String getMedia() {
			return media;
		}

		public void setMedia(String media) {
			this.media = media;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public String getTransportType() {
			return transportType;
		}

		public void setTransportType(String transportType) {
			this.transportType = transportType;
		}

		public List<Integer> getPayloads() {
			return payloads;
		}

		public void setPayloads(List<Integer> payloads) {
			this.payloads = payloads;
		}

		public String getSendOrRecv() {
			return sendOrRecv;
		}

		public void setSendOrRecv(String sendOrRecv) {
			this.sendOrRecv = sendOrRecv;
		}

		public List<E> getEncodeInfos() {
			return encodeInfos;
		}

		public void setEncodeInfos(List<E> encodeInfos) {
			this.encodeInfos = encodeInfos;
		}

		public Integer getDlSpeed() {
			return dlSpeed;
		}

		public void setDlSpeed(Integer dlSpeed) {
			this.dlSpeed = dlSpeed;
		}

		public Long getFileSize() {
			return fileSize;
		}

		public void setFileSize(Long fileSize) {
			this.fileSize = fileSize;
		}

		public String getSetup() {
			return setup;
		}

		public void setSetup(String setup) {
			this.setup = setup;
		}

		public String getConnection() {
			return connection;
		}

		public void setConnection(String connection) {
			this.connection = connection;
		}

		public Integer getSvcspace() {
			return svcspace;
		}

		public void setSvcspace(Integer svcspace) {
			this.svcspace = svcspace;
		}

		public Integer getSvctime() {
			return svctime;
		}

		public void setSvctime(Integer svctime) {
			this.svctime = svctime;
		}

		public String getY() {
			return y;
		}

		public void setY(String y) {
			this.y = y;
		}

		public MD getMd() {
			return md;
		}

		public void setMd(MD md) {
			this.md = md;
		}

		// --- f line 媒体描述
		class MD {
			// 视频参数
			// 编码格式
			private Integer vEncode = null;
			// 分辨率
			private Integer vResolution = null;
			// 帧率
			private Integer vFrameRate = null;
			// 码率类型
			private Integer vCodeRateType = null;
			// 码率大小
			private Integer vCodeRate = null;

			// 音频参数
			// 编码格式
			private Integer aEncode = null;
			// 码率大小
			private Integer aCodeRate = null;
			// 采样率
			private Integer aSampling = null;

			public MD() {

			}

			public MD(Integer vEncode, Integer vResolution, Integer vFrameRate, Integer vCodeRateType,
					Integer vCodeRate, Integer aEncode, Integer aCodeRate, Integer aSampling) {
				this.vEncode = vEncode;
				this.vResolution = vResolution;
				this.vFrameRate = vFrameRate;
				this.vCodeRateType = vCodeRateType;
				this.vCodeRate = vCodeRate;
				this.aEncode = aEncode;
				this.aCodeRate = aCodeRate;
				this.aSampling = aSampling;
			}

			public Integer getvEncode() {
				return vEncode;
			}

			public void setvEncode(Integer vEncode) {
				this.vEncode = vEncode;
			}

			public Integer getvResolution() {
				return vResolution;
			}

			public void setvResolution(Integer vResolution) {
				this.vResolution = vResolution;
			}

			public Integer getvFrameRate() {
				return vFrameRate;
			}

			public void setvFrameRate(Integer vFrameRate) {
				this.vFrameRate = vFrameRate;
			}

			public Integer getvCodeRateType() {
				return vCodeRateType;
			}

			public void setvCodeRateType(Integer vCodeRateType) {
				this.vCodeRateType = vCodeRateType;
			}

			public Integer getvCodeRate() {
				return vCodeRate;
			}

			public void setvCodeRate(Integer vCodeRate) {
				this.vCodeRate = vCodeRate;
			}

			public Integer getaEncode() {
				return aEncode;
			}

			public void setaEncode(Integer aEncode) {
				this.aEncode = aEncode;
			}

			public Integer getaCodeRate() {
				return aCodeRate;
			}

			public void setaCodeRate(Integer aCodeRate) {
				this.aCodeRate = aCodeRate;
			}

			public Integer getaSampling() {
				return aSampling;
			}

			public void setaSampling(Integer aSampling) {
				this.aSampling = aSampling;
			}
		}
	}
}
