package io.shardingsphere.core.metadata.datasource.dialect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.shardingsphere.core.exception.ShardingException;
import io.shardingsphere.core.metadata.datasource.DataSourceMetaData;

/**
 * 启动嵌入式的h2数据库
 * 
 * @author lifeng
 */
public class H2DataSourceMetaData implements DataSourceMetaData {
    
    private static final int DEFAULT_PORT = -1;
    
    private final String hostName;
    
    private final int port;
    
    private final String schemeName;
    
    private final Pattern pattern = Pattern.compile("jdbc:h2:(file|mem|~)[:](.+);?", Pattern.CASE_INSENSITIVE);
    
    public H2DataSourceMetaData(final String url) {
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            hostName = matcher.group(1);
            port = DEFAULT_PORT;
            schemeName = matcher.group(2);
        } else {
            throw new ShardingException("The URL of JDBC is not supported. Please refer to this pattern: %s.", pattern.pattern());
        }
    }
    
    @Override
    public boolean isInSameDatabaseInstance(final DataSourceMetaData dataSourceMetaData) {
        return hostName.equals(dataSourceMetaData.getHostName()) && port == dataSourceMetaData.getPort() && schemeName.equals(dataSourceMetaData.getSchemeName());
    }

	public String getHostName() {
		return hostName;
	}

	public int getPort() {
		return port;
	}

	public String getSchemeName() {
		return schemeName;
	}

	public Pattern getPattern() {
		return pattern;
	}
}