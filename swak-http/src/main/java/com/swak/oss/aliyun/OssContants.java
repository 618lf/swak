package com.swak.oss.aliyun;

import java.util.Arrays;
import java.util.List;

/**
 * Oss 上传的一些常量
 * 
 * @author lifeng
 */
public interface OssContants {
	
	// headers
	String AUTHORIZATION = "Authorization";
	String CACHE_CONTROL = "Cache-Control";
	String CONTENT_DISPOSITION = "Content-Disposition";
	String CONTENT_ENCODING = "Content-Encoding";
	String CONTENT_LENGTH = "Content-Length";
	String CONTENT_MD5 = "Content-MD5";
	String CONTENT_TYPE = "Content-Type";
	String TRANSFER_ENCODING = "Transfer-Encoding";
	String DATE = "Date";
	String ETAG = "ETag";
	String EXPIRES = "Expires";
	String HOST = "Host";
	String LAST_MODIFIED = "Last-Modified";
	String RANGE = "Range";
	String LOCATION = "Location";
	String CONNECTION = "Connection";
	String OSS_PREFIX = "x-oss-";

	// Paramters
	String SUBRESOURCE_ACL = "acl";
	String SUBRESOURCE_REFERER = "referer";
	String SUBRESOURCE_LOCATION = "location";
	String SUBRESOURCE_LOGGING = "logging";
	String SUBRESOURCE_WEBSITE = "website";
	String SUBRESOURCE_LIFECYCLE = "lifecycle";
	String SUBRESOURCE_UPLOADS = "uploads";
	String SUBRESOURCE_DELETE = "delete";
	String SUBRESOURCE_CORS = "cors";
	String SUBRESOURCE_APPEND = "append";
	String SUBRESOURCE_TAGGING = "tagging";
	String SUBRESOURCE_IMG = "img";
	String SUBRESOURCE_STYLE = "style";
	String SUBRESOURCE_REPLICATION = "replication";
	String SUBRESOURCE_REPLICATION_PROGRESS = "replicationProgress";
	String SUBRESOURCE_REPLICATION_LOCATION = "replicationLocation";
	String SUBRESOURCE_CNAME = "cname";
	String SUBRESOURCE_BUCKET_INFO = "bucketInfo";
	String SUBRESOURCE_COMP = "comp";
	String SUBRESOURCE_OBJECTMETA = "objectMeta";
	String SUBRESOURCE_QOS = "qos";
	String SUBRESOURCE_LIVE = "live";
	String SUBRESOURCE_STATUS = "status";
	String SUBRESOURCE_VOD = "vod";
	String SUBRESOURCE_START_TIME = "startTime";
	String SUBRESOURCE_END_TIME = "endTime";
	String SUBRESOURCE_PROCESS_CONF = "processConfiguration";
	String SUBRESOURCE_PROCESS = "x-oss-process";
	String SUBRESOURCE_CSV_SELECT = "csv/select";
	String SUBRESOURCE_CSV_META = "csv/meta";
	String SUBRESOURCE_JSON_SELECT = "json/select";
	String SUBRESOURCE_JSON_META = "json/meta";
	String SUBRESOURCE_SQL = "sql";
	String SUBRESOURCE_SYMLINK = "symlink";
	String SUBRESOURCE_STAT = "stat";
	String SUBRESOURCE_RESTORE = "restore";

	String SUBRESOURCE_UDF = "udf";
	String SUBRESOURCE_UDF_NAME = "udfName";
	String SUBRESOURCE_UDF_IMAGE = "udfImage";
	String SUBRESOURCE_UDF_IMAGE_DESC = "udfImageDesc";
	String SUBRESOURCE_UDF_APPLICATION = "udfApplication";
	String SUBRESOURCE_UDF_LOG = "udfApplicationLog";

	String PREFIX = "prefix";
	String DELIMITER = "delimiter";
	String MARKER = "marker";
	String MAX_KEYS = "max-keys";
	String BID = "bid";
	String ENCODING_TYPE = "encoding-type";

