package com.swak.oss.aliyun;

import java.util.HashMap;
import java.util.Map;

import com.swak.utils.StringUtils;

import io.netty.util.AsciiString;

/**
 * 文件类型
 * 
 * @author lifeng
 */
public interface MimeType {

	// 支持的文件类型
	Map<String, String> MIME_TYPES = new HashMap<String, String>() {
		private static final long serialVersionUID = 1L;
		{
			put("mp3", "audio/mpeg");
			put("jpeg", "image/jpeg");
			put("jpg", "image/jpeg");
			put("png", "image/png");
			put("pdf", "application/pdf");
			put("mp4", "video/mp4");
		}
	};

	/**
	 * Get MimeType by file name
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getMimeType(String fileName) {
		if (StringUtils.isBlank(fileName)) {
			return null;
		}
		String ext = (fileName.substring(fileName.lastIndexOf('.') + 1)).toLowerCase();
		if (MIME_TYPES.containsKey(ext)) {
			return MIME_TYPES.get(ext);
		} else {
			return AsciiString.cached("application/octet-stream").toString();
		}
	}
}
