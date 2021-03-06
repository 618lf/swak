package com.sample.tools.config;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 日志
 * 
 * @author lifeng
 */
@XmlRootElement(name="l")
public class Log implements Serializable {
	private static final long serialVersionUID = 7422461311723323267L;
	private String name;
	private String remarks;
	private String time;
	@XmlElement(name = "n")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@XmlElement(name = "r")
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	@XmlElement(name = "t")
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
	/**
	 * 格式化
	 */
	public String format() {
		return Xmls.toXml(this) + "\r\n";
	}
	
	/**
	 * 创建日志
	 * @param name
	 * @param remarks
	 * @return
	 */
	public static Log newLog(String name, String remarks) {
		Log log = new Log();
		log.setName(name);
		log.setRemarks(remarks);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		log.setTime(df.format(new Date()));
		return log;
	}
	
	/**
	 * 解析
	 */
	public static Log parse(String xml) {
		return Xmls.fromXml(xml, Log.class);
	}
	
	// 显示的时候从高往低版本显示
	public static Comparator<Log> show = new Comparator<Log>() {
		@Override
		public int compare(Log o1, Log o2) {
			return o2.name.compareTo(o1.name);
		}
	};
}