	String UPLOAD_ID = "uploadId";
	String PART_NUMBER = "partNumber";
	String MAX_UPLOADS = "max-uploads";
	String UPLOAD_ID_MARKER = "upload-id-marker";
	String KEY_MARKER = "key-marker";
	String MAX_PARTS = "max-parts";
	String PART_NUMBER_MARKER = "part-number-marker";
	String RULE_ID = "rule-id";

	String SECURITY_TOKEN = "security-token";

	String POSITION = "position";
	String STYLE_NAME = "styleName";

	String COMP_ADD = "add";
	String COMP_DELETE = "delete";
	String COMP_CREATE = "create";
	String COMP_UPGRADE = "upgrade";
	String COMP_RESIZE = "resize";

	String STAT = "stat";
	String HISTORY = "history";
	String PLAYLIST_NAME = "playlistName";
	String SINCE = "since";
	String TAIL = "tail";

	/* V1 signature params */
	String SIGNATURE = "Signature";
	String OSS_ACCESS_KEY_ID = "OSSAccessKeyId";

	// response
	String RESPONSE_HEADER_CONTENT_TYPE = "response-content-type";
	String RESPONSE_HEADER_CONTENT_LANGUAGE = "response-content-language";
	String RESPONSE_HEADER_EXPIRES = "response-expires";
	String RESPONSE_HEADER_CACHE_CONTROL = "response-cache-control";
	String RESPONSE_HEADER_CONTENT_DISPOSITION = "response-content-disposition";
	String RESPONSE_HEADER_CONTENT_ENCODING = "response-content-encoding";

	// sign
	String AUTHORIZATION_PREFIX = "OSS ";
	String AUTHORIZATION_ACCESS_KEY_ID = "AccessKeyId";
	String AUTHORIZATION_ADDITIONAL_HEADERS = "AdditionalHeaders";
	String AUTHORIZATION_SIGNATURE = "Signature";
	String NEW_LINE = "\n";
	List<String> SIGNED_PARAMTERS = Arrays.asList(new String[] { SUBRESOURCE_ACL, SUBRESOURCE_UPLOADS,
			SUBRESOURCE_LOCATION, SUBRESOURCE_CORS, SUBRESOURCE_LOGGING, SUBRESOURCE_WEBSITE, SUBRESOURCE_REFERER,
			SUBRESOURCE_LIFECYCLE, SUBRESOURCE_DELETE, SUBRESOURCE_APPEND, SUBRESOURCE_TAGGING, SUBRESOURCE_OBJECTMETA,
			UPLOAD_ID, PART_NUMBER, SECURITY_TOKEN, POSITION, RESPONSE_HEADER_CACHE_CONTROL,
			RESPONSE_HEADER_CONTENT_DISPOSITION, RESPONSE_HEADER_CONTENT_ENCODING, RESPONSE_HEADER_CONTENT_LANGUAGE,
			RESPONSE_HEADER_CONTENT_TYPE, RESPONSE_HEADER_EXPIRES, SUBRESOURCE_IMG, SUBRESOURCE_STYLE, STYLE_NAME,
			SUBRESOURCE_REPLICATION, SUBRESOURCE_REPLICATION_PROGRESS, SUBRESOURCE_REPLICATION_LOCATION,
			SUBRESOURCE_CNAME, SUBRESOURCE_BUCKET_INFO, SUBRESOURCE_COMP, SUBRESOURCE_QOS, SUBRESOURCE_LIVE,
			SUBRESOURCE_STATUS, SUBRESOURCE_VOD, SUBRESOURCE_START_TIME, SUBRESOURCE_END_TIME, SUBRESOURCE_PROCESS,
			SUBRESOURCE_PROCESS_CONF, SUBRESOURCE_SYMLINK, SUBRESOURCE_STAT, SUBRESOURCE_UDF, SUBRESOURCE_UDF_NAME,
			SUBRESOURCE_UDF_IMAGE, SUBRESOURCE_UDF_IMAGE_DESC, SUBRESOURCE_UDF_APPLICATION, SUBRESOURCE_UDF_LOG,
			SUBRESOURCE_RESTORE, });
}
