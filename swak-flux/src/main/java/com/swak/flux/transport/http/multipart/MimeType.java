package com.swak.flux.transport.http.multipart;

import java.util.HashMap;
import java.util.Map;

import com.swak.flux.transport.http.HttpConst;
import com.swak.utils.StringUtils;

/**
 * 文件类型
 * @author lifeng
 */
public interface MimeType {

	// 支持的文件类型
	Map<String, String> MIME_TYPES = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;

		{
			put("kar", "audio/midi");
			put("mid", "audio/midi");
			put("midi", "audio/midi");

			put("aac", "audio/mp4");
			put("f4a", "audio/mp4");
			put("f4b", "audio/mp4");
			put("m4a", "audio/mp4");

			put("mp3", "audio/mpeg");
			put("oga", "audio/ogg");
			put("ogg", "audio/ogg");
			put("opus", "audio/ogg");

			put("ra", "audio/x-realaudio");
			put("wav", "audio/x-wav");

			put("bmp", "image/bmp");
			put("gif", "image/gif");
			put("jpeg", "image/jpeg");
			put("jpg", "image/jpeg");

			put("png", "image/png");
			put("svg", "image/svg+xml");
			put("svgz", "image/svg+xml");

			put("tif", "image/tiff");
			put("tiff", "image/tiff");
			put("wbmp", "image/vnd.wap.wbmp");
			put("webp", "image/webp");
			put("ico", "image/x-icon");
			put("cur", "image/x-icon");
			put("jng", "image/x-jng");

			put("js", "application/javascript; charset=UTF-8");
			put("json", "application/json; charset=UTF-8");

			put("webapp", "application/x-web-app-manifest+json");
			put("manifest", "text/cache-manifest");
			put("appcache", "text/cache-manifest");

			put("doc", "application/msword");
			put("xls", "application/vnd.ms-excel");
			put("ppt", "application/vnd.ms-powerpoint");
			put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
			put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");

			put("3gpp", "video/3gpp");
			put("3gp", "video/3gpp");

			put("mp4", "video/mp4");
			put("m4v", "video/mp4");
			put("f4v", "video/mp4");
			put("f4p", "video/mp4");

			put("mpeg", "video/mpeg");
			put("mpg", "video/mpeg");

			put("ogv", "video/ogg");
			put("mov", "video/quicktime");
			put("webm", "video/webm");
			put("flv", "video/x-flv");
			put("mng", "video/x-mng");
			put("asx", "video/x-ms-asf");
			put("asf", "video/x-ms-asf");

			put("wmv", "video/x-ms-wmv");
			put("avi", "video/x-msvideo");

			put("atom", "application/xml; charset=UTF-8");
			put("rdf", "application/xml; charset=UTF-8");
			put("rss", "application/xml; charset=UTF-8");
			put("xml", "application/xml; charset=UTF-8");

			put("woff", "application/font-woff");
			put("woff2", "application/font-woff2");
			put("eot", "application/vnd.ms-fontobject");
			put("ttc", "application/x-font-ttf");
			put("ttf", "application/x-font-ttf");

			put("otf", "font/opentype");

			put("jar", "application/java-archive");
			put("war", "application/java-archive");
			put("ear", "application/java-archive");

			put("hqx", "application/mac-binhex40");
			put("pdf", "application/pdf");
			put("ps", "application/postscript");
			put("eps", "application/postscript");
			put("ai", "application/postscript");

			put("rtf", "application/rtf");
			put("wmlc", "application/vnd.wap.wmlc");
			put("xhtml", "application/xhtml+xml");
			put("kml", "application/vnd.google-earth.kml+xml");
			put("kmz", "application/vnd.google-earth.kmz");
			put("7z", "application/x-7z-compressed");
			put("crx", "application/x-chrome-extension");
			put("oex", "application/x-opera-extension");
			put("xpi", "application/x-xpinstall");
			put("cco", "application/x-cocoa");
			put("jardiff", "application/x-java-archive-diff");
			put("jnlp", "application/x-java-jnlp-file");
			put("run", "application/x-makeself");

			put("pl", "application/x-perl");
			put("pm", "application/x-perl");

			put("prc", "application/x-pilot");
			put("pdb", "application/x-pilot");

			put("rar", "application/x-rar-compressed");
			put("rpm", "application/x-redhat-package-manager");
			put("sea", "application/x-sea");
			put("swf", "application/x-shockwave-flash");
			put("sit", "application/x-stuffit");
			put("tcl", "application/x-tcl");
			put("tk", "application/x-tcl");

			put("der", "application/x-x509-ca-cert");
			put("pem", "application/x-x509-ca-cert");
			put("crt", "application/x-x509-ca-cert");

			put("torrent", "application/x-bittorrent");
			put("zip", "application/zip");

			put("bin", "application/octet-stream");
			put("exe", "application/octet-stream");
			put("dll", "application/octet-stream");

			put("deb", "application/octet-stream");
			put("dmg", "application/octet-stream");
			put("iso", "application/octet-stream");
			put("img", "application/octet-stream");

			put("msi", "application/octet-stream");
			put("msp", "application/octet-stream");
			put("msm", "application/octet-stream");

			put("safariextz", "application/octet-stream");

			put("css", "text/css; charset=UTF-8");
			put("html", "text/html; charset=UTF-8");
			put("htm", "text/html; charset=UTF-8");
			put("shtml", "text/html; charset=UTF-8");

			put("mml", "text/mathml");
			put("txt", "text/plain; charset=UTF-8");
			put("jad", "text/vnd.sun.j2me.app-descriptor");
			put("wml", "text/vnd.wap.wml");
			put("vtt", "text/vtt");
			put("htc", "text/x-component");
			put("vcf", "text/x-vcard");
		}
	};

	/**
	 * Get MimeType by file name
	 * 
	 * @param fileName
	 * @return
	 */
	public static CharSequence getMimeType(String fileName) {
		if (StringUtils.isBlank(fileName)) {
			return null;
		}
		String ext = (fileName.substring(fileName.lastIndexOf('.') + 1)).toLowerCase();
		if (MIME_TYPES.containsKey(ext)) {
			return MIME_TYPES.get(ext);
		} else {
			return HttpConst.APPLICATION_STREAM;
		}
	}
}
